package com.trinary.ExcelDB

import com.lucastex.grails.fileuploader.UFile
import net.lingala.zip4j.core.ZipFile
import org.codehaus.groovy.grails.commons.ConfigurationHolder as CH

class ZipController {
    def ExcelService
    
    def index() { }
    
    def success() {
        UFile file = UFile.findById(params.ufileId)
        def jobList = []
        def jobIds = []
        def jobs = []
        
        if (file) {
            def rootPath = CH.config.excel.zip
            def path = rootPath + (new Date()).getTime()
            
            //Unzip the zip file
            ZipFile zip = new ZipFile(file.path)
            zip.extractAll(path)
            
            //Read the contents of the directory
            def baseDir = new File(path)
            baseDir.eachFileMatch (~/.*.(xls|xlsx)/) { ent ->
                jobList << ent.toString()
            }
            
            jobList.each { job ->
                job = job.replaceAll("\\\\", "/")
                println "JOB: ${job}"
                jobIds += ExcelService.processExcelFiles(job)             
            }
            
            jobIds.eachWithIndex { jobId, index ->
                def fileName = jobList[index]
                jobs += [file: jobList[index], id: jobId]
            }
        }
                
        //[params:params, jobs: jobs]
        redirect(controller: "excelJob", action: "list")
    }
    
    def error() {
        println "ZipController ERROR"
    }
}
