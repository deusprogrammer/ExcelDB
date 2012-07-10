package com.trinary.ExcelDB

import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.CellStyle
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.ss.usermodel.WorkbookFactory

class ExcelService {
    def backgroundService
    
    def position(def s, def l) {
        for(def i = 0; i < l.size(); i++) {
            if (l[i].equals(s.toLowerCase())) {
                return i
            }
        }

        return -1
    }

    def precedes (def s1, def s2, def l) {
        def s1Pos = position(s1, l)
        def s2Pos = position(s2, l)
        
        if (s2.equals("") && s1Pos != -1)
            return true

        if (s1Pos == -1 || s2Pos == -1)
            return false

        if (s1Pos < s2Pos) {
            return true
        }
        else if (s2Pos >= s1Pos) {
            return false
        }
    }
    
    ExcelLabel getLabelType(def string, def old = null) {
        if (!old) {
            old = [productNumber: "", productDescription: "", productPrice: ""]
        }
        
        //println "OLD IS ${old}"
        
        if (isProductNumber(string, old["productNumber"])) {
            return ExcelLabel.PRODUCT_NUMBER
        }
        else if (isProductDescription(string, old["productDescription"])) {
            return ExcelLabel.PRODUCT_DESCRIPTION
        }
        else if (isProductPrice(string, old["productPrice"])) {
            return ExcelLabel.PRODUCT_PRICE
        }
        else {
            return ExcelLabel.UNKNOWN
        }
    }
    
    def isProductNumber(def string, def old) {
        //def productNumberLabels = ["part", "part number", "product number", "product id", " product name"]
        def productNumberLabels = Keywords.getKeywords("productNumber")
        
        //if (productNumberLabels.contains(string.toLowerCase())) {
        return precedes(string, old, productNumberLabels)
    }
    
    def isProductDescription(def string, def old) {
        //def productDescriptionLabels = ["description", "desc", "product description", "product information"]
        def productDescriptionLabels = Keywords.getKeywords("productDescription")
        
        //if (productDescriptionLabels.contains(string.toLowerCase())) {
        return precedes(string, old, productDescriptionLabels)
    }
    
    def isProductPrice(def string, def old) {
        //def productPriceLabels = ["price", "authorized", "authorized price", "msrp", "retail price"]
        def productPriceLabels = Keywords.getKeywords("productPrice")
        
        //println "\tOLD IS ${old}"
        //println "\tSTR IS ${string}"
        
        //if (productPriceLabels.contains(string.toLowerCase())) {
        return precedes(string, old, productPriceLabels)
    }

