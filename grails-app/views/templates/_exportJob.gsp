<g:if test="${ej}">
    <g:if test="${ej.status == 'Success'}">
	   <font color='green'><g:link controller="product" action="download" id="${ej?.id}">${ej?.filename}</g:link>- ${((ej?.step.toDouble()/ej?.steps.toDouble()) * 100).round(2)}% [SUCCESS]</font><br/>
	</g:if>
	<g:elseif test="${ej.status == 'Failed'}">
	   <font color='red'>${ej?.filename}- ${((ej?.step.toDouble()/ej?.steps.toDouble()) * 100).round(2)}% [FAILED]</font><br/>
	</g:elseif>
	<g:else>
	   ${ej?.filename}- ${((ej?.step.toDouble()/ej?.steps.toDouble()) * 100).round(2)}% [EXPORTING]<br/>
	</g:else>
</g:if>