package com.trinary.ExcelDB

import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.CellStyle
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.ss.usermodel.WorkbookFactory
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.hssf.usermodel.HSSFSheet
import org.apache.poi.hssf.usermodel.HSSFCell

import java.text.SimpleDateFormat

import org.codehaus.groovy.grails.commons.ConfigurationHolder as CH

class ExcelService {
    def backgroundService
    
    //DB Helper functions
    
    def setDone(def jobId, def message) {
        def job = ExcelJob.get(jobId)
        
        if (job) {
            synchronized(job) {
                job.lock()
                job.done = true
                job.status = message
                job.step = job.nSteps

                job.save(flush: true)
            }
        }        
    }
    
    def incrementStep(def jobId) {
        def job = ExcelJob.get(jobId)
        
        if (job) {
            synchronized(job) {
                job.lock()
                job.step++

                job.save(flush: true)
            }
        }        
    }
    
    def setSteps(def jobId, def nSteps) {
        def job = ExcelJob.get(jobId)
        
        if (job) {
            synchronized(job) {
                job.lock()
                job.nSteps = nSteps
                job.step = 0
                job.save(flush: true)
            }
        }        
    }
    
    def position(def s, def l) {
        for(def i = 0; i < l.size(); i++) {
            if (l[i].equals(s.toLowerCase())) {
                return i
            }
        }

        return -1
    }
    
    //Parsing Functions

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
        def productNumberLabels = Keywords.getKeywords("productNumber")
        
