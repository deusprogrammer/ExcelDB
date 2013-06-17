<%@ page import="com.trinary.ExcelDB.Product" %>



<div class="fieldcontain ${hasErrors(bean: productInstance, field: 'productDescription', 'error')} ">
	<label for="productDescription">
		<g:message code="product.productDescription.label" default="Product Description" />
		
	</label>
	<g:textField name="productDescription" value="${productInstance?.productDescription}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: productInstance, field: 'productNumber', 'error')} ">
	<label for="productNumber">
		<g:message code="product.productNumber.label" default="Product Number" />
		
	</label>
	<g:textField name="productNumber" value="${productInstance?.productNumber}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: productInstance, field: 'productPrice', 'error')} ">
	<label for="productPrice">
		<g:message code="product.productPrice.label" default="Product Price" />
		
	</label>
	<g:textField name="productPrice" value="${productInstance?.productPrice}"/>
</div>

