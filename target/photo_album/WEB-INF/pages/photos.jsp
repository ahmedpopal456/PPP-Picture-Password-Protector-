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

<body>
<div id="fb-root"></div>
</body>
<%--<div id="posterframe">--%>
<%--<!-- This will render the fetched Facebook profile picture using Cloudinary according to the--%>
<%--requested transformations. This also shows how to chain transformations -->--%>
<%--<cl:image src="officialchucknorrispage" type="facebook" format="png" height="95" width="95" crop="thumb" gravity="face" effect="sepia" radius="20">--%>
    <%--<jsp:attribute name="transformation">--%>
        <%--<cl:transformation angle="10"/>--%>
    <%--</jsp:attribute>--%>
<%--</cl:image>--%>
<%--</div>--%>

<h1>Welcome ${current_user_name} !</h1>

<p>
    This is a work in progress to simply upload and save images to the Google App Engine Data Store
</p>

<p>
    Select one of the options below. Using the Cloudinary REST API it will upload an image using uploader. This image will be saved into the Data Store
</p>
<div>
    <a href = "/logout">Logout from Facebook</a></div>
</div>
<h1>Your Photos</h1>
<div class="actions">
    <%--<input type=“hidden” name=“userid” value = ${current_user_id}/>--%>
    <a class="upload_link" href="upload_form">Add photo</a>
    <a class="upload_link" href="direct_upload_form">Add photo (direct upload)</a>
</div>
<div class="photos">
    <c:if test="empty photos}">
        <p>No photos were added yet.</p>
    </c:if>

    <canvas id="canvas" width="900" height="800"></canvas>
    <c:if test="${!empty photos}">
        <c:forEach items="${photos}" var="photo">
            <button id="${photo.publicId}" class= "photo">
                <h2>${photo.title}</h2>
                <c:if test="${photo.isImage}">
                    <a href="<cl:url storedSrc="${photo}" format="jpg"/>" target="_blank">
                        <cl:image storedSrc="${photo}" extraClasses="thumbnail inline" width="150" height="150" crop="fit" quality="80" format="jpg"/>
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

            function drawImage(url,id,format){
                var ctx = $("canvas")[0].getContext("2d"),
                    img = new Image();

                img.onload = function(){
                    ctx.drawImage(img, 0, 0, 900 , 800 );
                    $("span").text("Loaded.");
                };
                img.src = url;
                $("span").text("Loading...");

                imageurl = url;
                imageid = id;
                imageformat = format;

                document.getElementById("imageurl").value = imageurl;
                document.getElementById("imageformat").value = imageformat;
                document.getElementById("imageid").value = imageid;

            }

            <c:if test="${!empty photos}">
            <c:forEach items="${photos}" var="photo">
            document.getElementById("${photo.publicId}").onclick = function() {drawImage("${photo.url}","${photo.publicId}","${photo.format}")};
            </c:forEach>
            </c:if>
        </script>
    <%--</div>--%>
    <form:form method="post" action = "transform" modelAttribute="returnURL" enctype="multipart/form-data">
        <div class="form_line">
            <div class="form_controls">
                <input id="imageurl" name="imageurl" type="hidden" value="">
                <input id="imageformat" name="imageformat" type="hidden" value="">
                <input id="imageid" name="imageid" type="hidden" value="">
                <input type="submit" value="Transform Pictures"/>
            </div>
        </div>
    </form:form>

<%@include file="post.jsp"%>