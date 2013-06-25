package com.trinary.ExcelDB

class Product {
    String productNumber
    String oemProductNumber
    String productName
    String productDescription
    String productPrice

    static def belongsTo = [manufacturer: Manufacturer]

    Product() {
        //println "New Product being added!"
    }

    def beforeSave() {
    }

    def beforeInsert() {
        beforeSave()
    }

    def beforeUpdate() {
        beforeSave()
    }

    static constraints = {
        productNumber nullable: true
    }

    static mapping = {
        productDescription type: 'text'
        manufacturer lazy: false
    }
}
