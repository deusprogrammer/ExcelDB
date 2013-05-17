<%@ page import="com.trinary.ExcelDB.ExcelJob" %>
<%@ page import="com.trinary.ExcelDB.PendingJob" %>
<%@ page import="com.trinary.ExcelDB.Product" %>
<%@ page import="com.trinary.ExcelDB.Manufacturer" %>
<%@ page import="com.trinary.ExcelDB.ExportJob" %>
<html>
	<head>
		<meta name="layout" content="main"/>
		<title>Welcome to ExcelDB</title>
		<style type="text/css" media="screen">
			#status {
				background-color: #eee;
				border: .2em solid #fff;
				margin: 2em 2em 1em;
				padding: 1em;
				width: 12em;
				float: left;
				-moz-box-shadow: 0px 0px 1.25em #ccc;
				-webkit-box-shadow: 0px 0px 1.25em #ccc;
				box-shadow: 0px 0px 1.25em #ccc;
				-moz-border-radius: 0.6em;
				-webkit-border-radius: 0.6em;
				border-radius: 0.6em;
			}

			.ie6 #status {
				display: inline; /* float double margin fix http://www.positioniseverything.net/explorer/doubled-margin.html */
			}

			#status ul {
				font-size: 0.9em;
				list-style-type: none;
				margin-bottom: 0.6em;
				padding: 0;
			}
            
			#status li {
				line-height: 1.3;
			}

			#status h1 {
				text-transform: uppercase;
				font-size: 1.1em;
				margin: 0 0 0.3em;
			}

			#page-body {
				margin: 2em 1em 1.25em 18em;
			}
                        
                        div.sub-item {
                          position: relative;
                          left: 20px;
                        }

			h2 {
				margin-top: 1em;
				margin-bottom: 0.3em;
				font-size: 1em;
			}

			p {
				line-height: 1.5;
				margin: 0.25em 0;
			}

			#controller-list ul {
				list-style-position: inside;
			}

			#controller-list li {
				line-height: 1.3;
				list-style-position: inside;
				margin: 0.25em 0;
			}

			@media screen and (max-width: 480px) {
				#status {
					display: none;
				}

				#page-body {
					margin: 0 1em 1em;
				}

				#page-body h1 {
					margin-top: 0;
				}
			}
		</style>
	</head>
	<body>
		<a href="#page-body" class="skip"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div id="status" role="complementary">
			<h1>Application Status</h1>
			<ul>
				<li>App version: <g:meta name="app.version"/></li>
				<li>Grails version: <g:meta name="app.grails.version"/></li>
				<li>Groovy version: ${org.codehaus.groovy.runtime.InvokerHelper.getVersion()}</li>
				<li>JVM version: ${System.getProperty('java.version')}</li>
				<li>Reloading active: ${grails.util.Environment.reloadingAgentEnabled}</li>
				<li>Controllers: ${grailsApplication.controllerClasses.size()}</li>
				<li>Domains: ${grailsApplication.domainClasses.size()}</li>
				<li>Services: ${grailsApplication.serviceClasses.size()}</li>
				<li>Tag Libraries: ${grailsApplication.tagLibClasses.size()}</li>
			</ul>
			<h1>Installed Plugins</h1>
			<ul>
				<g:each var="plugin" in="${applicationContext.getBean('pluginManager').allPlugins}">
					<li>${plugin.name} - ${plugin.version}</li>
				</g:each>
			</ul>
		</div>
		<div id="page-body" role="main">
			<h1>Welcome to ExcelDB</h1>
			<p>This web application will extract product information from a Microsoft Excel file and insert that information into a GORM database.  Then at any time an Excel representation of that file can be requested.</p>
			<sec:ifAllGranted roles="ROLE_ADMIN">
                 <h3>Upload zip file:</h3>
                 <div class="sub-item">
                   <fileuploader:form upload="zip" 
                     successAction="success"
                     successController="zip"
                     errorAction="error"
                     errorController="zip"/>
                 </div>
                 
                 <h3>Upload Excel file:</h3>
                 <div class="sub-item">
                   <fileuploader:form upload="excel" 
                     successAction="success"
                     successController="excel"
                     errorAction="error"
                     errorController="excel"/>
                 </div>
			</sec:ifAllGranted>
            <sec:ifAllGranted roles="ROLE_ADMIN">
				<g:if test="${Product.count() > 0}">
                	<h3>Product Database</h3>
                    <div class="sub-item">
                    	There are currently <b>${Product.count()}</b> products in the database.<br/>
                        <g:link controller="product" action="list">Product List</g:link><br/>
                        <g:link controller="product" action="reset" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');">Reset Database</g:link><br/>
                        <g:link controller="product" action="writeOut">Request Excel Spreadsheet</g:link><br/>
                        <div class="sub-item">
                        	<g:if test="${ExportJob.count() > 0}">
                        		<%
									def ejList = ExportJob.list(max: 3, sort: 'dateCreated', order: 'desc')
									def ejId = ejList*.id
								%>
	                            <g:each in="${ExportJob.list(max: 3, sort: 'dateCreated', order: 'desc')}" var="ej">
	                            	<g:render template="/templates/exportJob" model="${[ej:ej]}"/>
	                            </g:each>
	                            <g:link controller="exportJob" action="list">See More...</g:link>
                            </g:if>
                    	</div>
                	</div>
				</g:if>
             
				<g:if test="${ExcelJob.findAllByStatusNotEqual("Success").size() > 0}">
                	<h3>View Running Jobs</h3>
                    <div class="sub-item">
						There are currently <b>${ExcelJob.findAllByStatusNotEqual("Success").size()}</b> jobs running.<br/>                          	
                        <g:link controller="excelJob" action="list">Jobs List</g:link>
					</div>
				</g:if>
                       
                <g:if test="${PendingJob.count() > 0}">
                	<h3>Process Pending Jobs</h3>
                    <div class="sub-item">
                    	There are currently <b>${PendingJob.count()}</b> jobs waiting to be mapped.<br/>
                        <g:link controller="pendingJob" action="pop">Map Pending Excel Jobs</g:link><br/>
					</div>
				</g:if>
                       
                <g:if test="${Manufacturer.count() > 0}">
                	<h3>Manufacturers</h3>
                    <div class="sub-item">
	                    <g:if test="${Manufacturer.count() - Manufacturer.findAllByManufacturerNameNotEqual("").size() > 0}">
							There are currently <b>${Manufacturer.count() - Manufacturer.findAllByManufacturerNameNotEqual("").size()}</b> manufacturers that still need to be identified in your database.<br/>
						</g:if>		
	                    There are currently <b>${Manufacturer.findAllByManufacturerNameNotEqual("").size()}</b> identified manufacturers in your database.<br/>
	                    <g:link controller="manufacturer" action="list">List and Edit Manufacturers</g:link><br/>
					</div>
				</g:if>
                       
                <h3>Settings</h3>
                	<div class="sub-item">
                         <g:link controller="excelDBConfig" action="list">Configuration</g:link><br/>
                         <g:link controller="user" action="list">Manage Users</g:link>
					</div>
			</sec:ifAllGranted>
		</div>
	</body>
</html>
