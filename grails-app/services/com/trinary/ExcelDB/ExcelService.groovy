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
    
    //Excel Functions

    def processExcelFiles(def fileLocation, def columnMappings = null) throws Exception, FileNotFoundException, IOException {
		ExcelJob job = new ExcelJob()
		
		job.fileName = fileLocation
		job.save(flush: true)

		def jobId = job.id
		
		Sheet sheet
		Workbook workbook
		def manu
		
		try {
	        manu = getManufacturer(fileLocation)
	        workbook = WorkbookFactory.create(new FileInputStream(fileLocation))
	        
		} catch (Exception e) {
			println "EXCEPTION: ${e.getMessage()}"
			job.status = "Failed"
			job.step = 1
			job.nSteps = 1
			job.save(flush: true)
			return jobId
		}

        def sheetNumber = 0
        
        println "MANUFACTURER: ${manu}"
		
		def manufacturer = Manufacturer.findByManufacturerCode(manu)
		
		if (!manufacturer) {
			manufacturer = new Manufacturer(manufacturerCode: manu)
			manufacturer.save()
		}

        runAsync {
            Product p

            println "PROCESSING: ${fileLocation}\nJOBID: ${job.id}"

            def columnVotes = [productNumber: -1, productDescription: -1, productPrice: -1]
            def columnLabels = [productNumber: "", productDescription: "", productPrice: ""]
            def labelFound = false
            def sheetFound = false
            def dataStartIndex = -1

            columnVotes["productNumber"] = columnMappings["productNumber"]
            columnVotes["productDescription"] = columnMappings["productDescription"]
            columnVotes["productPrice"] = columnMappings["productPrice"]
            sheetNumber = Integer.parseInt(columnMappings["sheet"])
            dataStartIndex = Integer.parseInt(columnMappings["row"])

            println "COLUMNS ARE"
            println "\tProduct Number:      "  + columnVotes["productNumber"]
            println "\tProduct Description: "  + columnVotes["productDescription"]
            println "\tProduct Price:       "  + columnVotes["productPrice"]

            sheet = workbook.getSheetAt(sheetNumber)
            setSteps(jobId, sheet.getLastRowNum())
            
            println "SHEET:      ${sheetNumber}"
            println "DATA START: ${dataStartIndex}"
			
			def state = State.findByKey("outdated")
			
			if (state) {
				state.value = "true"
			}

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

                if ((cellCount - emptyCount) >= 3) {
                    String productNumber = row.getCell(columnVotes["productNumber"]).toString()
                    println "ROW: ${i}"
                    println "ADDING PRODUCT NUMBER ${productNumber}"
                    
                    p = Product.findByProductNumberAndProductVendor(productNumber, manu)
                    
                    if (!p) {
                        println "Unable to find existing product.  Adding new one."
                        p = new Product()
                    }
					
					def productPrice       = row.getCell(columnVotes["productPrice"]).toString()
					def productDescription = row.getCell(columnVotes["productDescription"]).toString()
                    
                    p.productNumber = productNumber
                    p.productPrice = row.getCell(columnVotes["productPrice"]).toString()
                    p.productDescription = row.getCell(columnVotes["productDescription"]).toString()
                    p.productVendor = manu
					
					if (!productNumber || productNumber == "" || !productPrice || productPrice == "" || !productPrice.replace('$', '').isNumber()) {
						continue	
					}
					
                    p.save(flush: true)
                }

                incrementStep(jobId)
            }
            incrementStep(jobId)
            setDone(jobId, "Success")
        }
        
        return jobId
    }
    
    def writeDBToFile() {
        def products = Product.list()
        def markupValue = ExcelDBConfig.findByConfigKey("markupPercentage")
        def markup
        
        if (markupValue) {
            markup = markupValue.configValue.toDouble()
        }
        
        def workbook = new HSSFWorkbook()
        def sheet = workbook.createSheet()
        
        //Write header
        def row = sheet.createRow(0)
        row.createCell(0).setCellValue("Product Number")
        row.createCell(1).setCellValue("Product Description")
        row.createCell(2).setCellValue("Product Price")
		row.createCell(3).setCellValue("Product Vendor")
        
        products.eachWithIndex { product, i ->
            row = sheet.createRow(i + 1)
            def productNumber = product.productVendor + "-" + product.productNumber
			def productManufacturer = Manufacturer.findByManufacturerCode(product.productVendor)

            row.createCell(0).setCellValue(productNumber)
            row.createCell(1).setCellValue(product.productDescription)
            def productPrice = product.productPrice * markup
            def fixedProductPrice = fixPrice(productPrice)
            if (!productPrice.equals(fixedProductPrice)) {
                product.productPrice = fixedProductPrice
                product.save()
            }
            row.createCell(2).setCellValue(fixedProductPrice)
			row.createCell(3).setCellValue(productManufacturer.manufacturerName)
        }
        
        sheet.autoSizeColumn(0)
        sheet.autoSizeColumn(1)
        sheet.autoSizeColumn(2)
		sheet.autoSizeColumn(3)
        
        def date = Calendar.getInstance()
        def sdf = new SimpleDateFormat("yyyyMMddHHmmss")
        def time = sdf.format(date.getTime())
        
        def filePath = CH.config.excel.root + time + ".xls"
        def fileOut = new FileOutputStream(filePath)
        workbook.write(fileOut)
        fileOut.close()
		
		def state = State.findByKey("outdated")
		if (state) {
			state.value = "false"
		}
		
		state = State.findByKey("lastGenerated")
		if (state) {
			state.value = filePath
		}
        
        return filePath
    }
    
    def fixPrice(def price) {
        def pattern = /([0-9.]+)/
        def match = price =~ pattern
        if (match) {
            return match[0][1]
        }
        else {
            return "0.00"
        }
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