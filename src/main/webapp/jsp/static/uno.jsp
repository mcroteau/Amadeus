<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://www.opensymphony.com/sitemesh/decorator" prefix="decorator" %>
<%@ taglib prefix="parakeet" uri="/META-INF/tags/parakeet.tld"%>

<div id="uno-content-container">

    <c:if test="${not empty message}">
        <p>${message}</p>
    </c:if>

    <parakeet:isAuthenticated>
        <div style="margin-bottom:30px;">
            Welcome back <strong>${sessionScope.account.nameUsername}</strong> my child!
            <br/>
            <br/>
            <a href="${pageContext.request.contextPath}/" class="href-dotted-amadeus">Home</a>&nbsp;|&nbsp;
            <a href="${pageContext.request.contextPath}/account/edit/${sessionScope.account.id}" class="href-dotted-amadeus">Edit Profile</a>&nbsp;|&nbsp;
            <a href="${pageContext.request.contextPath}/signout" class="href-dotted-amadeus">Signout</a>
        </div>
    </parakeet:isAuthenticated>

    <div style="float:right;text-align: right">
        <a href="/o/signup" class="href-dotted">Signup</a>&nbsp;
        <a href="/o/signin" class="href-dotted">Signin</a>
    </div>

    <a href="/o/uno" id="amadeus-home-logo">
<%--        <svg id="amadeus-icon" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 130 130">--%>
<%--            <path id="amadeus-icon-path" d="M57.46 78.91L36.35 78.87L54.53 30.08L75.83 30.16L68.2 52.86L92 52.85L57.55 115L56.95 115L57.46 78.91ZM57.46 78.91L36.31 78.99L54.62 30.16L75.81 30.07L68.2 52.86L92 52.86L57.55 115L56.95 115L57.46 78.91Z"/>--%>
<%--        </svg>--%>
    <svg id="amadeus-icon" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 70 70" width="70" height="70">
        <g id="amadeus">
            <path id="Shape 13" class="shp0" d="M46 31L46 36L57.01 36L57.06 40.91L62 41L62 31L46 31Z" />
            <path id="Shape 10" fill-rule="evenodd" class="shp1" d="M16 20L21 20L32 48L4 48L16 20ZM11 44L25 44L19 26L18 26L11 44Z" />
        </g>
        <path id="Shape 13" fill-rule="evenodd" class="shp0" d="M38.5 23C36.01 23 34 20.99 34 18.5C34 16.01 36.01 14 38.5 14C40.99 14 43 16.01 43 18.5C43 20.99 40.99 23 38.5 23ZM40.6 18.5C40.6 17.34 39.66 16.4 38.5 16.4C37.34 16.4 36.4 17.34 36.4 18.5C36.4 19.66 37.34 20.6 38.5 20.6C39.66 20.6 40.6 19.66 40.6 18.5Z" />
    </svg>
        <%--        <span class="medium" id="amadeus-symbol" style="display:block">&Delta;</span>--%>
    </a>

    <h1 style="font-size:32px;font-family: Roboto-Bold !important;margin-top:20px;">Amadeus</h1>
    <p id="amadeus-words">Social Networking</p>



    <p style="font-size:23px !important;">Amadeus is a theophoric given name
        derived from the Latin words ama - the imperative of the word
        <strong>amare</strong> (to love) - and <strong>deus</strong> (god).
        <br/>As a linguistic compound in the form of a phereoikos...<br/>
        in simple terms, it means... <strong>Love God</strong>.</p>

    <p style="font-size:23px !important;">My name is
        <input type="text" name="name" placeholder="Mitch" style="width:170px;">
        I fear God!<br/>I want to be one of Amadeus's children
        <input type="text" name="email" placeholder="cira@gmail.com"> is my email.
        <button class="button yella signmeup" style="width:auto !important;">Sign me up!</button>
    </p>

    <style>
        input[type="text"],
        input[type="text"]:hover,
        input[type="text"]:focus{
            background: #fff;
            border:none !important;
            outline: none;
            font-size:23px;
            padding:0 !important;
            border-bottom:solid 1px #ddd !important;
        }
        input[type="text"]::placeholder{
            font-size: 23px;
        }

        .signmeup,
        .signmeup:hover{
            -webkit-box-shadow: 0px 1px 7px 0px rgba(179,179,179,0.23) !important;
            -moz-box-shadow: 0px 1px 7px 0px rgba(179,179,179,0.23) !important;
            box-shadow: 0px 1px 7px 0px rgba(179,179,179,0.23) !important;
        }

    </style>

</div>