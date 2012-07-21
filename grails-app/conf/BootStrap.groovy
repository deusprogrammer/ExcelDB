import com.trinary.ExcelDB.Keywords
import com.trinary.ExcelDB.Rules

class BootStrap {

    def init = { servletContext ->
        Keywords productNumberKeywords = new Keywords(columnName: "productNumber", keywords: "part, part number, product number, product id, catalog_no, catalog number, item number, product")
        Keywords productDescriptionKeywords = new Keywords(columnName: "productDescription", keywords: "name, product name, description, desc, product description, product information, catalog_desc, catalog description")
        Keywords productPriceKeywords = new Keywords(columnName: "productPrice", keywords: "price, p plus price, authorized, authorized price, msrp, retail price, list price, cat \$")
        productNumberKeywords.save()
        productDescriptionKeywords.save()
        productPriceKeywords.save()
    }
    def destroy = {
    }
}
