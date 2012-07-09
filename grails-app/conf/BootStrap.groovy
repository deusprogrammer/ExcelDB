import com.trinary.ExcelDB.Keywords
import com.trinary.ExcelDB.Rules

class BootStrap {

    def init = { servletContext ->
        Keywords productNumberKeywords = new Keywords(columnName: "productNumber", keywords: "part, part number, product number, product id")
        Keywords productDescriptionKeywords = new Keywords(columnName: "productDescription", keywords: "name, product name, description, desc, product description, product information")
        Keywords productPriceKeywords = new Keywords(columnName: "productPrice", keywords: "price, authorized, authorized price, msrp, retail price")
        productNumberKeywords.save()
        productDescriptionKeywords.save()
        productPriceKeywords.save()
    }
    def destroy = {
    }
}
