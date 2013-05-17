package com.trinary.ExcelDB

import org.springframework.dao.DataIntegrityViolationException
import grails.plugins.springsecurity.Secured

@Secured(['ROLE_USER', 'ROLE_ADMIN'])
class ExportJobController {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    }

    def list(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        [exportJobInstanceList: ExportJob.list(params), exportJobInstanceTotal: ExportJob.count()]
    }

    def create() {
        [exportJobInstance: new ExportJob(params)]
    }

    def save() {
        def exportJobInstance = new ExportJob(params)
        if (!exportJobInstance.save(flush: true)) {
            render(view: "create", model: [exportJobInstance: exportJobInstance])
            return
        }

        flash.message = message(code: 'default.created.message', args: [message(code: 'exportJob.label', default: 'ExportJob'), exportJobInstance.id])
        redirect(action: "show", id: exportJobInstance.id)
    }

    def show(Long id) {
        def exportJobInstance = ExportJob.get(id)
        if (!exportJobInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'exportJob.label', default: 'ExportJob'), id])
            redirect(action: "list")
            return
        }

        [exportJobInstance: exportJobInstance]
    }

    def edit(Long id) {
        def exportJobInstance = ExportJob.get(id)
        if (!exportJobInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'exportJob.label', default: 'ExportJob'), id])
            redirect(action: "list")
            return
        }

        [exportJobInstance: exportJobInstance]
    }

    def update(Long id, Long version) {
        def exportJobInstance = ExportJob.get(id)
        if (!exportJobInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'exportJob.label', default: 'ExportJob'), id])
            redirect(action: "list")
            return
        }

        if (version != null) {
            if (exportJobInstance.version > version) {
                exportJobInstance.errors.rejectValue("version", "default.optimistic.locking.failure",
                          [message(code: 'exportJob.label', default: 'ExportJob')] as Object[],
                          "Another user has updated this ExportJob while you were editing")
                render(view: "edit", model: [exportJobInstance: exportJobInstance])
                return
            }
        }

        exportJobInstance.properties = params

        if (!exportJobInstance.save(flush: true)) {
            render(view: "edit", model: [exportJobInstance: exportJobInstance])
            return
        }

        flash.message = message(code: 'default.updated.message', args: [message(code: 'exportJob.label', default: 'ExportJob'), exportJobInstance.id])
        redirect(action: "show", id: exportJobInstance.id)
    }

    def delete(Long id) {
        def exportJobInstance = ExportJob.get(id)
        if (!exportJobInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'exportJob.label', default: 'ExportJob'), id])
            redirect(action: "list")
            return
        }

        try {
            exportJobInstance.delete(flush: true)
            flash.message = message(code: 'default.deleted.message', args: [message(code: 'exportJob.label', default: 'ExportJob'), id])
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'exportJob.label', default: 'ExportJob'), id])
            redirect(action: "show", id: id)
        }
    }
	
	def progress() {
		def jobs = request.JSON.jobs
		List<ExcelJob> jobList
		def progress = [:]
		
		if (jobs && jobs instanceof List) {
			jobList = ExportJob.getAll(jobs)
		}
			
		jobList.each {
			if (it) {
				def p = (((double)it.step / (double)it.steps) * 100).round(2)
				progress["${it.id}"] = p
			}
		}
		
		render progress
	}
}
