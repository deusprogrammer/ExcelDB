package com.trinary.ExcelDB

import com.lucastex.grails.fileuploader.UFile
import net.lingala.zip4j.core.ZipFile

class ZipController {
    def ExcelService
    
    def index() { }
    
    def success() {
        UFile file = UFile.findById(params.ufileId)
        def jobList = []
        def jobIds = []
        def jobs = []
        
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
            
            jobIds = ExcelService.processExcelFiles(jobList, file.name)
            
            jobIds.eachWithIndex { jobId, index ->
                def fileName = jobList[index]
                jobs += [file: jobList[index], id: jobId]
            }
        }
                
        [params:params, jobs: jobs]
    }
    
    def error() {
        println "ERROR"
    }
}
