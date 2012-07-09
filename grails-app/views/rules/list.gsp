
<%@ page import="com.trinary.ExcelDB.Rules" %>
<!doctype html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'rules.label', default: 'Rules')}" />
		<title><g:message code="default.list.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#list-rules" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="list-rules" class="content scaffold-list" role="main">
			<h1><g:message code="default.list.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<table>
				<thead>
					<tr>
					
						<g:sortableColumn property="columnName" title="${message(code: 'rules.columnName.label', default: 'Column Name')}" />
					
						<g:sortableColumn property="rules" title="${message(code: 'rules.rules.label', default: 'Rules')}" />
					
					</tr>
				</thead>
				<tbody>
				<g:each in="${rulesInstanceList}" status="i" var="rulesInstance">
					<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
					
						<td><g:link action="show" id="${rulesInstance.id}">${fieldValue(bean: rulesInstance, field: "columnName")}</g:link></td>
					
						<td>${fieldValue(bean: rulesInstance, field: "rules")}</td>
					
					</tr>
				</g:each>
				</tbody>
			</table>
			<div class="pagination">
				<g:paginate total="${rulesInstanceTotal}" />
			</div>
		</div>
	</body>
</html>
