<%@ page import="com.trinary.ExcelDB.ExcelJob" %>



<div class="fieldcontain ${hasErrors(bean: excelJobInstance, field: 'fileName', 'error')} ">
	<label for="fileName">
		<g:message code="excelJob.fileName.label" default="File Name" />
		
	</label>
	<g:textField name="fileName" value="${excelJobInstance?.fileName}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: excelJobInstance, field: 'status', 'error')} ">
	<label for="status">
		<g:message code="excelJob.status.label" default="Status" />
		
	</label>
	<g:textField name="status" value="${excelJobInstance?.status}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: excelJobInstance, field: 'step', 'error')} required">
	<label for="step">
		<g:message code="excelJob.step.label" default="Step" />
		<span class="required-indicator">*</span>
	</label>
	<g:field type="number" name="step" required="" value="${excelJobInstance.step}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: excelJobInstance, field: 'nSteps', 'error')} required">
	<label for="nSteps">
		<g:message code="excelJob.nSteps.label" default="NS teps" />
		<span class="required-indicator">*</span>
	</label>
	<g:field type="number" name="nSteps" required="" value="${excelJobInstance.nSteps}"/>
</div>