    def processExcelFiles(def fileLocations, def jobName) throws Exception, FileNotFoundException, IOException {
        def rowSum = 0
        fileLocations.each { fileLocation ->
            //println "FILE LOCATION: ${fileLocation}"
            Workbook workbook = WorkbookFactory.create(new FileInputStream(fileLocation))
            Sheet sheet = workbook.getSheetAt(0)
            rowSum += sheet.getLastRowNum()
        }
        
        ExcelJob job = new ExcelJob()
        job.fileName = jobName
        job.nSteps = rowSum
        job.save(flush: true)
        
        def failedFiles = []
                
        backgroundService.execute ("Job ${job.id}", {
            Product p
                        
            //println "ROWS: ${rowSum}"

            //Files
            for (int k = 0; k < fileLocations.size(); k++) { 
                def fileLocation = fileLocations[k]
                
                //println "PROCESSING: ${fileLocation}"
                Workbook workbook = WorkbookFactory.create(new FileInputStream(fileLocation))
                Sheet sheet = workbook.getSheetAt(0)

                def columnVotes = [productNumber: -1, productDescription: -1, productPrice: -1]
                def columnLabels = [productNumber: "", productDescription: "", productPrice: ""]
                def labelFound = false
                def dataStartIndex = -1

                //Row
                for (def i = 0; i < sheet.getLastRowNum(); i++) {
                    Row row = sheet.getRow(i)

                    def rowIsLabel = false
                    def cellCount = row.getLastCellNum()
                    def emptyCount = 0

                    //Cell
                    for (def j = 0; j < row.getLastCellNum(); j++) {
                        Cell cell = row.getCell(j)
                        def cellString = cell.toString()

                        switch (cell.getCellType()) {
                            case Cell.CELL_TYPE_STRING:
                                switch (getLabelType(cellString, columnLabels)) {
                                    case ExcelLabel.PRODUCT_NUMBER:
                                        rowIsLabel = true
                                        columnVotes["productNumber"] = j
                                        columnLabels["productNumber"] = cellString
                                        break
                                    case ExcelLabel.PRODUCT_DESCRIPTION:
                                        rowIsLabel = true
                                        columnVotes["productDescription"] = j
                                        columnLabels["productDescription"] = cellString
                                        break
                                    case ExcelLabel.PRODUCT_PRICE:
                                        rowIsLabel = true
                                        columnVotes["productPrice"] = j
                                        columnLabels["productPrice"] = cellString
                                        break
                                }
                                break
                            case Cell.CELL_TYPE_NUMERIC:
                                break
                            case Cell.CELL_TYPE_BLANK:
                                emptyCount++
                                break
                            default:
                                break
                        }
                    }

                    //Determine facts about the line
                    if ((cellCount - emptyCount) < 4 && !rowIsLabel) {
                        //Throw away
                        println "\t\tRow is mostly empty...discarding"
                    }
                    else if (rowIsLabel) {
                        //Set columns
                        labelFound = true
                        println "\t\tRow appears to be a label"
                    }
                    else {
                        //Jump out of loop.  Column types are known
                        if (labelFound) {
                            println "Columns are identified"
                            if (dataStartIndex == -1) {
                                dataStartIndex = i
                                //println "Data starts at line ${dataStartIndex}"
                            }
                            break;
                        }
                        //Data has been found before any kind of label
                        //Parse columns with regex to determine types
                        else {
                            println "\t\tRow appears to contain data"
                        }
                    }
                }

                println "COLUMNS ARE"
                println "\tProduct Number:      "  + columnVotes["productNumber"]
                println "\tProduct Description: "  + columnVotes["productDescription"]
                println "\tProduct Price:       "  + columnVotes["productPrice"]

                if (columnVotes["productNumber"] == -1 || columnVotes["productDescription"] == -1 || columnVotes["productPrice"] == -1) {
                    println "Unable to identify all columns!"
                    job.setDone("Failed")
                    failedFiles += fileLocation
                    continue
                }

                //Add excel file to database
                for (def i = dataStartIndex; i < sheet.getLastRowNum(); i++) {
                    Row row = sheet.getRow(i)
                    def rowIsLabel = false
                    def cellCount = row.getLastCellNum()
                    def emptyCount = 0

                    //Check each cell to determine if the row is empty (mostly)
                    for (def j = 0; j < row.getLastCellNum(); j++) {
                        Cell cell = row.getCell(j)
                        def cellString = cell.toString()

                        switch (cell.getCellType()) {
                            case Cell.CELL_TYPE_STRING:
                                switch (getLabelType(cellString)) {
                                    case ExcelLabel.PRODUCT_NUMBER:
                                    case ExcelLabel.PRODUCT_DESCRIPTION:
                                    case ExcelLabel.PRODUCT_PRICE:
                                        rowIsLabel = true
                                        break
                                }
                                break
                            case Cell.CELL_TYPE_BLANK:
                                emptyCount++
                                break
                            default:
                                break
                        }
                    }

                    if ((cellCount - emptyCount) >= 3 && !rowIsLabel) {
                        String productNumber = row.getCell(columnVotes["productNumber"]).toString()
                        println "ADDING PRODUCT NUMBER ${productNumber}"

                        p = new Product()
                        p.productNumber = productNumber
                        p.productPrice = row.getCell(columnVotes["productPrice"]).toString()
                        p.productDescription = row.getCell(columnVotes["productDescription"]).toString()
                        p.productVendor = "Default"
                        p.save(flush: true)
                    }

                    job.incrementStep()
                }
                job.incrementStep()
            }
            
            job.setDone("Success")
        })
        
        return job.id
    }
}
