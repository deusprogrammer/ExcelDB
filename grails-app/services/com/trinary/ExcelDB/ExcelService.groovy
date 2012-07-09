package com.trinary.ExcelDB

import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.CellStyle
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.ss.usermodel.WorkbookFactory

class ExcelService {
    def backgroundService
    
    ExcelLabel getLabelType(def string) {
        if (isProductNumber(string)) {
            return ExcelLabel.PRODUCT_NUMBER
        }
        else if (isProductDescription(string)) {
            return ExcelLabel.PRODUCT_DESCRIPTION
        }
        else if (isProductPrice(string)) {
            return ExcelLabel.PRODUCT_PRICE
        }
        else {
            return ExcelLabel.UNKNOWN
        }
    }
    
    def isProductNumber(def string) {
        def productNumberLabels = ["part", "part number", "product number", "product id", " product name"]
        
        if (productNumberLabels.contains(string.toLowerCase())) {
            return true
        }
        else {
            return false
        }
    }
    
    def isProductDescription(def string) {
        def productDescriptionLabels = ["description", "desc", "product description", "product information"]
        
        if (productDescriptionLabels.contains(string.toLowerCase())) {
            return true
        }
        else {
            return false
        }
    }
    
    def isProductPrice(def string) {
        def productPriceLabels = ["price", "authorized", "authorized price", "msrp", "retail price"]
        
        if (productPriceLabels.contains(string.toLowerCase())) {
            return true
        }
        else {
            return false
        }
    }

    def processExcelFiles(def fileLocations, def jobName) throws Exception, FileNotFoundException, IOException {
        def rowSum = 0
        fileLocations.each { fileLocation ->
            println "FILE LOCATION: ${fileLocation}"
            Workbook workbook = WorkbookFactory.create(new FileInputStream(fileLocation))
            Sheet sheet = workbook.getSheetAt(0)
            rowSum += sheet.getLastRowNum()
        }
        
        ExcelJob job = new ExcelJob()
        job.fileName = jobName
        job.nSteps = rowSum
        job.save(flush: true)
                
        backgroundService.execute ("Job ${job.id}", {
            Product p
            
            println "ROWS: ${rowSum}"

            fileLocations.each { fileLocation ->
                println "PROCESSING: ${fileLocation}"
                Workbook workbook = WorkbookFactory.create(new FileInputStream(fileLocation))
                Sheet sheet = workbook.getSheetAt(0)

                def columnVotes = [productNumber: -1, productDescription: -1, productPrice: -1]
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
                                switch (getLabelType(cellString)) {
                                    case ExcelLabel.PRODUCT_NUMBER:
                                        rowIsLabel = true
                                        columnVotes["productNumber"] = j
                                        break
                                    case ExcelLabel.PRODUCT_DESCRIPTION:
                                        rowIsLabel = true
                                        columnVotes["productDescription"] = j
                                        break
                                    case ExcelLabel.PRODUCT_PRICE:
                                        rowIsLabel = true
                                        columnVotes["productPrice"] = j
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
                                println "Data starts at line ${dataStartIndex}"
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
                    return -1
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
            }
            
            job.setDone("Success")
        })
        
        return job.id
    }
}
