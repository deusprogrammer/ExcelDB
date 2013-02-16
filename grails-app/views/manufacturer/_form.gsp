<%@ page import="com.trinary.ExcelDB.Manufacturer" %>

<div class="fieldcontain ${hasErrors(bean: manufacturerInstance, field: 'manufacturerCode', 'error')} ">
	<label for="manufacturerCode">
		<g:message code="manufacturer.manufacturerCode.label" default="Manufacturer Code" />
		
	</label>
	<g:textField name="manufacturerCode" value="${manufacturerInstance?.manufacturerCode}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: manufacturerInstance, field: 'manufacturerName', 'error')} ">
	<label for="manufacturerName">
		<g:message code="manufacturer.manufacturerName.label" default="Manufacturer Name" />
		
	</label>
	<g:textField name="manufacturerName" value="${manufacturerInstance?.manufacturerName}"/>
</div>