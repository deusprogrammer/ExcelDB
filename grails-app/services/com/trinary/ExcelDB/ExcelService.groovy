package com.trinary.ExcelDB

import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.CellStyle
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.ss.usermodel.WorkbookFactory

import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.apache.poi.xssf.usermodel.XSSFSheet
import org.apache.poi.xssf.usermodel.XSSFCell

import org.apache.poi.xssf.streaming.SXSSFWorkbook
import org.apache.poi.xssf.streaming.SXSSFSheet
import org.apache.poi.xssf.streaming.SXSSFCell

import java.text.SimpleDateFormat

class ExcelService {
    def grailsApplication

    //DB Helper functions
    protected Object lock = new Object()

    def setExportDone(def jobId, def message) {
        def job = ExportJob.get(jobId)

        if (job) {
            synchronized(job) {
                job.lock()
                job.done = true
                job.status = message
                job.step = job.steps

                job.save(flush: true)
            }
        }
    }

    def incrementExportStep(def jobId) {
        def job = ExportJob.get(jobId)

        try {
            if (job) {
                synchronized(job) {
                    job.lock()
                    job.step++

                    job.save(flush: true)
                }
            }
        } catch (Exception e) {
            println "FAILED TO INCREMENT STEP!"
        }
    }

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

        try {
            if (job) {
                synchronized(job) {
                    job.lock()
                    job.step++

                    job.save(flush: true)
                }
            }
        } catch (Exception e) {
            println "FAILED TO INCREMENT STEP!"
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

        runAsync {
            try {
                manu = getManufacturer(fileLocation)
                println "MANUFACTURER: ${manu}"

                Product p

                println "PROCESSING: ${fileLocation}\nJOBID: ${job.id}"

                def manufacturer
                def columnVotes = [productNumber: -1, productDescription: -1, productPrice: -1, productManufacturer: -1]
                def columnLabels = [productNumber: "", productDescription: "", productPrice: ""]
                def labelFound = false
                def sheetFound = false
                def dataStartIndex = -1

                if (columnMappings["productName"] == null) {
                    columnVotes["productName"] = columnMappings["productNumber"]
                } else {
                    columnVotes["productName"] = columnMappings["productName"]
                }

                if (columnMappings["productManufacturer"] != null) {
                    columnVotes["productManufacturer"] = columnMappings["productManufacturer"]
                }

                if (columnMappings["productNumber"] == null || columnMappings["productDescription"] == null || columnMappings["productPrice"] == null || columnMappings["row"] == null || columnMappings["sheet"] == null) {
                    println "COLUMNS ARE"
                    println "\tProduct Number:      "  + columnMappings["productNumber"]
                    println "\tProduct Name:        "  + columnMappings["productName"]
                    println "\tProduct Description: "  + columnMappings["productDescription"]
                    println "\tProduct Price:       "  + columnMappings["productPrice"]
                    println "FAILED!"
                    setDone(jobId, "Failure")
                    return
                }


                columnVotes["productNumber"] = columnMappings["productNumber"]
                columnVotes["productDescription"] = columnMappings["productDescription"]
                columnVotes["productPrice"] = columnMappings["productPrice"]
                sheetNumber = Integer.parseInt(columnMappings["sheet"])
                dataStartIndex = Integer.parseInt(columnMappings["row"])

                println "COLUMNS ARE"
                println "\tProduct Number:      "  + columnVotes["productNumber"]
                println "\tProduct Name:        "  + columnVotes["productName"]
                println "\tProduct Description: "  + columnVotes["productDescription"]
                println "\tProduct Price:       "  + columnVotes["productPrice"]

                sheet = workbook.getSheetAt(sheetNumber)
                setSteps(jobId, sheet.getLastRowNum())

                println "SHEET:      ${sheetNumber}"
                println "DATA START: ${dataStartIndex}"

                if (columnVotes["productManufacturer"] == -1) {
                    manufacturer = Manufacturer.findByManufacturerCode(manu)

                    if (!manufacturer) {
                        manufacturer = new Manufacturer(manufacturerCode: manu)
                        manufacturer.save()
                    }
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

                        def productPrice       = row.getCell(columnVotes["productPrice"]).toString().replace('$', '')
                        def productName        = row.getCell(columnVotes["productName"]).toString()
                        def productDescription = row.getCell(columnVotes["productDescription"]).toString()
                        def productManufacturer

                        if (!productNumber || productNumber == "" || !productDescription || productDescription == "" || !productPrice || productPrice == "" || !productPrice.isNumber()) {
                            continue
                        }

                        try {
                            synchronized (lock) {
                                p = Product.findByOemProductNumber(productNumber)
                                Manufacturer m

                                if (!p) {
                                    println "Unable to find existing product.  Adding new one."
                                    p = new Product()
                                }

                                p.oemProductNumber = productNumber
                                p.productPrice = fixPrice(productPrice)
                                p.productName  = productName
                                p.productDescription = productDescription

                                if (columnVotes["productManufacturer"] == -1) {
                                    println "No column mapping for manufacturer.  Using ${manufacturer.manufacturerCode}"

                                    m = manufacturer
                                } else {
                                    productManufacturer = row.getCell(columnMappings["productManufacturer"]).toString()
                                    def newManufacturer = Manufacturer.findByManufacturerName(productManufacturer)

                                    if (!newManufacturer) {
                                        println "Creating new manufacturer ${productManufacturer}"
                                        def found = false
                                        def abrev = productManufacturer.replaceAll(" ", "")[0..2].toUpperCase()
                                        def tmp = abrev
                                        def index = 1
                                        while (Manufacturer.findByManufacturerCode(tmp)) {
                                            tmp = "${abrev}${index++}"
                                        }
                                        abrev = tmp
                                        newManufacturer = new Manufacturer(manufacturerName: productManufacturer, manufacturerCode: abrev)
                                        newManufacturer.save(flush: true)
                                    }

                                    m = newManufacturer
                                }

                                p.manufacturer = m

                                if (!p.save(flush: true)) {
                                    println "ERRORS:"
                                    p.errors.each {
                                        println "\t${it}"
                                    }
                                }

                                m.addToProducts(p)
                                m.save(flush: true)

                                //Create product number
                                def itemNumber = String.format('%010d', m.products.size())
                                def abrev = "UNK"

                                if (m.manufacturerCode) {
                                    abrev = m.manufacturerCode
                                } else if (m.manufacturerName && m.manufacturerName.length() >= 3) {
                                    abrev = m.manufacturerName[0..2].toUpperCase()
                                }

                                p.productNumber = "${abrev}-${itemNumber}"

                                if (!p.save(flush: true)) {
                                    println "ERRORS:"
                                    p.errors.each {
                                        println "\t${it}"
                                    }
                                }
                            }
                        } catch (Exception e) {
                            println "EXCEPTION: " + e.getMessage()
                        }
                    }

                    incrementStep(jobId)
                }
                incrementStep(jobId)
                setDone(jobId, "Success")
            } catch (Exception e) {
                println "EXCEPTION: ${e.getMessage()}"
            }
        }

