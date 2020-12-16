<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<style type="text/css">
    #signup-button-old{
    	-webkit-animation-name: amadeus; /* Safari 4.0 - 8.0 */
        -webkit-animation-duration: 23s; /* Safari 4.0 - 8.0 */
        animation-name: amadeus;
        animation-iteration-count: infinite;
        animation-duration: 23s;
    }
    input[type="text"],
    input[type="email"],
    input[type="password"]{
        margin:0.35em 0;
        width:90% !important;
    }

</style>

<script src="https://www.google.com/recaptcha/api.js" async defer></script>


<div id="registration-form-container" style="width:90%;padding-top:20px;text-align: center">

    <c:if test="${not empty message}">
        <div class="notify alert-info">${message}</div>
    </c:if>

    <c:if test="${not empty error}">
        <div class="notify alert-danger">${error}</div>
    </c:if>

    <div id="registration-form-container" style="margin:auto; text-align: left;">

        <form action="${pageContext.request.contextPath}/register" modelAttribute="account" method="post" enctype="multipart/form-data" autocomplete="false" class="pure-form pure-form-stacked" id="registration-form">
            <fieldset>

                <h1 style="font-family:Roboto-Bold !important;font-size:23px;margin-bottom:20px;">Signup!</h1>

                <p>Create your account and begin sharing with friends &amp; family.</p>

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


        <div style="margin-top:41px;text-align:center; width:inherit;margin-bottom:30px; ">
            <input type="submit" class="button retro" id="signup-button" value="Signup!" style="width:100%;"/>

            <p style="text-align: left; margin-top:30px;">Are you already one of our delicate flowers?</p>

            <a href="${pageContext.request.contextPath}/" class="button amadeus" id="signin-button" style="width:100%;">Signin!</a>

        </div>

        <br class="clear"/>

    </div>
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
