package com.trinary.ExcelDB

import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.CellStyle
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.ss.usermodel.WorkbookFactory

class ExcelMappingController {

    def index() {
        if (!params["file"]) {
            return []
        }
        
        def fileLocation = params["file"]
        def table = []
                
        //println "PROCESSING: ${fileLocation}\nJOBID: ${job.id}"
        Workbook workbook = WorkbookFactory.create(new FileInputStream(fileLocation))
        Sheet sheet = workbook.getSheetAt(0)

        def columnLabels = [productNumber: "", productDescription: "", productPrice: ""]
        def labelFound = false
        def dataStartIndex = -1

        //Row
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
                continue
            }

            //Cell
            for (def j = 0; j < row.getLastCellNum(); j++) {
                Cell cell = row.getCell(j)
                def cellString = cell.toString().trim()
                try {
                    switch (cell.getCellType()) {
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
            if ((cellCount - emptyCount) >= 4) {
                if (dataStartIndex == -1) {
                    dataStartIndex = i
                    //println "Data starts at line ${dataStartIndex}"
                }
            }
        }

        def rowNumber = 0
        def width = 0
        //Add excel file to database
        for (def i = dataStartIndex; i < dataStartIndex + 10; i++) {
            Row row
            def rowIsLabel = false
            def cellCount
            def emptyCount = 0
                    
            try {
                row = sheet.getRow(i)
                cellCount = row.getLastCellNum()
            }
            catch (Exception e) {
                //println "Row is non existent..."
                continue
            }

            //Check each cell to determine if the row is empty (mostly)
            for (def j = 0; j < row.getLastCellNum(); j++) {
                Cell cell = row.getCell(j)
                def cellString = cell.toString()

                try {
                    switch (cell.getCellType()) {
                    case Cell.CELL_TYPE_STRING:
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

            def thisWidth = 0
            //If the row is data, print it.
            if ((cellCount - emptyCount) >= 3) {
                def cells = []
                for (def j = 0; j < row.getLastCellNum(); j++) {
                    cells += row.getCell(j).toString()
                    //println "CELLS: ${cells}"
                    thisWidth++
                }
                table[rowNumber++] = cells
                //println "TABLE: ${table}"
            }
            
            if (thisWidth > width) {
                width = thisWidth
            }
        }

        println "WIDTH: ${width}"
        println "TABLE: ${table}"
        
        [table: table, width: width]
    }
    
    def map() {
        render params["colhead"]
    }
}