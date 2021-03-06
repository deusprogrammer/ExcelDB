package com.trinary.ExcelDB

class FailedJobController {

    def index() { }
    
    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        [failedJobInstanceList: FailedJob.list(params), failedJobInstanceTotal: FailedJob.count()]
    }
    
    def populate() {
        new FailedJob(fileLocation: "C:\\ftest\\test1.xlsx").save(flush: true)
        new FailedJob(fileLocation: "C:\\ftest\\test2.xls").save(flush: true)
        new FailedJob(fileLocation: "C:\\ftest\\test3.xls").save(flush: true)
        
        render "OK"
    }
    
    def pop() {
        def failed = FailedJob.list()
        
        if (failed) {
            if (failed.size() == 0) {
                redirect(controller: "excelJob", action: "list")
            }
            else {
                def fileLocation = failed[0].fileLocation
                failed[0].delete()

                redirect(controller: "excelMapping", action: "show", params: [file: fileLocation])
            }
        }
        else {
            redirect(controller: "excelJob", action: "list")
        }
    }
}
