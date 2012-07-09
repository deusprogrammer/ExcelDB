
<%@ page import="com.trinary.ExcelDB.ExcelJob" %>
<!doctype html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'excelJob.label', default: 'ExcelJob')}" />
		<title><g:message code="default.show.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#show-excelJob" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="list" action="list"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="show-excelJob" class="content scaffold-show" role="main">
			<h1><g:message code="default.show.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<ol class="property-list excelJob">
			
				<g:if test="${excelJobInstance?.fileName}">
				<li class="fieldcontain">
					<span id="fileName-label" class="property-label"><g:message code="excelJob.fileName.label" default="File Name" /></span>
					
						<span class="property-value" aria-labelledby="fileName-label"><g:fieldValue bean="${excelJobInstance}" field="fileName"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${excelJobInstance?.status}">
				<li class="fieldcontain">
					<span id="status-label" class="property-label"><g:message code="excelJob.status.label" default="Status" /></span>
					
						<span class="property-value" aria-labelledby="status-label"><g:fieldValue bean="${excelJobInstance}" field="status"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${excelJobInstance?.step}">
				<li class="fieldcontain">
					<span id="step-label" class="property-label"><g:message code="excelJob.step.label" default="Step" /></span>
					
						<span class="property-value" aria-labelledby="step-label"><g:fieldValue bean="${excelJobInstance}" field="step"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${excelJobInstance?.nSteps}">
				<li class="fieldcontain">
					<span id="nSteps-label" class="property-label"><g:message code="excelJob.nSteps.label" default="NS teps" /></span>
					
						<span class="property-value" aria-labelledby="nSteps-label"><g:fieldValue bean="${excelJobInstance}" field="nSteps"/></span>
					
				</li>
				</g:if>
			
			</ol>
			<g:form>
				<fieldset class="buttons">
					<g:hiddenField name="id" value="${excelJobInstance?.id}" />
					<g:link class="edit" action="edit" id="${excelJobInstance?.id}"><g:message code="default.button.edit.label" default="Edit" /></g:link>
					<g:actionSubmit class="delete" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />
				</fieldset>
			</g:form>
		</div>
	</body>
</html>
