package com.trinary.ExcelDB

class Rules {
    String columnName
    String rules

    static constraints = {
        columnName unique: true
    }
}
