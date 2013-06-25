package com.trinary.ExcelDB

class Role {

    String authority

    static mapping = {
        cache true
    }

    String toString() {
        return authority
    }

    static constraints = {
        authority blank: false, unique: true
    }
}
