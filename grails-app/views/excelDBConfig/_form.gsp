<%@ page import="com.trinary.ExcelDB.ExcelDBConfig" %>



<div class="fieldcontain ${hasErrors(bean: excelDBConfigInstance, field: 'configKey', 'error')} ">
	<label for="configKey">
		<g:message code="excelDBConfig.configKey.label" default="Config Key" />
		
	</label>
	<g:textField name="configKey" value="${excelDBConfigInstance?.configKey}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: excelDBConfigInstance, field: 'configValue', 'error')} ">
	<label for="configValue">
		<g:message code="excelDBConfig.configValue.label" default="Config Value" />
		
	</label>
	<g:textField name="configValue" value="${excelDBConfigInstance?.configValue}"/>
</div>

