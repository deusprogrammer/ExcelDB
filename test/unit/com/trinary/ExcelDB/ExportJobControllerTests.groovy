package com.trinary.ExcelDB



import org.junit.*
import grails.test.mixin.*

@TestFor(ExportJobController)
@Mock(ExportJob)
class ExportJobControllerTests {

    def populateValidParams(params) {
        assert params != null
        // TODO: Populate valid properties like...
        //params["name"] = 'someValidName'
    }

    void testIndex() {
        controller.index()
        assert "/exportJob/list" == response.redirectedUrl
    }

    void testList() {

        def model = controller.list()

        assert model.exportJobInstanceList.size() == 0
        assert model.exportJobInstanceTotal == 0
    }

    void testCreate() {
        def model = controller.create()

        assert model.exportJobInstance != null
    }

    void testSave() {
        controller.save()

        assert model.exportJobInstance != null
        assert view == '/exportJob/create'

        response.reset()

        populateValidParams(params)
        controller.save()

        assert response.redirectedUrl == '/exportJob/show/1'
        assert controller.flash.message != null
        assert ExportJob.count() == 1
    }

    void testShow() {
        controller.show()

        assert flash.message != null
        assert response.redirectedUrl == '/exportJob/list'

        populateValidParams(params)
        def exportJob = new ExportJob(params)

        assert exportJob.save() != null

        params.id = exportJob.id

        def model = controller.show()

        assert model.exportJobInstance == exportJob
    }

    void testEdit() {
        controller.edit()

        assert flash.message != null
        assert response.redirectedUrl == '/exportJob/list'

        populateValidParams(params)
        def exportJob = new ExportJob(params)

        assert exportJob.save() != null

        params.id = exportJob.id

        def model = controller.edit()

        assert model.exportJobInstance == exportJob
    }

    void testUpdate() {
        controller.update()

        assert flash.message != null
        assert response.redirectedUrl == '/exportJob/list'

        response.reset()

        populateValidParams(params)
        def exportJob = new ExportJob(params)

        assert exportJob.save() != null

        // test invalid parameters in update
        params.id = exportJob.id
        //TODO: add invalid values to params object

        controller.update()

        assert view == "/exportJob/edit"
        assert model.exportJobInstance != null

        exportJob.clearErrors()

        populateValidParams(params)
        controller.update()

        assert response.redirectedUrl == "/exportJob/show/$exportJob.id"
        assert flash.message != null

        //test outdated version number
        response.reset()
        exportJob.clearErrors()

        populateValidParams(params)
        params.id = exportJob.id
        params.version = -1
        controller.update()

        assert view == "/exportJob/edit"
        assert model.exportJobInstance != null
        assert model.exportJobInstance.errors.getFieldError('version')
        assert flash.message != null
    }

    void testDelete() {
        controller.delete()
        assert flash.message != null
        assert response.redirectedUrl == '/exportJob/list'

        response.reset()

        populateValidParams(params)
        def exportJob = new ExportJob(params)

        assert exportJob.save() != null
        assert ExportJob.count() == 1

        params.id = exportJob.id

        controller.delete()

        assert ExportJob.count() == 0
        assert ExportJob.get(exportJob.id) == null
        assert response.redirectedUrl == '/exportJob/list'
    }
}
