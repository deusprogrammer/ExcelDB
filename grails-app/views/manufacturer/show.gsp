
<%@ page import="com.trinary.ExcelDB.Manufacturer" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'manufacturer.label', default: 'Manufacturer')}" />
		<title><g:message code="default.show.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#show-manufacturer" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="list" action="list"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="show-manufacturer" class="content scaffold-show" role="main">
			<h1><g:message code="default.show.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<ol class="property-list manufacturer">
			
				<g:if test="${manufacturerInstance?.manufacturerCode}">
				<li class="fieldcontain">
					<span id="manufacturerCode-label" class="property-label"><g:message code="manufacturer.manufacturerCode.label" default="Manufacturer Code" /></span>
					
						<span class="property-value" aria-labelledby="manufacturerCode-label"><g:fieldValue bean="${manufacturerInstance}" field="manufacturerCode"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${manufacturerInstance?.manufacturerName}">
				<li class="fieldcontain">
					<span id="manufacturerName-label" class="property-label"><g:message code="manufacturer.manufacturerName.label" default="Manufacturer Name" /></span>
					
						<span class="property-value" aria-labelledby="manufacturerName-label"><g:fieldValue bean="${manufacturerInstance}" field="manufacturerName"/></span>
					
				</li>
				</g:if>
			

			
			</ol>
			<g:form>
				<fieldset class="buttons">
					<g:hiddenField name="id" value="${manufacturerInstance?.id}" />
					<g:link class="edit" action="edit" id="${manufacturerInstance?.id}"><g:message code="default.button.edit.label" default="Edit" /></g:link>
					<g:actionSubmit class="delete" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />
				</fieldset>
			</g:form>
		</div>
	</body>
</html>
