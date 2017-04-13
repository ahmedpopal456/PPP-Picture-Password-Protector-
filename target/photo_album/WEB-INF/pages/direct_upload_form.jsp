<!doctype html>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@include file="pre.jsp"%>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
    <title>Please Input Up to Three Passwords</title>
</head>
<p style="font-size:14px; font-style:normal;">Please Input Up to Three Passwords</p>
<br>
<div class="input-group">
    <span class="input-group-addon" id="basic-addon1">@</span>
    <input id="password1" type="text" class="form-control" placeholder="Password 1" aria-describedby="basic-addon1">
</div>
<br>
<div class="input-group">
    <span class="input-group-addon" id="basic-addon2">@</span>
    <input id="password2" type="text" class="form-control" placeholder="Password 2" aria-describedby="basic-addon1">
</div>
<br>
<div class="input-group">
    <span class="input-group-addon" id="basic-addon3">@</span>
    <input id="password3" type="text" class="form-control" placeholder="Password 3" aria-describedby="basic-addon1">
</div>
<br>
<a id="input_text" class= "upload_link" onclick="submitForm();" href="javascript:void(0);">Submit Form</a>
</body>
<%@include file="post.jsp"%>
</html>

<script>
    function submitForm() {

        var password1 = document.getElementById("password1").value;
        var password2 = document.getElementById("password2").value;
        var password3 = document.getElementById("password3").value;

        if (!(password1 == "")){

            setCookie("password1", password1, 1);
        }
        else
        {
            setCookie("password1", "", 1);
        }

        if (!(password2 == "")){

            setCookie("password2", password2, 1);
        }
        else
        {
            setCookie("password2", "", 1);

        }

        if (!(password3 == "")){

            setCookie("password3", password3, 1);
        }
        else
        {
            setCookie("password3", "", 1);
        }

        window.opener.confirmExit();
        window.close();
    }
</script>

<script>
    function setCookie(cname, cvalue, exdays) {
        var d = new Date();
        d.setTime(d.getTime() + (exdays*24*60*60*1000));
        var expires = "expires="+ d.toUTCString();
        document.cookie = cname + "=" + cvalue + ";" + expires + ";path=/";
    }
</script>
<%@include file="post.jsp"%>
