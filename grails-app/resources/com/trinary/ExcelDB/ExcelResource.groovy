/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.trinary.ExcelDB

import javax.ws.rs.GET
import javax.ws.rs.PUT
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.Consumes
import javax.ws.rs.QueryParam
import javax.ws.rs.core.Response
import org.grails.jaxrs.response.Responses.*

@Path('/api/excel')
class ExcelResource {
    def ExcelService

    @GET
    @Path('/parseLocal')
    @Produces('text/plain')
    String parseLocal(@QueryParam('file') String file) {
        try {
            return ExcelService.processExcelFile(file)
        }
        catch (Exception e) {
            return "Unable to find specified file!"
        }
    }
}