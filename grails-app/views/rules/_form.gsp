<%@ page import="com.trinary.ExcelDB.Rules" %>



<div class="fieldcontain ${hasErrors(bean: rulesInstance, field: 'columnName', 'error')} ">
	<label for="columnName">
		<g:message code="rules.columnName.label" default="Column Name" />
		
	</label>
	<g:textField name="columnName" value="${rulesInstance?.columnName}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: rulesInstance, field: 'rules', 'error')} ">
	<label for="rules">
		<g:message code="rules.rules.label" default="Rules" />
		
	</label>
	<g:textField name="rules" value="${rulesInstance?.rules}"/>
</div>

