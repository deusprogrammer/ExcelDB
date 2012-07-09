package com.trinary.ExcelDB

import org.springframework.dao.DataIntegrityViolationException

class RulesController {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    }

    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        [rulesInstanceList: Rules.list(params), rulesInstanceTotal: Rules.count()]
    }

    def create() {
        [rulesInstance: new Rules(params)]
    }

    def save() {
        def rulesInstance = new Rules(params)
        if (!rulesInstance.save(flush: true)) {
            render(view: "create", model: [rulesInstance: rulesInstance])
            return
        }

		flash.message = message(code: 'default.created.message', args: [message(code: 'rules.label', default: 'Rules'), rulesInstance.id])
        redirect(action: "show", id: rulesInstance.id)
    }

    def show() {
        def rulesInstance = Rules.get(params.id)
        if (!rulesInstance) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'rules.label', default: 'Rules'), params.id])
            redirect(action: "list")
            return
        }

        [rulesInstance: rulesInstance]
    }

    def edit() {
        def rulesInstance = Rules.get(params.id)
        if (!rulesInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'rules.label', default: 'Rules'), params.id])
            redirect(action: "list")
            return
        }

        [rulesInstance: rulesInstance]
    }

    def update() {
        def rulesInstance = Rules.get(params.id)
        if (!rulesInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'rules.label', default: 'Rules'), params.id])
            redirect(action: "list")
            return
        }

        if (params.version) {
            def version = params.version.toLong()
            if (rulesInstance.version > version) {
                rulesInstance.errors.rejectValue("version", "default.optimistic.locking.failure",
                          [message(code: 'rules.label', default: 'Rules')] as Object[],
                          "Another user has updated this Rules while you were editing")
                render(view: "edit", model: [rulesInstance: rulesInstance])
                return
            }
        }

        rulesInstance.properties = params

        if (!rulesInstance.save(flush: true)) {
            render(view: "edit", model: [rulesInstance: rulesInstance])
            return
        }

		flash.message = message(code: 'default.updated.message', args: [message(code: 'rules.label', default: 'Rules'), rulesInstance.id])
        redirect(action: "show", id: rulesInstance.id)
    }

    def delete() {
        def rulesInstance = Rules.get(params.id)
        if (!rulesInstance) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'rules.label', default: 'Rules'), params.id])
            redirect(action: "list")
            return
        }

        try {
            rulesInstance.delete(flush: true)
			flash.message = message(code: 'default.deleted.message', args: [message(code: 'rules.label', default: 'Rules'), params.id])
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
			flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'rules.label', default: 'Rules'), params.id])
            redirect(action: "show", id: params.id)
        }
    }
}
