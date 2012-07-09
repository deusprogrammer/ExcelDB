
<%@ page import="com.trinary.ExcelDB.Keywords" %>
<!doctype html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'keywords.label', default: 'Keywords')}" />
		<title><g:message code="default.list.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#list-keywords" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="list-keywords" class="content scaffold-list" role="main">
			<h1><g:message code="default.list.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<table>
				<thead>
					<tr>
					
						<g:sortableColumn property="columnName" title="${message(code: 'keywords.columnName.label', default: 'Column Name')}" />
					
						<g:sortableColumn property="keywords" title="${message(code: 'keywords.keywords.label', default: 'Keywords')}" />
					
					</tr>
				</thead>
				<tbody>
				<g:each in="${keywordsInstanceList}" status="i" var="keywordsInstance">
					<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
					
						<td><g:link action="show" id="${keywordsInstance.id}">${fieldValue(bean: keywordsInstance, field: "columnName")}</g:link></td>
					
						<td>${fieldValue(bean: keywordsInstance, field: "keywords")}</td>
					
					</tr>
				</g:each>
				</tbody>
			</table>
			<div class="pagination">
				<g:paginate total="${keywordsInstanceTotal}" />
			</div>
		</div>
	</body>
</html>
