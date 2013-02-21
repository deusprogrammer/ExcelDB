package com.trinary.ExcelDB

import org.springframework.dao.DataIntegrityViolationException

class ProductController {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]
    def ExcelService

    def index() {
        redirect(action: "list", params: params)
    }

    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        [productInstanceList: Product.list(params), productInstanceTotal: Product.count()]
    }
	
	def listInvalidEntries() {
		def offset = (params.offset ? params.offset : 0).toInteger()
		def max = Math.min(params.max ? params.int('max') : 10, 100).toInteger()
		
		def lower = offset 
		def upper = offset + max
		
		def missingCol1 = Product.findAllByProductNumber("")
		def missingCol2 = Product.findAllByProductDescription("")
		def missingCol3 = Product.findAllByProductPrice("")
		def allInvalid = missingCol1 + missingCol2 + missingCol3
		
		println "LOWER: ${lower}"
		println "UPPER: ${upper}"
		allInvalid = allInvalid.subList(lower, upper)
		
		render(view: "list", model: [productInstanceList: allInvalid, productInstanceTotal: allInvalid.size()])
	}

    def create() {
        [productInstance: new Product(params)]
    }

    def save() {
        def productInstance = new Product(params)
        if (!productInstance.save(flush: true)) {
            render(view: "create", model: [productInstance: productInstance])
            return
        }

        flash.message = message(code: 'default.created.message', args: [message(code: 'product.label', default: 'Product'), productInstance.id])
        redirect(action: "show", id: productInstance.id)
    }

    def show() {
        def productInstance = Product.get(params.id)
        if (!productInstance) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'product.label', default: 'Product'), params.id])
            redirect(action: "list")
            return
        }

        [productInstance: productInstance]
    }

    def edit() {
        def productInstance = Product.get(params.id)
        if (!productInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'product.label', default: 'Product'), params.id])
            redirect(action: "list")
            return
        }

        [productInstance: productInstance]
    }

    def update() {
        def productInstance = Product.get(params.id)
        if (!productInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'product.label', default: 'Product'), params.id])
            redirect(action: "list")
            return
        }

        if (params.version) {
            def version = params.version.toLong()
            if (productInstance.version > version) {
                productInstance.errors.rejectValue("version", "default.optimistic.locking.failure",
                          [message(code: 'product.label', default: 'Product')] as Object[],
                          "Another user has updated this Product while you were editing")
                render(view: "edit", model: [productInstance: productInstance])
                return
            }
        }

        productInstance.properties = params

        if (!productInstance.save(flush: true)) {
            render(view: "edit", model: [productInstance: productInstance])
            return
        }

		flash.message = message(code: 'default.updated.message', args: [message(code: 'product.label', default: 'Product'), productInstance.id])
        redirect(action: "show", id: productInstance.id)
    }

    def delete() {
        def productInstance = Product.get(params.id)
        if (!productInstance) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'product.label', default: 'Product'), params.id])
            redirect(action: "list")
            return
        }

        try {
            productInstance.delete(flush: true)
			flash.message = message(code: 'default.deleted.message', args: [message(code: 'product.label', default: 'Product'), params.id])
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
			flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'product.label', default: 'Product'), params.id])
            redirect(action: "show", id: params.id)
        }
    }
    
    def writeOut() {
		def filePath

		filePath = ExcelService.writeDBToFile()
		
		/*
		def state = State.findByKey("outdated")
		if (!state || state.value == "false") {
			state = State.findByKey("lastGenerated")
			filePath = state?.value
		} else {
			
		}
		*/
		
		if (!filePath) {
			response.status = 404
			return
		}
		
        def file = new File(filePath) 
        response.setHeader("Content-Type", "application/excel") 
        response.setHeader("Content-Disposition", "attachment; filename=${file.getName()}") 
        response.setHeader("Content-Length", "${file.size()}") 

        response.outputStream << file.newInputStream()
    }
}
