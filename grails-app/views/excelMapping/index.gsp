<!--
  To change this template, choose Tools | Templates
  and open the template in the editor.
-->

<%@ page contentType="text/html;charset=UTF-8" %>

<html>
  <head>
    <meta name="layout" content="main">
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Manual Excel Mapping</title>
  </head>
  <body>
    <h1>Manual Excel Mappings</h1>
    <g:form name="mapping" url="[action:'map', controller: 'excelMapping']">
      <table>
        <tbody>
          <tr>
            <%def j=0%>
            <g:while test="${j < width}">
              <th><g:select from="${["None", "Price", "Product Number", "Product Description"]}" name="colhead" /></th>
              <%j++%>
            </g:while>
          </tr>
          <g:each in="${table}" status="i" var="cells">
            <tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
              <g:each in="${cells}" var="cell">
                <td>${cell}</td>
              </g:each>
            </tr>
          </g:each>
        </tbody>
      </table>
      <div style="text-align: right; position: relative; right: 10px"><g:submitButton name="submit" value="Submit" /></div>
  </g:form>
  </body>
</html>