package com.trinary.ExcelDB

import com.lucastex.grails.fileuploader.UFile

class ExcelController {
    def index() { }
    
    def success() {
        UFile file = UFile.findById(params.ufileId)
        def jobs = []
                
        if (file) {
			def pendingJob = new PendingJob(fileLocation: file.path)
			pendingJob.save()
        }

        redirect(uri: "/")
    }
    
    def error() {
        println "ExcelController ERROR!"
    }
}
