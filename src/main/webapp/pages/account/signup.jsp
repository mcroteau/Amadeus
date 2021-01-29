<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://www.opensymphony.com/sitemesh/decorator" prefix="decorator" %>
<%@ taglib prefix="parakeet" uri="/META-INF/tags/parakeet.tld"%>

<script src="https://www.google.com/recaptcha/api.js" async defer></script>

<div id="signup-form-container">

    <c:if test="${not empty message}">
        <div class="notify alert-info">${message}</div>
    </c:if>

    <a href="/o/home" id="amadeus-home-logo">
        <svg id="amadeus-icon" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 70 70" width="70" height="70">
            <g id="amadeus">
                <path d="M46 31L46 36L57.01 36L57.06 40.91L62 41L62 31L46 31Z" />
                <path fill-rule="evenodd" d="M16 20L21 20L32 48L4 48L16 20ZM11 44L25 44L19 26L18 26L11 44Z" />
            </g>
            <path fill-rule="evenodd" d="M38.5 23C36.01 23 34 20.99 34 18.5C34 16.01 36.01 14 38.5 14C40.99 14 43 16.01 43 18.5C43 20.99 40.99 23 38.5 23ZM40.6 18.5C40.6 17.34 39.66 16.4 38.5 16.4C37.34 16.4 36.4 17.34 36.4 18.5C36.4 19.66 37.34 20.6 38.5 20.6C39.66 20.6 40.6 19.66 40.6 18.5Z" />
        </svg>
    </a>

    <h1 style="font-size:32px;font-family: Roboto-Bold !important;margin-top:20px;">Amadeus</h1>

    <p id="amadeus-words">Social Networking</p>

    <p style="text-align: left; margin-top:30px;">Are you already one of our delicate flowers?
        <a href="${pageContext.request.contextPath}/signin" class="href-dotted">Signin!</a>
    </p>


    <form action="${pageContext.request.contextPath}/register" modelAttribute="account" method="post" enctype="multipart/form-data" autocomplete="false" class="pure-form pure-form-stacked" id="registration-form">
        <fieldset>

            <h2 style="margin-bottom:20px;">Signup</h2>

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
        <input type="submit" class="button retro" id="signup-button" value="Signup!" style="width:100%;"/>
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
