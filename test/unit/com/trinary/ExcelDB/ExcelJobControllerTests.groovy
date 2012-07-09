package com.trinary.ExcelDB



import org.junit.*
import grails.test.mixin.*

@TestFor(ExcelJobController)
@Mock(ExcelJob)
class ExcelJobControllerTests {


    def populateValidParams(params) {
      assert params != null
      // TODO: Populate valid properties like...
      //params["name"] = 'someValidName'
    }

    void testIndex() {
        controller.index()
        assert "/excelJob/list" == response.redirectedUrl
    }

    void testList() {

        def model = controller.list()

        assert model.excelJobInstanceList.size() == 0
        assert model.excelJobInstanceTotal == 0
    }

    void testCreate() {
       def model = controller.create()

       assert model.excelJobInstance != null
    }

    void testSave() {
        controller.save()

        assert model.excelJobInstance != null
        assert view == '/excelJob/create'

        response.reset()

        populateValidParams(params)
        controller.save()

        assert response.redirectedUrl == '/excelJob/show/1'
        assert controller.flash.message != null
        assert ExcelJob.count() == 1
    }

    void testShow() {
        controller.show()

        assert flash.message != null
        assert response.redirectedUrl == '/excelJob/list'


        populateValidParams(params)
        def excelJob = new ExcelJob(params)

        assert excelJob.save() != null

        params.id = excelJob.id

        def model = controller.show()

        assert model.excelJobInstance == excelJob
    }

    void testEdit() {
        controller.edit()

        assert flash.message != null
        assert response.redirectedUrl == '/excelJob/list'


        populateValidParams(params)
        def excelJob = new ExcelJob(params)

        assert excelJob.save() != null

        params.id = excelJob.id

        def model = controller.edit()

        assert model.excelJobInstance == excelJob
    }

    void testUpdate() {
        controller.update()

        assert flash.message != null
        assert response.redirectedUrl == '/excelJob/list'

        response.reset()


        populateValidParams(params)
        def excelJob = new ExcelJob(params)

        assert excelJob.save() != null

        // test invalid parameters in update
        params.id = excelJob.id
        //TODO: add invalid values to params object

        controller.update()

        assert view == "/excelJob/edit"
        assert model.excelJobInstance != null

        excelJob.clearErrors()

        populateValidParams(params)
        controller.update()

        assert response.redirectedUrl == "/excelJob/show/$excelJob.id"
        assert flash.message != null

        //test outdated version number
        response.reset()
        excelJob.clearErrors()

        populateValidParams(params)
        params.id = excelJob.id
        params.version = -1
        controller.update()

        assert view == "/excelJob/edit"
        assert model.excelJobInstance != null
        assert model.excelJobInstance.errors.getFieldError('version')
        assert flash.message != null
    }

    void testDelete() {
        controller.delete()
        assert flash.message != null
        assert response.redirectedUrl == '/excelJob/list'

        response.reset()

        populateValidParams(params)
        def excelJob = new ExcelJob(params)

        assert excelJob.save() != null
        assert ExcelJob.count() == 1

        params.id = excelJob.id

        controller.delete()

        assert ExcelJob.count() == 0
        assert ExcelJob.get(excelJob.id) == null
        assert response.redirectedUrl == '/excelJob/list'
    }
}
