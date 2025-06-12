<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
	<head>
		<meta charset="UTF-8">
		<title>Edit Project</title>
	</head>
	<body>
		<%@ include file="include/menu.jsp" %>
		<div>
			<form method="post" action="/ssr/project/edit/${ projectinformation.projectId }">
				<input type="hidden" name="_method" value="PUT"/>
				項目名: <input type="text" name="projectName" value="${ projectinformation.name }" required /><br />
				部門: <input type="text" name="department" value="${ projectinformation.department }" required /><br />
				組員名稱: <input type="text" name="userName" value="${ projectinformation.userName }" required /><br />
				項目狀態: <input type="checkbox" name="projectStatu" ${ projectinformation.projectStatu ? "checked" : "" } /><br />
				<button type="submit">修改</button>
			</form>
		</div>
	</body>
</html>