<g:if test="${ej}">
	<g:link controller="product" action="download" id="${ej?.id}">${ej?.filename}</g:link>- ${((ej?.step.toDouble()/ej?.steps.toDouble()) * 100).round(2)}%<br/>
</g:if>