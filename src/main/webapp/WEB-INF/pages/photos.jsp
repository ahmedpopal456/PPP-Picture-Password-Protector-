<!doctype html>
<%@include file="pre.jsp"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

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

    <c:if test="${!empty photos}">
        <c:forEach items="${photos}" var="photo">
            <div class="photo">
                <h2>${photo.title}</h2>
                <c:if test="${photo.isImage}">
                    <a href="<cl:url storedSrc="${photo}" format="jpg"/>" target="_blank">
                        <cl:image storedSrc="${photo}" extraClasses="thumbnail inline" width="150" height="150" crop="fit" quality="80" format="jpg"/>
                    </a>

                    <div class="less_info">
                        <a href="#" class="toggle_info">Show transformations</a>
                    </div>

                    <div class="more_info">
                        <a href="#" class="toggle_info">Hide transformations</a>
                        <table class="thumbnails">
                            <td>
                                <div class="thumbnail_holder">
                                    <cl:image storedSrc="${photo}" extraClasses="thumbnail inline" crop="fill" height="150" width="150" radius="10" format="jpg"/>
                                </div>
                                <table class="info">
                                    <tr><td>crop</td><td>fill</td></tr>
                                    <tr><td>width</td><td>150</td></tr>
                                    <tr><td>height</td><td>150</td></tr>
                                    <tr><td>radius</td><td>10</td></tr>
                                </table>
                                <br/>
                            </td>
                            <td>
                                <div class="thumbnail_holder">
                                    <cl:image storedSrc="${photo}" extraClasses="thumbnail inline" crop="scale" height="150" width="150" format="jpg"/>
                                </div>
                                <table class="info">
                                    <tr><td>crop</td><td>scale</td></tr>
                                    <tr><td>width</td><td>150</td></tr>
                                    <tr><td>height</td><td>150</td></tr>
                                </table>
                                <br/>
                            </td>
                            <td>
                                <div class="thumbnail_holder">
                                    <cl:image storedSrc="${photo}" extraClasses="thumbnail inline" crop="fit" height="150" width="150" format="jpg"/>
                                </div>
                                <table class="info">
                                    <tr><td>crop</td><td>fit</td></tr>
                                    <tr><td>width</td><td>150</td></tr>
                                    <tr><td>height</td><td>150</td></tr>
                                </table>
                                <br/>
                            </td>
                            <td>
                                <div class="thumbnail_holder">
                                    <cl:image storedSrc="${photo}" extraClasses="thumbnail inline" crop="thumb" gravity="face" height="150" width="150" format="jpg"/>
                                </div>
                                <table class="info">
                                    <tr><td>crop</td><td>thumb</td></tr>
                                    <tr><td>gravity</td><td>face</td></tr>
                                    <tr><td>width</td><td>150</td></tr>
                                    <tr><td>height</td><td>150</td></tr>
                                </table>
                                <br/>
                            </td>
                            <td>
                                <div class="thumbnail_holder">
                                    <cl:image storedSrc="${photo}" extraClasses="thumbnail inline" format="png" angle="20">
                                        <jsp:attribute name="transformation">
                                            <cl:transformation crop="fill" gravity="north" height="150" width="150" effect="sepia"/>
                                        </jsp:attribute>
                                    </cl:image>
                                </div>
                                <table class="info">
                                    <tr><td>format</td><td>png</td></tr>
                                    <tr><td>angle</td><td>20</td></tr>
                                    <tr><td colspan="2">and then</td></tr>
                                    <tr><td>crop</td><td>fill</td></tr>
                                    <tr><td>gravity</td><td>north</td></tr>
                                    <tr><td>effect</td><td>sepia</td></tr>
                                    <tr><td>width</td><td>150</td></tr>
                                    <tr><td>height</td><td>150</td></tr>
                                </table>
                                <br/>
                            </td>
                        </table>

                        <div class="note">
                            Take a look at our documentation of <a href="http://cloudinary.com/documentation/image_transformations" target="_blank">Image Transformations</a> for a full list of supported transformations.
                        </div>
                    </div>
                </c:if>
                <c:if test="${!photo.isImage}">
                    <a href="<cl:url storedSrc="${photo}"/>" target="_blank">Non Image File</a>
                </c:if>
            </div>
        </c:forEach>
    </c:if>
</div>
<script type='text/javascript'>
    $('.toggle_info').click(function () {
        $(this).next('.photo').show();
        return false;
    });
</script>
<%@include file="post.jsp"%>