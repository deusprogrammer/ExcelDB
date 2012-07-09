<!--
  To change this template, choose Tools | Templates
  and open the template in the editor.
-->

<%@ page contentType="text/html;charset=UTF-8" %>

<html>
    <head>
        <title>Job Created</title>
		<meta name="layout" content="main" />
    </head>
    <body>
        <h3 style="margin-left:20px;">Jobs created</h3>
        <p style="margin-left:20px;width:80%">
			<g:each var="job" in="${jobs}">
				<table>
					<tr>
						<td><b>Name</b></td>
						<td>${job.file}</td>
                                        </tr><tr>
                                                <td><b>ID</b></td>
						<td>${job.id}</td>
					</tr>
                                        </tr><tr>
                                                <td><b>Files</b></td>
						<td>
                                                  <ul>
                                                  <g:each var="file" in="${files}">
                                                    <li>${file}</li>
                                                  </g:each>
                                                  </ul>
                                                </td>
					</tr>
				</table>
			</g:each>
		</p>
    </body>
</html>
