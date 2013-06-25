package com.trinary.ExcelDB

import org.springframework.dao.DataIntegrityViolationException
import grails.plugins.springsecurity.Secured

@Secured(['ROLE_ADMIN'])
class ExcelDBConfigController {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    }

    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        [excelDBConfigInstanceList: ExcelDBConfig.list(params), excelDBConfigInstanceTotal: ExcelDBConfig.count()]
    }

    def create() {
        [excelDBConfigInstance: new ExcelDBConfig(params)]
    }

    def save() {
        def excelDBConfigInstance = new ExcelDBConfig(params)
        if (!excelDBConfigInstance.save(flush: true)) {
            render(view: "create", model: [excelDBConfigInstance: excelDBConfigInstance])
            return
        }

        flash.message = message(code: 'default.created.message', args: [message(code: 'excelDBConfig.label', default: 'ExcelDBConfig'), excelDBConfigInstance.id])
        redirect(action: "show", id: excelDBConfigInstance.id)
    }

    def show() {
        def excelDBConfigInstance = ExcelDBConfig.get(params.id)
        if (!excelDBConfigInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'excelDBConfig.label', default: 'ExcelDBConfig'), params.id])
            redirect(action: "list")
            return
        }

        [excelDBConfigInstance: excelDBConfigInstance]
    }

    def edit() {
        def excelDBConfigInstance = ExcelDBConfig.get(params.id)
        if (!excelDBConfigInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'excelDBConfig.label', default: 'ExcelDBConfig'), params.id])
            redirect(action: "list")
            return
        }

        [excelDBConfigInstance: excelDBConfigInstance]
    }

    def update() {
        def excelDBConfigInstance = ExcelDBConfig.get(params.id)
        if (!excelDBConfigInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'excelDBConfig.label', default: 'ExcelDBConfig'), params.id])
            redirect(action: "list")
            return
        }

        if (params.version) {
            def version = params.version.toLong()
            if (excelDBConfigInstance.version > version) {
                excelDBConfigInstance.errors.rejectValue("version", "default.optimistic.locking.failure",
                          [message(code: 'excelDBConfig.label', default: 'ExcelDBConfig')] as Object[],
                          "Another user has updated this ExcelDBConfig while you were editing")
                render(view: "edit", model: [excelDBConfigInstance: excelDBConfigInstance])
                return
            }
        }

        excelDBConfigInstance.properties = params

        if (!excelDBConfigInstance.save(flush: true)) {
            render(view: "edit", model: [excelDBConfigInstance: excelDBConfigInstance])
            return
        }

        flash.message = message(code: 'default.updated.message', args: [message(code: 'excelDBConfig.label', default: 'ExcelDBConfig'), excelDBConfigInstance.id])
        redirect(action: "show", id: excelDBConfigInstance.id)
    }

    def delete() {
        def excelDBConfigInstance = ExcelDBConfig.get(params.id)
        if (!excelDBConfigInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'excelDBConfig.label', default: 'ExcelDBConfig'), params.id])
            redirect(action: "list")
            return
        }

        try {
            excelDBConfigInstance.delete(flush: true)
            flash.message = message(code: 'default.deleted.message', args: [message(code: 'excelDBConfig.label', default: 'ExcelDBConfig'), params.id])
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'excelDBConfig.label', default: 'ExcelDBConfig'), params.id])
            redirect(action: "show", id: params.id)
        }
    }
}
