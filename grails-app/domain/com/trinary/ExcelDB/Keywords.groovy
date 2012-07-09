package com.trinary.ExcelDB

class Keywords {
    String columnName
    String keywords
    
    static def getKeywords(def columnName) {
        def words = Keywords.findByColumnName(columnName)
        
        if (words) {
            def l = []

            words.keywords.split(",").each {
                l += it.trim()
            }

            return l
        }
        else {
            return null
        }
    }

    static constraints = {
        columnName unique: true
    }
}