        return jobId
    }

    def writeDBToFile() {
        def manufacturers = Manufacturer.list()
        def products = Product.list()
        def markupValue = ExcelDBConfig.findByConfigKey("markupPercentage")
        def markup

        def date = Calendar.getInstance()
        def sdf = new SimpleDateFormat("yyyyMMddHHmmss")
        def time = sdf.format(date.getTime())

        def filePath = grailsApplication.config.excel.root + time + ".xlsx"

        def exportJob = new ExportJob(step: 0, steps: Product.count(), filename: filePath)
        exportJob.save(flush: true)
        def exportJobId = exportJob.id

        runAsync {
            if (markupValue) {
                markup = markupValue.configValue.toDouble()
            }

            def workbook = new SXSSFWorkbook()
            def sheet = workbook.createSheet()

            //Write header
            def row = sheet.createRow(0)
            row.createCell(0).setCellValue("PART_NUMBER")
            row.createCell(1).setCellValue("PART_NAME")
            row.createCell(2).setCellValue("PRICE")
            row.createCell(3).setCellValue("UNIT_OF_ISSUE")
            row.createCell(4).setCellValue("ITEMS_PER_UOI")
            row.createCell(5).setCellValue("OEM_NAME")
            row.createCell(6).setCellValue("OEM_PART_NO")
            row.createCell(7).setCellValue("DESCRIPTION")
            row.createCell(8).setCellValue("DAYS ARO")

            def i = 1

            try {
                manufacturers.each { manufacturer ->
                    println "MANUFACTURER: ${manufacturer.manufacturerName}"
                    manufacturer.products.each { product ->
                        row = sheet.createRow(i++)
                        def productNumber = product.productNumber
                        def productManufacturer = product.manufacturer.manufacturerName
                        def productPrice = (product.productPrice.toString().toDouble() * (1.0 + markup)).round(2)

                        println "\tWRITING PRODUCT: ${product.productNumber} (${product.oemProductNumber})"

                        row.createCell(0).setCellValue(productNumber)
                        row.createCell(1).setCellValue(product.productName)
                        row.createCell(2).setCellValue("\$" + productPrice)
                        row.createCell(3).setCellValue("EA")
                        row.createCell(4).setCellValue("1")
                        row.createCell(5).setCellValue(productManufacturer)
                        row.createCell(6).setCellValue(product.oemProductNumber)
                        row.createCell(7).setCellValue(productManufacturer + "- " + product.productDescription)
                        row.createCell(8).setCellValue("14")
                        incrementExportStep(exportJobId)
                    }
                }
            } catch (Exception e) {
                println "EXCEPTION: ${e.getMessage()}"
                setExportDone(exportJobId, "Failed")
                return
            }

            sheet.autoSizeColumn(0)
            sheet.autoSizeColumn(1)
            sheet.autoSizeColumn(2)
            sheet.autoSizeColumn(3)
            sheet.autoSizeColumn(4)
            sheet.autoSizeColumn(5)
            sheet.autoSizeColumn(6)
            sheet.autoSizeColumn(7)
            sheet.autoSizeColumn(8)

            try {
                def fileOut = new FileOutputStream(filePath)
                workbook.write(fileOut)
                fileOut.close()
            } catch (Exception e) {
                println "Unable to write out file!"
                println "EXCEPTION: ${e.getMessage()}"
                setExportDone(exportJobId, "Failed")
            }

            setExportDone(exportJobId, "Success")
        }
    }

    def fixPrice(def price) {
        def dPrice = price.toString().toDouble().round(2)
        return dPrice.toString()
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