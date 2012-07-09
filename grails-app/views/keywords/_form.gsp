<%@ page import="com.trinary.ExcelDB.Keywords" %>



<div class="fieldcontain ${hasErrors(bean: keywordsInstance, field: 'columnName', 'error')} ">
	<label for="columnName">
		<g:message code="keywords.columnName.label" default="Column Name" />
		
	</label>
	<g:textField name="columnName" value="${keywordsInstance?.columnName}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: keywordsInstance, field: 'keywords', 'error')} ">
	<label for="keywords">
		<g:message code="keywords.keywords.label" default="Keywords" />
		
	</label>
	<g:textField name="keywords" value="${keywordsInstance?.keywords}"/>
</div>

