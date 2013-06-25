package com.trinary.ExcelDB

class ExcelJob {
    String fileName
    String status
    Boolean done
    Integer step
    Integer nSteps

    ExcelJob() {
        println "Creating new job..."
        done = false
        status = ""
        nSteps = -1
        step = 0
    }

    static constraints = {
        fileName()
        status()
        step()
        nSteps()
    }
}
