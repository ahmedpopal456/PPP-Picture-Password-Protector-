<%@page import="social.FBConnection"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%
	FBConnection fbConnection = new FBConnection();
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Java Facebook Login</title>
</head>
<body style="text-align: center; margin: 0 auto; background-image: url(https://images7.alphacoders.com/352/352540.jpg); background-position: center; background-attachment: fixed">
	<div>
		<a href="<%=fbConnection.getFBAuthUrl()%>"> <img
			style="margin-top: 138px;" src="https://support.shopgate.com/hc/en-us/article_attachments/207878347/log_in_with_facebook_button.png" />
		</a>
	</div>
</body>
</html>