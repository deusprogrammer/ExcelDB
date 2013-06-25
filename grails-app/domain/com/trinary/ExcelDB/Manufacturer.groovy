package com.trinary.ExcelDB

class Manufacturer {
    String manufacturerCode
    String manufacturerName

    static def hasMany = [products: Product]

    static constraints = {
        manufacturerCode nullable: true
        manufacturerName nullable: true
    }
}
