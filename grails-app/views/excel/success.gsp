<!--
  To change this template, choose Tools | Templates
  and open the template in the editor.
-->

<%@ page contentType="text/html;charset=UTF-8" %>

<html>
    <head>
        <title>Grails File-Uploader Demo</title>
		<meta name="layout" content="main" />
    </head>
    <body>
        <h3 style="margin-left:20px;">Jobs Created</h3>
        <p style="margin-left:20px;width:80%">
			<g:each var="f" in="${files}">
				<table>
					<tr>
						<td><b>Name</b></td>
						<td>${job.file}</td>
                                                <td><b>ID</b></td>
						<td>${job.id}</td>
					</tr>

				</table>
			</g:each>
		</p>
    </body>
</html>
