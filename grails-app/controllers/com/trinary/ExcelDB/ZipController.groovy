package com.trinary.ExcelDB

import com.lucastex.grails.fileuploader.UFile
import net.lingala.zip4j.core.ZipFile

class ZipController {
    def ExcelService
    
    def index() { }
    
    def success() {
        UFile file = UFile.findById(params.ufileId)
        def jobList = []
        def jobId = -1
        
        if (file) {
            def path = "C:/tmp/unzipped/${(new Date()).getTime()}"
            
            //Unzip the zip file
            ZipFile zip = new ZipFile(file.path)
            zip.extractAll(path)
            
            //Read the contents of the directory
            def baseDir = new File(path)
            baseDir.eachFileMatch (~/.*.(xls|xlsx)/) { ent ->
                jobList << ent
            }
            
            jobId = ExcelService.processExcelFiles(jobList, file.name)
        }
                
        [params:params, jobs: [[file: file.name, id: jobId]], files: jobList]
    }
    
    def error() {
        println "ERROR"
    }
}