        return precedes(string, old, productNumberLabels)
    }
    
    def isProductDescription(def string, def old) {
        def productDescriptionLabels = Keywords.getKeywords("productDescription")
        
        return precedes(string, old, productDescriptionLabels)
    }
    
    def isProductPrice(def string, def old) {
        def productPriceLabels = Keywords.getKeywords("productPrice")

        return precedes(string, old, productPriceLabels)
    }
    
    //Excel Functions

    def processExcelFiles(def fileLocation, def columnMappings = null) throws Exception, FileNotFoundException, IOException {
        def jobId
        def manu = getManufacturer(fileLocation)
        def failedFiles = []
        Workbook workbook = WorkbookFactory.create(new FileInputStream(fileLocation))
        Sheet sheet
        ExcelJob job = new ExcelJob()
                
        job.fileName = fileLocation
        job.save(flush: true)
        
        jobId = job.id

        def sheetNumber = 0
        
        println "MANUFACTURER: ${manu}"

        backgroundService.execute ("Job ${job.id}", {
            Product p

            println "PROCESSING: ${fileLocation}\nJOBID: ${job.id}"

            def columnVotes = [productNumber: -1, productDescription: -1, productPrice: -1]
            def columnLabels = [productNumber: "", productDescription: "", productPrice: ""]
            def labelFound = false
            def sheetFound = false
            def dataStartIndex = -1

            if (!columnMappings) {
                //Row
                for (def n = 0; n < workbook.getNumberOfSheets() && !sheetFound; n++) {
                    sheet = workbook.getSheetAt(n)
                    println "*******SHEET ${n}*******"
                    for (def i = 0; i < sheet.getLastRowNum(); i++) {
                        Row row
                        def rowIsLabel = false
                        def cellCount = 0
                        def emptyCount = 0

                        try {
                            row = sheet.getRow(i)
                            cellCount = row.getLastCellNum()
                        }
                        catch (Exception e) {
                            println "Row is non existent..."
                            continue
                        }

                        //Cell
                        for (def j = 0; j < row.getLastCellNum(); j++) {
                            Cell cell = row.getCell(j)
                            def cellString = cell.toString().trim()
                            println "CELL DATA (${i},${j}): ${cellString}"
                            try {
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
                            catch (Exception e) {
                                emptyCount++
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
                                sheetNumber = n
                                sheetFound = true
                                if (dataStartIndex == -1) {
                                    dataStartIndex = i
                                    //println "Data starts at line ${dataStartIndex}"
                                }
                                break
                            }
                            //Data has been found before any kind of label
                            //Parse columns with regex to determine types
                            else {
                                println "\t\tRow appears to contain data"
                                println "\t\tNo rules found...assuming failure"
                                break
                            }
                        }
                    }
                }
            }
            else {
                columnVotes["productNumber"] = columnMappings["productNumber"]
                columnVotes["productDescription"] = columnMappings["productDescription"]
                columnVotes["productPrice"] = columnMappings["productPrice"]
                sheetNumber = Integer.parseInt(columnMappings["sheet"])
                dataStartIndex = Integer.parseInt(columnMappings["row"])
            }

            println "COLUMNS ARE"
            println "\tProduct Number:      "  + columnVotes["productNumber"]
            println "\tProduct Description: "  + columnVotes["productDescription"]
            println "\tProduct Price:       "  + columnVotes["productPrice"]

            if (columnVotes["productNumber"] == -1 || columnVotes["productDescription"] == -1 || columnVotes["productPrice"] == -1) {
                println "Unable to identify all columns!"
                setDone(jobId, "Failed")

                def failure = new FailedJob(fileLocation: fileLocation)
                failure.save(flush: true)

                return
            }

            sheet = workbook.getSheetAt(sheetNumber)
            setSteps(jobId, sheet.getLastRowNum())
            
            println "SHEET:      ${sheetNumber}"
            println "DATA START: ${dataStartIndex}"

            //Add excel file to database
            for (def i = dataStartIndex; i < sheet.getLastRowNum(); i++) {
                Row row
                def rowIsLabel = false
                def cellCount
                def emptyCount = 0

                try {
                    row = sheet.getRow(i)
                    cellCount = row.getLastCellNum()
                }
                catch (Exception e) {
                    println "Row is non existent..."
                    continue
                }

                //Check each cell to determine if the row is empty (mostly)
                for (def j = 0; j < row.getLastCellNum(); j++) {
                    Cell cell = row.getCell(j)
                    def cellString = cell.toString()

                    try {
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
                    catch (Exception e) {
                        emptyCount++
                    }
                }

                if ((cellCount - emptyCount) >= 3 && !rowIsLabel) {
                    String productNumber = row.getCell(columnVotes["productNumber"]).toString()
                    println "ROW: ${i}"
                    println "ADDING PRODUCT NUMBER ${productNumber}"
                    
                    p = Product.findByProductNumberAndProductVendor(productNumber, manu)
                    
                    if (!p) {
                        println "Unable to find existing product.  Adding new one."
                        p = new Product()
                    }
                    
                    p.productNumber = productNumber
                    p.productPrice = row.getCell(columnVotes["productPrice"]).toString()
                    p.productDescription = row.getCell(columnVotes["productDescription"]).toString()
                    p.productVendor = manu
                    p.save(flush: true)
                }

                incrementStep(jobId)
            }
            incrementStep(jobId)
            setDone(jobId, "Success")
        })
        
        return jobId
    }
    
    def writeDBToFile() {
        def products = Product.list()
        
        def workbook = new HSSFWorkbook()
        def sheet = workbook.createSheet()
        
        //Write header
        def row = sheet.createRow(0)
        row.createCell(0).setCellValue("Product Number")
        row.createCell(1).setCellValue("Product Description")
        row.createCell(2).setCellValue("Product Price")
        
        products.eachWithIndex { product, i ->
            row = sheet.createRow(i + 1)
            def productNumber = product.productVendor + "-" + product.productNumber
            println "PRODUCT NUMBER:      ${productNumber}"
            println "PRODUCT DESCRIPTION: ${productNumber}"
            println "PRODUCT PRICE:       ${productNumber}"
            row.createCell(0).setCellValue(productNumber)
            row.createCell(1).setCellValue(product.productDescription)
            row.createCell(2).setCellValue(product.productPrice)
        }
        
        sheet.autoSizeColumn(0)
        sheet.autoSizeColumn(1)
        sheet.autoSizeColumn(2)
        
        def date = Calendar.getInstance()
        def sdf = new SimpleDateFormat("yyyyMMddHHmmss")
        def time = sdf.format(date.getTime())
        
        def filePath = CH.config.excel.root + time + ".xls"
        def fileOut = new FileOutputStream(filePath)
        workbook.write(fileOut)
        fileOut.close()
        
        return filePath
    }
    
    
    def getManufacturer(def filePath) {
        def pattern = /([A-Z]{3})-.*/
        def fileName = filePath.split("/")[-1]
        def match = fileName =~ pattern
        if (match) {
            return match[0][1]
        }
        return {
            return "UNK"
        }
    }
}