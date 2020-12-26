<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://www.opensymphony.com/sitemesh/decorator" prefix="decorator" %>
<%@ taglib prefix="parakeet" uri="/META-INF/tags/parakeet.tld"%>


<html>
<head>
    <title>Amadeus : Social Networking</title>

    <meta name="viewport" content="width=device-width, initial-scale=1">

    <link rel="icon" type="image/png" href="${pageContext.request.contextPath}/images/favicon.png?v=<%=System.currentTimeMillis()%>">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/app.gap.css?v=<%=System.currentTimeMillis()%>">

    <style type="text/css">

        @media screen and (max-width: 690px) {
            #signup-form-container {
                width: 80% !important;
            }
        }

        #signup-form-container{
            width:51%;
            padding:20px 40px 120px 40px;
        }

        input[type="text"],
        input[type="email"],
        input[type="password"]{
            margin:0.35em 0;
        }

        #amadeus-home-logo{
            -webkit-box-shadow: 0px 3px 49px 0px rgba(0,0,0,.29);
            -moz-box-shadow: 0px 3px 49px 0px rgba(0,0,0,.29);
            box-shadow: 0px 3px 49px 0px rgba(0,0,0,.29);
        }
        #amadeus-symbol{
            font-size:42px;
        }

        .href-dotted,
        .href-dotted-amadeus{
            font-family: Roboto-Bold !important;
        }
    </style>
</head>
<body>

<script src="https://www.google.com/recaptcha/api.js" async defer></script>


<div id="signup-form-container">

    <c:if test="${not empty message}">
        <div class="notify alert-info">${message}</div>
    </c:if>

    <a href="/o/uno" id="amadeus-home-logo" style="background:#0ad5fc;display:inline-block;padding:28px 33px;text-decoration: none;">
        <span class="medium" id="amadeus-symbol" style="display:block">&Delta;</span>
    </a>

    <h1 style="font-size:32px;font-family: Roboto-Bold !important;margin-top:20px;">Amadeus</h1>

    <p id="amadeus-words">Social Networking</p>


    <form action="${pageContext.request.contextPath}/register" modelAttribute="account" method="post" enctype="multipart/form-data" autocomplete="false" class="pure-form pure-form-stacked" id="registration-form">
        <fieldset>

            <h1 style="font-family:Roboto-Bold !important;font-size:23px;margin-bottom:20px;">Signup!</h1>

<%--                <p>Create your account and begin sharing.</p>--%>

            <input type="hidden" name="uri" value="${uri}"/>

            <input id="name" type="text" placeholder="Name" name="name" style="width:100%">

            <input id="username" type="email" placeholder="Email Address" name="username" style="width:100%;">

            <input id="password" type="password" placeholder="Password &#9679;&#9679;&#9679;" name="password" style="width:100%;">

<!--
            <p style="text-align: center;">
                <span id="summation" class="yella" style="font-size:27px;"></span>
                <input type="text" placeholder="" id="value" style="width:50px;"/>
            </p>

            <p class="notify" id="verdict" style="display:none"></p>
-->

            <div class="g-recaptcha" data-sitekey="6Lfr1OMZAAAAADF6n5E_Z58iu-qyqAb0AnrElSGk" style="margin-top:30px;"></div>

        </fieldset>
    </form>


    <div style="width:100%;margin-top:41px;text-align:center;margin-bottom:30px; ">
        <input type="submit" class="button yella" id="signup-button" value="Signup!" style="width:100%;"/>

        <p style="text-align: left; margin-top:30px;">Are you already one of our delicate flowers?</p>

        <a href="${pageContext.request.contextPath}/" class="button modern" id="signin-button" style="width:80%;">Signin!</a>

    </div>

    <br class="clear"/>

</div>


<script>


    var processing = false

    //var pass = "You may proceed..."

    var email = document.getElementById("username")
    var password = document.getElementById("password")
    var form = document.getElementById("registration-form")
    var signupButton = document.getElementById("signup-button")

    /**
    var summationP = document.getElementById("summation")
    var summationInput = document.getElementById("value")
    var verdictP = document.getElementById("verdict")

    signupButton.setAttribute("disabled", true)

    var numOne = getRandom()
    var numTwo = getRandom()

    var z = numOne + numTwo

    var summationText = numOne + " + " + numTwo + " = ";
    summationP.innerHTML = summationText

    summationInput.addEventListener("input", function(event){
        verdictP.style.display = "block"
        if(z == summationInput.value){
            verdictP.innerHTML = pass
            signupButton.removeAttribute("disabled")
        }else{
            verdictP.style.display = "none"
            signupButton.setAttribute("disabled", disabled)
        }
    })
    **/

    signupButton.addEventListener("click", function(event){
        event.preventDefault();
        if(!processing){
            processing = true;
            form.submit();
        }
    })

    setTimeout(function(){
        password.value = ""
        email.value = ""
    }, 1000)


    function getRandom(){
        return Math.ceil(Math.random()* 20);
    }

</script>
</body>
</html>