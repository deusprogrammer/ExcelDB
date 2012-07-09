package com.trinary.ExcelDB



import org.junit.*
import grails.test.mixin.*

@TestFor(RulesController)
@Mock(Rules)
class RulesControllerTests {


    def populateValidParams(params) {
      assert params != null
      // TODO: Populate valid properties like...
      //params["name"] = 'someValidName'
    }

    void testIndex() {
        controller.index()
        assert "/rules/list" == response.redirectedUrl
    }

    void testList() {

        def model = controller.list()

        assert model.rulesInstanceList.size() == 0
        assert model.rulesInstanceTotal == 0
    }

    void testCreate() {
       def model = controller.create()

       assert model.rulesInstance != null
    }

    void testSave() {
        controller.save()

        assert model.rulesInstance != null
        assert view == '/rules/create'

        response.reset()

        populateValidParams(params)
        controller.save()

        assert response.redirectedUrl == '/rules/show/1'
        assert controller.flash.message != null
        assert Rules.count() == 1
    }

    void testShow() {
        controller.show()

        assert flash.message != null
        assert response.redirectedUrl == '/rules/list'


        populateValidParams(params)
        def rules = new Rules(params)

        assert rules.save() != null

        params.id = rules.id

        def model = controller.show()

        assert model.rulesInstance == rules
    }

    void testEdit() {
        controller.edit()

        assert flash.message != null
        assert response.redirectedUrl == '/rules/list'


        populateValidParams(params)
        def rules = new Rules(params)

        assert rules.save() != null

        params.id = rules.id

        def model = controller.edit()

        assert model.rulesInstance == rules
    }

    void testUpdate() {
        controller.update()

        assert flash.message != null
        assert response.redirectedUrl == '/rules/list'

        response.reset()


        populateValidParams(params)
        def rules = new Rules(params)

        assert rules.save() != null

        // test invalid parameters in update
        params.id = rules.id
        //TODO: add invalid values to params object

        controller.update()

        assert view == "/rules/edit"
        assert model.rulesInstance != null

        rules.clearErrors()

        populateValidParams(params)
        controller.update()

        assert response.redirectedUrl == "/rules/show/$rules.id"
        assert flash.message != null

        //test outdated version number
        response.reset()
        rules.clearErrors()

        populateValidParams(params)
        params.id = rules.id
        params.version = -1
        controller.update()

        assert view == "/rules/edit"
        assert model.rulesInstance != null
        assert model.rulesInstance.errors.getFieldError('version')
        assert flash.message != null
    }

    void testDelete() {
        controller.delete()
        assert flash.message != null
        assert response.redirectedUrl == '/rules/list'

        response.reset()

        populateValidParams(params)
        def rules = new Rules(params)

        assert rules.save() != null
        assert Rules.count() == 1

        params.id = rules.id

        controller.delete()

        assert Rules.count() == 0
        assert Rules.get(rules.id) == null
        assert response.redirectedUrl == '/rules/list'
    }
}
