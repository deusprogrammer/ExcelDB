package com.trinary.ExcelDB

import com.lucastex.grails.fileuploader.UFile

class ExcelController {
    def ExcelService
    
    def index() { }
    
    def success() {
        UFile file = UFile.findById(params.ufileId)
        def jobs = []
                
        if (file) {
            def job = [:]
            job["id"] = ExcelService.processExcelFiles(file.path)
            job["file"] = file.path
            jobs += job
        }
        
        //[jobs: jobs]
        redirect(controller: "excelJob", action: "list")
    }
    
    def error() {
        println "ExcelController ERROR!"
    }
}
