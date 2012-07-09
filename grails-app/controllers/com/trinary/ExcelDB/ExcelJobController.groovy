package com.trinary.ExcelDB

import org.springframework.dao.DataIntegrityViolationException

class ExcelJobController {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    }

    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        [excelJobInstanceList: ExcelJob.list(params), excelJobInstanceTotal: ExcelJob.count()]
    }

    def create() {
        [excelJobInstance: new ExcelJob(params)]
    }

    def save() {
        def excelJobInstance = new ExcelJob(params)
        if (!excelJobInstance.save(flush: true)) {
            render(view: "create", model: [excelJobInstance: excelJobInstance])
            return
        }

		flash.message = message(code: 'default.created.message', args: [message(code: 'excelJob.label', default: 'ExcelJob'), excelJobInstance.id])
        redirect(action: "show", id: excelJobInstance.id)
    }

    def show() {
        def excelJobInstance = ExcelJob.get(params.id)
        if (!excelJobInstance) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'excelJob.label', default: 'ExcelJob'), params.id])
            redirect(action: "list")
            return
        }

        [excelJobInstance: excelJobInstance]
    }

    def edit() {
        def excelJobInstance = ExcelJob.get(params.id)
        if (!excelJobInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'excelJob.label', default: 'ExcelJob'), params.id])
            redirect(action: "list")
            return
        }

        [excelJobInstance: excelJobInstance]
    }

    def update() {
        def excelJobInstance = ExcelJob.get(params.id)
        if (!excelJobInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'excelJob.label', default: 'ExcelJob'), params.id])
            redirect(action: "list")
            return
        }

        if (params.version) {
            def version = params.version.toLong()
            if (excelJobInstance.version > version) {
                excelJobInstance.errors.rejectValue("version", "default.optimistic.locking.failure",
                          [message(code: 'excelJob.label', default: 'ExcelJob')] as Object[],
                          "Another user has updated this ExcelJob while you were editing")
                render(view: "edit", model: [excelJobInstance: excelJobInstance])
                return
            }
        }

        excelJobInstance.properties = params

        if (!excelJobInstance.save(flush: true)) {
            render(view: "edit", model: [excelJobInstance: excelJobInstance])
            return
        }

		flash.message = message(code: 'default.updated.message', args: [message(code: 'excelJob.label', default: 'ExcelJob'), excelJobInstance.id])
        redirect(action: "show", id: excelJobInstance.id)
    }

    def delete() {
        def excelJobInstance = ExcelJob.get(params.id)
        if (!excelJobInstance) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'excelJob.label', default: 'ExcelJob'), params.id])
            redirect(action: "list")
            return
        }

        try {
            excelJobInstance.delete(flush: true)
			flash.message = message(code: 'default.deleted.message', args: [message(code: 'excelJob.label', default: 'ExcelJob'), params.id])
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
			flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'excelJob.label', default: 'ExcelJob'), params.id])
            redirect(action: "show", id: params.id)
        }
    }
}
