<!--
  To change this template, choose Tools | Templates
  and open the template in the editor.
-->

<%@ page contentType="text/html;charset=UTF-8" %>

<html>
  <head>
    <meta name="layout" content="main">
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Manual Excel Mapping for ${file}</title>
  </head>
  <body>
    <g:if test="${sheet > 0}">
      <g:link action="show" params="${[file: file, sheet: sheet-1]}">[Prev Sheet]</g:link>
    </g:if>
    <g:if test="${sheet < nSheets - 1}">
      <g:link action="show" params="${[file: file, sheet: sheet+1]}">[Next Sheet]</g:link>
    </g:if>
    <h1>Manual Excel Mapping for ${file}</h1>
      <g:form name="mapping" url="[action:'map', controller: 'excelMapping']">
        <div style="overflow: scroll; max-width: 960px; max-height: 400px; margin: auto;">
          <g:hiddenField name="fileLocation" value="${file}" />
          <g:hiddenField name="sheet" value="${sheet}" />
          <table>
            <tbody>
              <tr>
                <%def j=0%>
                <th>Starting Row</th>
                <g:while test="${j < width}">
                  <th><g:select from="${["None", "Product Price", "Product Number", "Product Description"]}" name="colhead" /></th>
                  <%j++%>
                </g:while>
              </tr>
              <g:each in="${table}" status="i" var="cells">
                <tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
                  <td><g:radio name="radioGroup" value="${i}"/></td>
                  <g:each in="${cells}" var="cell">
                    <td>${cell}</td>
                  </g:each>
                </tr>
              </g:each>
            </tbody>
          </table>
        </div>
        <div style="text-align: right; position: relative; right: 10px"><g:submitButton name="submit" value="Submit" />|<g:link action="pop">[Skip]</g:link></div>
    </g:form>
  </body>
</html>