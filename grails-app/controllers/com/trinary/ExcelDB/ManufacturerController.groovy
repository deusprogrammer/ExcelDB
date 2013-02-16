package com.trinary.ExcelDB

import org.springframework.dao.DataIntegrityViolationException

class ManufacturerController {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    }

    def list(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        [manufacturerInstanceList: Manufacturer.list(params), manufacturerInstanceTotal: Manufacturer.count()]
    }

    def create() {
        [manufacturerInstance: new Manufacturer(params)]
    }

    def save() {
        def manufacturerInstance = new Manufacturer(params)
        if (!manufacturerInstance.save(flush: true)) {
            render(view: "create", model: [manufacturerInstance: manufacturerInstance])
            return
        }

        flash.message = message(code: 'default.created.message', args: [message(code: 'manufacturer.label', default: 'Manufacturer'), manufacturerInstance.id])
        redirect(action: "show", id: manufacturerInstance.id)
    }

    def show(Long id) {
        def manufacturerInstance = Manufacturer.get(id)
        if (!manufacturerInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'manufacturer.label', default: 'Manufacturer'), id])
            redirect(action: "list")
            return
        }

        [manufacturerInstance: manufacturerInstance]
    }

    def edit(Long id) {
        def manufacturerInstance = Manufacturer.get(id)
        if (!manufacturerInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'manufacturer.label', default: 'Manufacturer'), id])
            redirect(action: "list")
            return
        }

        [manufacturerInstance: manufacturerInstance]
    }

    def update(Long id, Long version) {
        def manufacturerInstance = Manufacturer.get(id)
        if (!manufacturerInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'manufacturer.label', default: 'Manufacturer'), id])
            redirect(action: "list")
            return
        }

        if (version != null) {
            if (manufacturerInstance.version > version) {
                manufacturerInstance.errors.rejectValue("version", "default.optimistic.locking.failure",
                          [message(code: 'manufacturer.label', default: 'Manufacturer')] as Object[],
                          "Another user has updated this Manufacturer while you were editing")
                render(view: "edit", model: [manufacturerInstance: manufacturerInstance])
                return
            }
        }

        manufacturerInstance.properties = params

        if (!manufacturerInstance.save(flush: true)) {
            render(view: "edit", model: [manufacturerInstance: manufacturerInstance])
            return
        }

        flash.message = message(code: 'default.updated.message', args: [message(code: 'manufacturer.label', default: 'Manufacturer'), manufacturerInstance.id])
        redirect(action: "list")
    }

    def delete(Long id) {
        def manufacturerInstance = Manufacturer.get(id)
        if (!manufacturerInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'manufacturer.label', default: 'Manufacturer'), id])
            redirect(action: "list")
            return
        }

        try {
            manufacturerInstance.delete(flush: true)
            flash.message = message(code: 'default.deleted.message', args: [message(code: 'manufacturer.label', default: 'Manufacturer'), id])
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'manufacturer.label', default: 'Manufacturer'), id])
            redirect(action: "show", id: id)
        }
    }
}
