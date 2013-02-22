
<%@ page import="com.trinary.ExcelDB.ExcelDBConfig" %>
<!doctype html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'excelDBConfig.label', default: 'ExcelDBConfig')}" />
		<title><g:message code="default.list.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#list-excelDBConfig" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="list-excelDBConfig" class="content scaffold-list" role="main">
			<h1><g:message code="default.list.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<table>
				<thead>
					<tr>
					
						<g:sortableColumn property="configKey" title="${message(code: 'excelDBConfig.configKey.label', default: 'Config Key')}" />
					
						<g:sortableColumn property="configValue" title="${message(code: 'excelDBConfig.configValue.label', default: 'Config Value')}" />
					
					</tr>
				</thead>
				<tbody>
				<g:each in="${excelDBConfigInstanceList}" status="i" var="excelDBConfigInstance">
					<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
					
						<td><g:link action="edit" id="${excelDBConfigInstance.id}">${fieldValue(bean: excelDBConfigInstance, field: "configKey")}</g:link></td>
					
						<td>${fieldValue(bean: excelDBConfigInstance, field: "configValue")}</td>
					
					</tr>
				</g:each>
				</tbody>
			</table>
			<div class="pagination">
				<g:paginate total="${excelDBConfigInstanceTotal}" />
			</div>
		</div>
	</body>
</html>
