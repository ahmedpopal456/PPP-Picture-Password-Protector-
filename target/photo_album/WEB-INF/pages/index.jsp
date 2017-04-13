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
<img style="position: relative; width: 220px; height: 260px;top: 80px;" src="http://res.cloudinary.com/dr1g3e4hb/image/upload/v1492076056/ds3pbycwtnve7n9exhgw.jpg"  />

<body style="text-align: center; margin: 0 auto; background-color: #15a878; background-position: center; background-attachment: fixed">
	<div>
		<h1 style="font-size: 70px; color: white; font-family: Calibri; top: 70px; position: relative;">PICTURE PASSWORD PROTECTOR</h1>
		<h1 style= "font-size: x-large; color: white; font-family: Calibri; top: 100px; position: relative; ">Login</h1>
		<a id = "facebookButton" href="<%=fbConnection.getFBAuthUrl()%>"> <img
			style="margin-top: 90px;" src="https://support.shopgate.com/hc/en-us/article_attachments/207878347/log_in_with_facebook_button.png" />
		</a>
	</div>
</body>
</html>