<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
	<head>
		<meta charset="UTF-8">
		<title>Project List</title>
	</head>
	<body>
		<%@ include file="include/menu.jsp" %>
		<div>
			<form method="post" action="/ssr/project/add">
				項目名22: <input type="text" name="projectName" required /><br />
				部門: <input type="text" name="department" required /><br />
				組員名稱: <input type="text" name="userName" required /><br />
				項目狀態: <input type="checkbox" name="projectStatu" /><br />
				<button type="submit">送出</button>
			</form>
		</div>
		<div>
			<table border="1">
				<thead>
					<tr>
						<th>項目ID</th><th>項目名</th><th>部門</th><th>組員名稱</th><th>項目狀態</th><th>操作</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach var="projectinformation" items="${ projects }">
						<tr>
							<td>${ projectinformation.projectId }</td>
							<td>${ projectinformation.projectName }</td>
							<td>${ projectinformation.department }</td>
							<td>${ projectinformation.userName }</td>
							<td>${ projectinformation.projectStatu }</td>
							<td>
								<a href="/ssr/project/edit/${ projectinformation.projectId }">修改</a> 
								&nbsp;|&nbsp; 
								<a href="/ssr/project/delete/${ projectinformation.projectId }">刪除</a>
								&nbsp;|&nbsp;
								<form style="display:inline" method="post" action="/ssr/project/delete/${ projectinformation.projectId }">
									<input type="hidden" name="_method" value="DELETE"/>
									<button type="submit">刪除</button>
								</form>
								&nbsp;|&nbsp;
								<a href="#" onclick="deleteProject(${ projectinformation.projectId })">刪除</a>
							</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</div>
		
		<script>
			async function deleteProject(projectId) {
				if(confirm('確定要刪除嗎?')) {
					const response = await fetch('/ssr/project/delete/' + projectId, {method:'DELETE'});
					window.location.href='/ssr/project';
				}
			}
		</script>
		
	</body>
</html>