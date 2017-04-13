<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@page import="social.FBConnection"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
         pageEncoding="ISO-8859-1"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
    <title>Java Facebook Login</title>
</head>
<body style="text-align: center; margin: 0 auto; background-image: url(https://images7.alphacoders.com/352/352540.jpg); background-position: center; background-attachment: fixed">
<div>
${returnURL}
</div>
</body>
<form:form cssStyle="border: none" method="post" action = "save" modelAttribute="returnURL" enctype="multipart/form-data">
    <div id = "transformButton" class="form_line">
        <div class="form_controls">
            <input id="imageurl" name="imageurl" type="hidden" value= "${returnURL}">
            <input id="imageformat" name="imageformat" type="hidden" value= "${format}">
            <input id="imageid" name="imageid" type="hidden" value= "${publicid}">
            <input  class="upload_link" type="submit" value="Save Picture"/>
        </div>
    </div>
</form:form>
<br>
    <div>
         <a id="addPhotoButton" type="submit" class= "upload_link" href="<c:url value="http://localhost:8080/homepage"/>" class="back_link">Back to Homepage</a>
    </div>
<%@include file="post.jsp"%>
</html>

