package com.trinary.ExcelDB

class Manufacturer {
	String manufacturerCode
	String manufacturerName

    static constraints = {
		manufacturerName nullable: true
    }
}
