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
    <h1 id = "welcomeMainHeader">PICTURE PASSWORD PROTECTOR </h1>
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
                <button id="${photo.publicId}" class= "photo">
                    <h2>${photo.title}</h2>
                    <c:if test="${photo.isImage}">
                        <a href="<cl:url storedSrc="${photo}" format="jpg"/>" target="_blank">
                            <cl:image storedSrc="${photo}" extraClasses="thumbnail inline"  width="250" height="150" crop="fit" quality="100" format="jpg"/>
                        </a>
                        <td>
                            <br/>
                        </td>
                    </c:if>
                    <c:if test="${!photo.isImage}">
                        <a href="<cl:url storedSrc="${photo}"/>" target="_blank">Non Image File</a>
                    </c:if>
                </button>
            </c:forEach>
        </c:if>

        <script type='text/javascript'>

            var imageurl = "";
            var imageid = "";
            var imageformat = "";

            var image_id = "";


            function drawImage(url,id,format){
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

                imageurl = url;
                imageid = id;
                image_id = id;
                imageformat = format;

                document.getElementById("imageurl").value = imageurl;
                document.getElementById("imageformat").value = imageformat;
                document.getElementById("imageid").value = imageid;
                document.getElementById("image_id").value = image_id;
            }

            <c:if test="${!empty photos}">
            <c:forEach items="${photos}" var="photo">
            document.getElementById("${photo.publicId}").onclick = function() {drawImage("${photo.url}","${photo.publicId}","${photo.format}")};
            </c:forEach>
            </c:if>
        </script>
    </div>

    <canvas id = "canvas" >

    </canvas>
    <a id="addPhotoButton" class= "upload_link" onclick="popupCenter('upload_form', 'myPop1',600,375);" href="javascript:void(0);">Add Image</a>
    <a id= "addPasswordButton" class= "upload_link" onclick="popupCenter('direct_upload_form', 'myPop1',450,330);" href="javascript:void(0);">List of Passwords</a>

    <form:form cssStyle="border: none" method="post" action = "delete" modelAttribute="returnURL" enctype="multipart/form-data">
        <div id = "deletePhotoButton" class="form_line">
            <div class="form_controls">
                <input id="image_id" name="image_id" type="hidden" value="">
                <input  class="upload_link" type="submit" value="Clear All"/>
            </div>
        </div>
    </form:form>

    <form:form cssStyle="border: none" method="post" action = "transform" modelAttribute="returnURL" enctype="multipart/form-data">
    <div id = "transformButton" class="form_line">
    <div class="form_controls">
    <input id="imageurl" name="imageurl" type="hidden" value="">
    <input id="imageformat" name="imageformat" type="hidden" value="">
    <input id="imageid" name="imageid" type="hidden" value="">
    <input id="password_input1" name="password_input1" type="hidden" value="">
    <input id="password_input2" name="password_input2" type="hidden" value="">
    <input id="password_input3" name="password_input3" type="hidden" value="">
    <input  class="upload_link" type="submit" value="Transform Pictures"/>
    </div>
    </div>
    </form:form>
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

    function popupCenter(url, title, w, h) {
        var left = (screen.width / 2) - (w / 2);
        var top = (screen.height / 2) - (h / 2);
        var win = window.open(url, title, 'toolbar=no, location=no, directories=no, status=no, menubar=no, scrollbars=no, resizable=no, copyhistory=no, width=' + w + ', height=' + h + ', top=' + top + ', left=' + left);
    }

    function confirmExit()
    {
        var cookie1 =getCookie("password1");
        var cookie2 =getCookie("password2");
        var cookie3 =getCookie("password3");

        document.getElementById("password_input1").value = cookie1;
        document.getElementById("password_input2").value = cookie2;
        document.getElementById("password_input3").value = cookie3;
        return true;
    }

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
