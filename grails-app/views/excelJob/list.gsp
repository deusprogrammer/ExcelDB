
<%@ page import="com.trinary.ExcelDB.ExcelJob" %>
<!doctype html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'excelJob.label', default: 'ExcelJob')}" />
		<title><g:message code="default.list.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#list-excelJob" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="list-excelJob" class="content scaffold-list" role="main">
			<h1><g:message code="default.list.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<table>
				<thead>
					<tr>
					
						<g:sortableColumn property="fileName" title="${message(code: 'excelJob.fileName.label', default: 'File Name')}" />
					
						<g:sortableColumn property="status" title="${message(code: 'excelJob.status.label', default: 'Status')}" />
					
						<g:sortableColumn property="step" title="${message(code: 'excelJob.step.label', default: 'Step')}" />
					
						<g:sortableColumn property="nSteps" title="${message(code: 'excelJob.nSteps.label', default: 'NS teps')}" />
                                                
                                                <th>Progress</th>
					
					</tr>
				</thead>
				<tbody>
				<g:each in="${excelJobInstanceList}" status="i" var="excelJobInstance">
					<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
					
						<td><g:link action="show" id="${excelJobInstance.id}">${fieldValue(bean: excelJobInstance, field: "fileName")}</g:link></td>
					
						<td>${fieldValue(bean: excelJobInstance, field: "status")}</td>
					
						<td>${fieldValue(bean: excelJobInstance, field: "step")}</td>
					
						<td>${fieldValue(bean: excelJobInstance, field: "nSteps")}</td>
                                                
                                                <td>${((double)((excelJobInstance.step/excelJobInstance.nSteps) * 100.0)).round(2)}%</td>
					
					</tr>
				</g:each>
				</tbody>
			</table>
			<div class="pagination">
				<g:paginate total="${excelJobInstanceTotal}" />
			</div>
		</div>
	</body>
</html>
