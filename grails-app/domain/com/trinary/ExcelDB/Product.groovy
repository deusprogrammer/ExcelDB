package com.trinary.ExcelDB

class Product {
    String productNumber
    String productDescription
    String productVendor
    String productPrice
    
    Product() {
        println "New Product being added!"
        productVendor = "Default"
    }
    
    def afterSave() {
        println "PRODUCT SAVED: "
        println "\tnumber:      " + productNumber
        println "\tdescription: " + productDescription
        println "\tvendor:      " + productVendor
        println "\tprice:       " + productPrice
    }
    
    def afterInsert() {
        afterSave()
    }
    
    def afterUpdate() {
        afterSave()
    }

    static constraints = {
    }
}
