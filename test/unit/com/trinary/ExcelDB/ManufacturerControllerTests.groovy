package com.trinary.ExcelDB



import org.junit.*
import grails.test.mixin.*

@TestFor(ManufacturerController)
@Mock(Manufacturer)
class ManufacturerControllerTests {

    def populateValidParams(params) {
        assert params != null
        // TODO: Populate valid properties like...
        //params["name"] = 'someValidName'
    }

    void testIndex() {
        controller.index()
        assert "/manufacturer/list" == response.redirectedUrl
    }

    void testList() {

        def model = controller.list()

        assert model.manufacturerInstanceList.size() == 0
        assert model.manufacturerInstanceTotal == 0
    }

    void testCreate() {
        def model = controller.create()

        assert model.manufacturerInstance != null
    }

    void testSave() {
        controller.save()

        assert model.manufacturerInstance != null
        assert view == '/manufacturer/create'

        response.reset()

        populateValidParams(params)
        controller.save()

        assert response.redirectedUrl == '/manufacturer/show/1'
        assert controller.flash.message != null
        assert Manufacturer.count() == 1
    }

    void testShow() {
        controller.show()

        assert flash.message != null
        assert response.redirectedUrl == '/manufacturer/list'

        populateValidParams(params)
        def manufacturer = new Manufacturer(params)

        assert manufacturer.save() != null

        params.id = manufacturer.id

        def model = controller.show()

        assert model.manufacturerInstance == manufacturer
    }

    void testEdit() {
        controller.edit()

        assert flash.message != null
        assert response.redirectedUrl == '/manufacturer/list'

        populateValidParams(params)
        def manufacturer = new Manufacturer(params)

        assert manufacturer.save() != null

        params.id = manufacturer.id

        def model = controller.edit()

        assert model.manufacturerInstance == manufacturer
    }

    void testUpdate() {
        controller.update()

        assert flash.message != null
        assert response.redirectedUrl == '/manufacturer/list'

        response.reset()

        populateValidParams(params)
        def manufacturer = new Manufacturer(params)

        assert manufacturer.save() != null

        // test invalid parameters in update
        params.id = manufacturer.id
        //TODO: add invalid values to params object

        controller.update()

        assert view == "/manufacturer/edit"
        assert model.manufacturerInstance != null

        manufacturer.clearErrors()

        populateValidParams(params)
        controller.update()

        assert response.redirectedUrl == "/manufacturer/show/$manufacturer.id"
        assert flash.message != null

        //test outdated version number
        response.reset()
        manufacturer.clearErrors()

        populateValidParams(params)
        params.id = manufacturer.id
        params.version = -1
        controller.update()

        assert view == "/manufacturer/edit"
        assert model.manufacturerInstance != null
        assert model.manufacturerInstance.errors.getFieldError('version')
        assert flash.message != null
    }

    void testDelete() {
        controller.delete()
        assert flash.message != null
        assert response.redirectedUrl == '/manufacturer/list'

        response.reset()

        populateValidParams(params)
        def manufacturer = new Manufacturer(params)

        assert manufacturer.save() != null
        assert Manufacturer.count() == 1

        params.id = manufacturer.id

        controller.delete()

        assert Manufacturer.count() == 0
        assert Manufacturer.get(manufacturer.id) == null
        assert response.redirectedUrl == '/manufacturer/list'
    }
}
