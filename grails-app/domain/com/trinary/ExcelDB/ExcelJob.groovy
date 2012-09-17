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
    
    void setDone(def status) {
        done = true
        this.status = status
        this.step = nSteps
        
        this.save()
    }
    
    void incrementStep() {
        if (this.nSteps != -1 && this.step < this.nSteps) {
            println "Incrementing step"
            this.step++
            this.save(flush: true)
        }
    }

    static constraints = {
        fileName()
        status()
        step()
        nSteps()
    }
}
