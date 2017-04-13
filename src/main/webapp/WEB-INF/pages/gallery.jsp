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

    <div id="galleryContainer">
        <c:if test="empty photos}">
            <p>No photos were added yet.</p>
        </c:if>
        <c:if test="${!empty photos}">
            <c:forEach items="${photos}" var="photo">
                <button id="${photo.title}" class= "photo">
                        <a href="<cl:url src="${photo.publicId}" format="${photo.format}"/>" target="_blank">
                            <cl:image src="${photo.publicId}" extraClasses="thumbnail inline" width="250" height="150" crop="fit" quality="100" format="jpg"/>
                        </a>
                        <td>
                            <br/>
                        </td>
                </button>
            </c:forEach>
        </c:if>

        <script type='text/javascript'>

            function drawImage(url){
                var ctx = $("canvas")[0].getContext("2d"),
                    img = new Image();

                ctx.canvas.width = window.innerWidth*0.78;
                ctx.canvas.height = window.innerHeight*0.90;

                img.onload = function(){
                    ctx.drawImage(img, 0, 0, ctx.canvas.width, ctx.canvas.height);
                    $("span").text("Loaded.");
                };
                img.src =  url;
                $("span").text("Loading...");
            }

            <c:if test="${!empty photos}">
            <c:forEach items="${photos}" var="photo">
            document.getElementById("${photo.title}").onclick = function() {drawImage("${photo.publicId}")};
            </c:forEach>
            </c:if>
        </script>
    </div>
    <canvas id = "canvas"></canvas>
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
