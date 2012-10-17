package com.trinary.ExcelDB



import org.junit.*
import grails.test.mixin.*

@TestFor(ExcelDBConfigController)
@Mock(ExcelDBConfig)
class ExcelDBConfigControllerTests {


    def populateValidParams(params) {
      assert params != null
      // TODO: Populate valid properties like...
      //params["name"] = 'someValidName'
    }

    void testIndex() {
        controller.index()
        assert "/excelDBConfig/list" == response.redirectedUrl
    }

    void testList() {

        def model = controller.list()

        assert model.excelDBConfigInstanceList.size() == 0
        assert model.excelDBConfigInstanceTotal == 0
    }

    void testCreate() {
       def model = controller.create()

       assert model.excelDBConfigInstance != null
    }

    void testSave() {
        controller.save()

        assert model.excelDBConfigInstance != null
        assert view == '/excelDBConfig/create'

        response.reset()

        populateValidParams(params)
        controller.save()

        assert response.redirectedUrl == '/excelDBConfig/show/1'
        assert controller.flash.message != null
        assert ExcelDBConfig.count() == 1
    }

    void testShow() {
        controller.show()

        assert flash.message != null
        assert response.redirectedUrl == '/excelDBConfig/list'


        populateValidParams(params)
        def excelDBConfig = new ExcelDBConfig(params)

        assert excelDBConfig.save() != null

        params.id = excelDBConfig.id

        def model = controller.show()

        assert model.excelDBConfigInstance == excelDBConfig
    }

    void testEdit() {
        controller.edit()

        assert flash.message != null
        assert response.redirectedUrl == '/excelDBConfig/list'


        populateValidParams(params)
        def excelDBConfig = new ExcelDBConfig(params)

        assert excelDBConfig.save() != null

        params.id = excelDBConfig.id

        def model = controller.edit()

        assert model.excelDBConfigInstance == excelDBConfig
    }

    void testUpdate() {
        controller.update()

        assert flash.message != null
        assert response.redirectedUrl == '/excelDBConfig/list'

        response.reset()


        populateValidParams(params)
        def excelDBConfig = new ExcelDBConfig(params)

        assert excelDBConfig.save() != null

        // test invalid parameters in update
        params.id = excelDBConfig.id
        //TODO: add invalid values to params object

        controller.update()

        assert view == "/excelDBConfig/edit"
        assert model.excelDBConfigInstance != null

        excelDBConfig.clearErrors()

        populateValidParams(params)
        controller.update()

        assert response.redirectedUrl == "/excelDBConfig/show/$excelDBConfig.id"
        assert flash.message != null

        //test outdated version number
        response.reset()
        excelDBConfig.clearErrors()

        populateValidParams(params)
        params.id = excelDBConfig.id
        params.version = -1
        controller.update()

        assert view == "/excelDBConfig/edit"
        assert model.excelDBConfigInstance != null
        assert model.excelDBConfigInstance.errors.getFieldError('version')
        assert flash.message != null
    }

    void testDelete() {
        controller.delete()
        assert flash.message != null
        assert response.redirectedUrl == '/excelDBConfig/list'

        response.reset()

        populateValidParams(params)
        def excelDBConfig = new ExcelDBConfig(params)

        assert excelDBConfig.save() != null
        assert ExcelDBConfig.count() == 1

        params.id = excelDBConfig.id

        controller.delete()

        assert ExcelDBConfig.count() == 0
        assert ExcelDBConfig.get(excelDBConfig.id) == null
        assert response.redirectedUrl == '/excelDBConfig/list'
    }
}
