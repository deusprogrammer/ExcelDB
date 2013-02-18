package com.trinary.ExcelDB

class User {
    String username
    String password
    String salt

    def beforeSave = {
        def temp
        
        salt = new Date().getTime()
        temp = password + "-" + salt
        password = temp.encodeAsMD5()
    }
    
    def beforeInsert = {beforeSave()}
    def beforeUpdate = {beforeSave()}
    
    static constraints = {
		salt nullable: true
    }
}
