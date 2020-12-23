<%@ taglib uri="http://www.opensymphony.com/sitemesh/decorator" prefix="decorator" %>
<%@ page import="io.github.mcroteau.Parakeet" %>
<%@ page import="social.amadeus.common.BeanLookup" %>

<html>
<head>
    <title>Amadeus : Rock Me Amadeus!</title>

    <meta name="viewport" content="width=device-width, initial-scale=1">

    <link rel="icon" type="image/png" href="${pageContext.request.contextPath}/images/icon.png?v=<%=System.currentTimeMillis()%>">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/css/app.css?v=<%=System.currentTimeMillis()%>">
</head>
<body>

    <style type="text/css">

      @media screen and (max-width: 590px) {
            #guest-content-container{
                width:100%;
                margin:0px auto 0px auto;
            }
            #guest-content-right{
                float:none;
                margin:30px !important;
            }
            #amadeus-logo-container{
                display:none !important;
            }
            #content-mobile{
                display:block !important;
            }
        }

        body{
            background:#fff;
        }

        #guest-content-right{
            float:left;
            margin-left:100px;
            margin-top:24px;
            width:300px;
        }

        #guest-content-container{
            margin:0px auto 0px auto;
            width:760px; 
            border:solid 0px #ddd;
        }

        #amadeus-logo-container{
            float:left;
            margin-top:71px;
            position:relative;
            width:339px;
        }

        #amadeus-logo-deg{
            color:#617078 !important;
            margin-top:-181px;
            margin-left:-20px;
        }

        #amadeus-logo-container h1{
            font-size:72px;
            font-family:Roboto-Bold !important;
            margin-left:8px;
        }

        #amadeus-logo-container p{
            font-family:Roboto;
        }

        #guest-header{
            text-align:center;
            margin-bottom:29px;
        }

        h1{
            color:#000;
            text-align:center;
        }

        h2{
            font-size:32px;
            font-family:Roboto-Bold !important;
            line-height:1.3em;
        }

        p{
            font-family:Roboto !important;
        }
        a{
            font-family:Roboto-Medium !important;
        }

        #amadeus{
            height:121px;
            fill:#43a7fb;
            fill:#000;
        }
    </style>

    <div id="top-outer-container">
        <a href="/o/" id="logo-logo">
            <span class="medium" id="amadeus-symbol">&Delta;</span>
<%--            <svg id="amadeus-logo" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 171 171" width="171" height="171">--%>
<%--                <path d="M73 108L38 108L92 21L107 21L91 67L129 67L74 154L58 154L73 108Z"/>--%>
<%--            </svg>--%>
        </a>
        <br class="clear"/>
    </div>

    <div id="guest-content-container">

        <div id="guest-content-right">

            <div id="guest-header">

                <br class="clear"/>

                <%
                    BeanLookup beanLookup = new BeanLookup();
                    Parakeet parakeet = (Parakeet) beanLookup.get("parakeet");
                %>

                <%if(parakeet.isAuthenticated()){%>

                    Hello <strong>${sessionScope.account.nameUsername}</strong>!
                    &nbsp;|&nbsp;
                    <a href="${pageContext.request.contextPath}/signout" class="href-dotted">Signout</a>
                    <br/>
                    <br/>
                    <a href="${pageContext.request.contextPath}/" class="href-dotted">Home</a>&nbsp;|&nbsp;
                    <a href="${pageContext.request.contextPath}/account/edit/${sessionScope.account.id}" class="href-dotted">Edit Profile</a>&nbsp;|&nbsp;
                <a href="${pageContext.request.contextPath}/flyer/list/${sessionScope.account.id}" class="href-dotted">Advertise</a>

                <%}else{%>
                    <a href="${pageContext.request.contextPath}/?uri=${uri}" class="href-dotted">Signin</a>
                    &nbsp;
                    <a href="${pageContext.request.contextPath}/signup?uri=${uri}" class="href-dotted">Signup!</a>
                <%}%>

            </div>

            <decorator:body />
        </div>

        <br class="clear"/>
    </div>


    <script async src="https://www.googletagmanager.com/gtag/js?id=UA-40862316-16"></script>
    <script>
      window.dataLayer = window.dataLayer || [];
      function gtag(){dataLayer.push(arguments);}
      gtag('js', new Date());

      gtag('config', 'UA-40862316-16');
    </script>

</body>
</html>