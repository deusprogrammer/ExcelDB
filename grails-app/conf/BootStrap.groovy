import com.trinary.ExcelDB.Keywords
import com.trinary.ExcelDB.Rules
import com.trinary.ExcelDB.ExcelDBConfig
import com.trinary.ExcelDB.User

class BootStrap {

    def init = { servletContext ->
        if (!Keywords.findByColumnName("productNumber")) {
            new Keywords(columnName: "productNumber", keywords: "part, part number, product number, product id, catalog_no, catalog number, item number, product").save()
        }
        if (!Keywords.findByColumnName("productDescription")) {
            new Keywords(columnName: "productDescription", keywords: "name, product name, description, desc, product description, product information, catalog_desc, catalog description").save()
        }
        if (!Keywords.findByColumnName("productPrice")) {
            new Keywords(columnName: "productPrice", keywords: "price, p plus price, authorized, authorized price, msrp, retail price, list price, cat \$").save()
        }
        
        if (!ExcelDBConfig.findByConfigKey("markupPercentage")) {
            new ExcelDBConfig(configKey: "markupPercentage", configValue: "0.0").save()
        }
        
        if (!User.findByUsername("admin")) {
            new User(username: "admin", password: "admin", salt: "").save()
        }
    }
    def destroy = {
    }
}
