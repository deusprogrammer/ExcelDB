package com.trinary.ExcelDB

import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.CellStyle
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.ss.usermodel.WorkbookFactory

class ExcelService {
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
        def productDescriptionLabels = ["description", "product description", "product information"]
        
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

    def parseLocalExcelFile(def fileLocation) throws Exception, FileNotFoundException, IOException {
        def columnVotes = [productNumber: -1, productDescription: -1, productPrice: -1]
        
        def ret = "See console for output."
        def labelFound = false

        Workbook workbook = WorkbookFactory.create(new FileInputStream(fileLocation))
        Sheet sheet = workbook.getSheetAt(0)
        
        println "Number of Sheets: " + workbook.getNumberOfSheets()
        println "Number of Rows: " + sheet.getLastRowNum()

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
                    break;
                }
                //Data has been found before any kind of label
                //Parse columns with regex to determine types
                else {
                    println "\t\tRow appears to contain data"
                }
                
            }
        }
        
        ret = "COLUMNS ARE\n"
        ret += "\tProduct Number:      "  + columnVotes["productNumber"] + "\n"
        ret += "\tProduct Description: "  + columnVotes["productDescription"] + "\n"
        ret += "\tProduct Price:       "  + columnVotes["productPrice"] + "\n"
        
        return ret
    }
}
