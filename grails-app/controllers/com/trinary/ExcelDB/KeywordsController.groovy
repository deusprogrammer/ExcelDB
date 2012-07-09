package com.trinary.ExcelDB

import org.springframework.dao.DataIntegrityViolationException

class KeywordsController {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    }

    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        [keywordsInstanceList: Keywords.list(params), keywordsInstanceTotal: Keywords.count()]
    }

    def create() {
        [keywordsInstance: new Keywords(params)]
    }

    def save() {
        def keywordsInstance = new Keywords(params)
        if (!keywordsInstance.save(flush: true)) {
            render(view: "create", model: [keywordsInstance: keywordsInstance])
            return
        }

		flash.message = message(code: 'default.created.message', args: [message(code: 'keywords.label', default: 'Keywords'), keywordsInstance.id])
        redirect(action: "show", id: keywordsInstance.id)
    }

    def show() {
        def keywordsInstance = Keywords.get(params.id)
        if (!keywordsInstance) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'keywords.label', default: 'Keywords'), params.id])
            redirect(action: "list")
            return
        }

        [keywordsInstance: keywordsInstance]
    }

    def edit() {
        def keywordsInstance = Keywords.get(params.id)
        if (!keywordsInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'keywords.label', default: 'Keywords'), params.id])
            redirect(action: "list")
            return
        }

        [keywordsInstance: keywordsInstance]
    }

    def update() {
        def keywordsInstance = Keywords.get(params.id)
        if (!keywordsInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'keywords.label', default: 'Keywords'), params.id])
            redirect(action: "list")
            return
        }

        if (params.version) {
            def version = params.version.toLong()
            if (keywordsInstance.version > version) {
                keywordsInstance.errors.rejectValue("version", "default.optimistic.locking.failure",
                          [message(code: 'keywords.label', default: 'Keywords')] as Object[],
                          "Another user has updated this Keywords while you were editing")
                render(view: "edit", model: [keywordsInstance: keywordsInstance])
                return
            }
        }

        keywordsInstance.properties = params

        if (!keywordsInstance.save(flush: true)) {
            render(view: "edit", model: [keywordsInstance: keywordsInstance])
            return
        }

		flash.message = message(code: 'default.updated.message', args: [message(code: 'keywords.label', default: 'Keywords'), keywordsInstance.id])
        redirect(action: "show", id: keywordsInstance.id)
    }

    def delete() {
        def keywordsInstance = Keywords.get(params.id)
        if (!keywordsInstance) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'keywords.label', default: 'Keywords'), params.id])
            redirect(action: "list")
            return
        }

        try {
            keywordsInstance.delete(flush: true)
			flash.message = message(code: 'default.deleted.message', args: [message(code: 'keywords.label', default: 'Keywords'), params.id])
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
			flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'keywords.label', default: 'Keywords'), params.id])
            redirect(action: "show", id: params.id)
        }
    }
}
