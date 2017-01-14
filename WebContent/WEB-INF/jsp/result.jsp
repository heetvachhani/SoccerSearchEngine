<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
</head>
<body>

	<p>Here are the results..</p>

	<table class="table table-hover">
		<tbody>
			<c:forEach items="${records}" var="doc" varStatus="recordIndex">
				<tr>
					<td><h3>
							<strong><a href='<c:out value="${doc.urlOfDoc}"/>'><c:out
										value="${doc.titleOfDoc}" /></a></strong>
						</h3></td>


				</tr>
				<tr>
					<td><a href='<c:out value="${doc.urlOfDoc}"/>'><c:out
								value="${doc.urlOfDoc}" /></a></td>
				</tr>
				<tr>
					<td>"${doc.docContents}"</td>
				</tr>
			</c:forEach>
		</tbody>
	</table>








</body>
</html>