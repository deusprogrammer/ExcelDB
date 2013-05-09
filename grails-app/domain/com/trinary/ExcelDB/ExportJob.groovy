package com.trinary.ExcelDB

import java.util.Date;

class ExportJob {
	String filename
	Integer step = 0
	Integer steps = 0
	Date dateCreated
	
	Boolean done = false
	String status = ""

    static constraints = {
    }
}
