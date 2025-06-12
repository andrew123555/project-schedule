<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
	<head>
		<meta charset="UTF-8">
		<title>ProjectIM List</title>
	</head>
	<body>
		<%@ include file="include/IMmenu.jsp" %>
		<div>
			<form method="post" action="/ssr/project/item/add">
				類別: <input type="text" name="classification" required /><br />
				待辦事項: <input type="text" name="toDoList" required /><br />
				開始時間: <input type="text" name="scheduleStartTime" required /><br />
				結束時間: <input type="text" name="scheduleEndTime" required /><br />
				負責人: <input type="text" name="userName" required /><br />
				機密性: <input type="checkbox" name="confidential" /><br />
				<button type="submit">送出</button>
			</form>
		</div>
		<div>
			<table border="1">
				<thead>
					<tr>
						<th>類別</th><th>待辦事項</th><th>開始時間</th><th>結束時間</th><th>負責人</th><th>機密性</th><th>操作</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach var="projectinformationD" items="${ projectIMs }">
						<tr>
							<td>${ projectinformationD.classification }</td>
							<td>${ projectinformationD.toDoList }</td>
							<td>${ projectinformationD.scheduleStartTime }</td>
							<td>${ projectinformationD.scheduleEndTime }</td>
							<td>${ projectinformationD.userName }</td>
							<td>${ projectinformationD.confidential }</td>
							<td>
								<a href="/ssr/project/item/edit/${ projectinformationD.toDoListId }">修改</a> 
								&nbsp;|&nbsp; 
								<a href="/ssr/project/item/delete/${ projectinformationD.toDoListId }">刪除</a>
								&nbsp;|&nbsp;
								<form style="display:inline" method="post" action="/ssr/project/item/delete/${ projectinformationD.toDoListId }">
									<input type="hidden" name="_method" value="DELETE"/>
									<button type="submit">刪除</button>
								</form>
								&nbsp;|&nbsp;
								<a href="#" onclick="deleteProject(${ projectinformationD.toDoListId })">刪除</a>
							</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</div>
		
		<script>
			async function deleteProject(toDoListId) {
				if(confirm('確定要刪除嗎?')) {
					const response = await fetch('/ssr/project/item/delete/' + toDoListId, {method:'DELETE'});
					window.location.href='/ssr/project/item';
				}
			}
		</script>
		
	</body>
</html>