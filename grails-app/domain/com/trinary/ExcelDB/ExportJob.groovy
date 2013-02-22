package com.trinary.ExcelDB

class ExportJob {
	String filename
	Integer step = 0
	Integer steps = 0
	
	Boolean done = false
	String status = ""

    static constraints = {
    }
}
