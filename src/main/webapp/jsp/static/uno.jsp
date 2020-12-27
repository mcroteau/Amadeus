<%@ taglib uri="http://www.opensymphony.com/sitemesh/decorator" prefix="decorator" %>
<%@ taglib prefix="parakeet" uri="/META-INF/tags/parakeet.tld"%>

<html>
<head>
    <title>Amadeus : Social Networking</title>

    <meta name="viewport" content="width=device-width, initial-scale=1">

    <link rel="icon" type="image/png" href="${pageContext.request.contextPath}/images/icon.png?v=<%=System.currentTimeMillis()%>">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/app.gap.css?v=<%=System.currentTimeMillis()%>">

    <style type="text/css">

        @media screen and (max-width: 690px) {
            #uno-content-container {
                width: 80% !important;
            }
        }

        #amadeus-logo-container h1{
            font-size:72px;
            font-family:Roboto-Black !important;
            margin-left:8px;
        }

        #amadeus-logo-container p{
            font-family:'RobotoBold';
        }

        #uno-content-container{
            width:51%;
            padding:20px 40px 120px 40px;
        }

        h2{
            font-size:32px;
            font-family:Roboto-Bold !important;
            line-height:1.3em;
        }

        .button{
            -webkit-box-shadow: 0px 1px 7px 0px rgba(0,0,0,0);
            -moz-box-shadow: 0px 1px 7px 0px rgba(0,0,0,0);
            box-shadow: 0px 1px 7px 0px rgba(0,0,0,0);
        }
        .button:hover,
        .retro:hover,
        .yella:hover{
            -webkit-box-shadow: 0px 1px 7px 0px rgba(0,0,0,0) !important;
            -moz-box-shadow: 0px 1px 7px 0px rgba(0,0,0,0) !important;
            box-shadow: 0px 1px 7px 0px rgba(0,0,0,0) !important;
        }
        input[type="button"],
        .button,
        .retro,
        .yella{
            width:80%;
            text-align:center;
        }
        p{
            line-height:1.4em;
            font-family:Roboto !important;
        }

        a{
            font-family:Roboto !important;
        }
        p{
            margin:0px;
            padding:0px;
        }

        #amadeus-words{
            margin-bottom:30px;
        }

        #amadeus-home-logo{
            border-radius: 4px;
            -moz-border-radius: 4px;
            -webkit-border-radius: 4px;
            background: rgb(255,69,174);
            background: linear-gradient(180deg, rgba(255,69,174,1) 0%, rgba(249,255,34,1) 14%, rgba(45,245,0,1) 37%, rgba(84,175,255,1) 59%, rgba(113,11,170,1) 100%);

            -webkit-box-shadow: 0px 3px 49px 0px rgba(0,0,0,.29);
            -moz-box-shadow: 0px 3px 49px 0px rgba(0,0,0,.29);
            box-shadow: 0px 3px 49px 0px rgba(0,0,0,.29);
        }

        #amadeus-symbol{
            color: #fff;
            font-size:42px;
            text-shadow: -2px 0px #710baa;
        }
        .href-dotted,
        .href-dotted-amadeus{
            font-family: Roboto-Bold !important;
        }
    </style>
</head>
<body>
<div id="uno-content-container">

    <c:if test="${not empty message}">
        <div class="span12">
            <p>${message}</p>
        </div>
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

    <a href="/o/uno" id="amadeus-home-logo" style="display:inline-block;padding:27px 25px 21px 20px;text-decoration: none;">
        <svg id="amadeus-logo" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 171 171" width="171" height="171">
            <path d="M73 108L38 108L92 21L107 21L91 67L129 67L74 154L58 154L73 108Z"/>
        </svg>
        <%--        <span class="medium" id="amadeus-symbol" style="display:block">&Delta;</span>--%>
    </a>

    <h1 style="font-size:32px;font-family: Roboto-Bold !important;margin-top:20px;">Amadeus</h1>

    <p id="amadeus-words">Social Networking <a href="${pageContext.request.contextPath}/signup" class="href-dotted">Signup!</a></p>



    <h2 style="margin-bottom:20px;">Signin</h2>

    <form action="${pageContext.request.contextPath}/authenticate" modelAttribute="signon" method="post" >

        <input type="hidden" name="uri" value="${uri}"/>

        <div class="form-group">
            <label for="username">Username</label>
            <input type="text" name="username" class="form-control" id="username" placeholder=""  value=""  style="width:100%;">
        </div>

        <div class="form-group">
            <label for="password">Password</label>
            <input type="password" name="password" class="form-control" id="password" style="width:100%;" value=""  placeholder="&#9679;&#9679;&#9679;&#9679;&#9679;&#9679;">
        </div>

        <div style="text-align:right; margin-top:30px;">
            <input type="hidden" name="targetUri" value="${targetUri}" />
            <input type="submit" class="button retro" value="Signin" style="width:100%;">
        </div>

        <br/>

        <br/>
        <a href="${pageContext.request.contextPath}/account/reset" class="href-dotted">Forgot Password</a>&nbsp;&nbsp;

    </form>


    <div id="signup-container" style="text-align: center;margin-top:51px">
        <a href="${pageContext.request.contextPath}/signup" class="button modern large">Sign Up !</a>
    </div>

</div>
</body>
</html>
