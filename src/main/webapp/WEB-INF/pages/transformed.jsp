<!doctype html>
<%@include file="pre.jsp"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@page import="social.FBConnection"%>
<%@ page import="org.springframework.social.facebook.api.Facebook" %>
<%
    FBConnection fbConnection = new FBConnection();
%>
<body id="photoPage">
<div id = "mainHeader" >
    <img id="logoImage" src=http://res.cloudinary.com/dr1g3e4hb/image/upload/v1492082557/snhaxbjfbxauwm4jchnq.png alt="logo" />

    <h1 id= "welcomeMainHeader">PICTURE PASSWORD PROTECTOR </h1>
    <h2 id="welcomeHeader">Welcome ${current_user_name} </h2>

</div>

<div id = "mainTabs">
    <a class = "tabButton" href="<c:url value="http://localhost:8080/homepage"/>" class="back_link">HOME</a>
    <a class = "tabButton" href="<c:url value="http://localhost:8080/gallery"/>" class="back_link">TRANSFORMED GALLERY</a>
    <a class = "tabButton" href="<c:url value="http://localhost:8080/help"/>" class="back_link">HELP</a>
    <a class = "tabButton" href="<c:url value="http://localhost:8080/logout"/>" class="back_link">LOGOUT</a>
</div>

<div id = "photoBody">

    <canvas id = "canvas" src="">
        ${returnURL}

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
    </canvas>
        <br>
        <div>
            <a id="addPhotoButton" type="submit" class= "upload_link" href="<c:url value="http://localhost:8080/homepage"/>" class="back_link">Back to Homepage</a>
        </div>
</div>

</body>
<script type='text/javascript'>
    $('.toggle_info').click(function () {
        $(this).next('.photo').show();
        return false;
    });
</script>
<%@include file="post.jsp"%>

<script>
    function getCookie(cname) {
        var name = cname + "=";
        var decodedCookie = decodeURIComponent(document.cookie);
        var ca = decodedCookie.split(';');
        for(var i = 0; i <ca.length; i++) {
            var c = ca[i];
            while (c.charAt(0) == ' ') {
                c = c.substring(1);
            }
            if (c.indexOf(name) == 0) {
                return c.substring(name.length, c.length);
            }
        }
        return "";
    }
</script>

















