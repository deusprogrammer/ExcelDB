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
    <table>
    <tbody>
      <g:each in="${table}" status="i" var="cells">
	<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
          <g:each in="${cells}" var="cell">
            <td>${cell}</td>
          </g:each>
        </tr>
      </g:each>
    </tbody>
    </table>
  </body>
</html>