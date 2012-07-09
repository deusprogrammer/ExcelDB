
<%@ page import="com.trinary.ExcelDB.Rules" %>
<!doctype html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'rules.label', default: 'Rules')}" />
		<title><g:message code="default.show.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#show-rules" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="list" action="list"><g:message code="default.list.label" args="[entityName]" /></g:link></li>
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="show-rules" class="content scaffold-show" role="main">
			<h1><g:message code="default.show.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<ol class="property-list rules">
			
				<g:if test="${rulesInstance?.columnName}">
				<li class="fieldcontain">
					<span id="columnName-label" class="property-label"><g:message code="rules.columnName.label" default="Column Name" /></span>
					
						<span class="property-value" aria-labelledby="columnName-label"><g:fieldValue bean="${rulesInstance}" field="columnName"/></span>
					
				</li>
				</g:if>
			
				<g:if test="${rulesInstance?.rules}">
				<li class="fieldcontain">
					<span id="rules-label" class="property-label"><g:message code="rules.rules.label" default="Rules" /></span>
					
						<span class="property-value" aria-labelledby="rules-label"><g:fieldValue bean="${rulesInstance}" field="rules"/></span>
					
				</li>
				</g:if>
			
			</ol>
			<g:form>
				<fieldset class="buttons">
					<g:hiddenField name="id" value="${rulesInstance?.id}" />
					<g:link class="edit" action="edit" id="${rulesInstance?.id}"><g:message code="default.button.edit.label" default="Edit" /></g:link>
					<g:actionSubmit class="delete" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" />
				</fieldset>
			</g:form>
		</div>
	</body>
</html>
