<%@ taglib uri="http://www.opensymphony.com/sitemesh/decorator" prefix="decorator" %>
<%@ taglib uri="http://shiro.apache.org/tags" prefix="shiro"%>
<%@ taglib prefix="parakeet" uri="/META-INF/tags/parakeet.tld"%>

<%@ page import="io.github.mcroteau.Parakeet" %>
<%@ page import="social.amadeus.common.BeanLookup" %>

<html>
<head>
    <title>Amadeus : Rock Me Amadeus!</title>

    <meta name="viewport" content="width=device-width, initial-scale=1">

    <link rel="icon" type="image/png" href="${pageContext.request.contextPath}/images/favicon.png?v=<%=System.currentTimeMillis()%>">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/css/app.css?v=<%=System.currentTimeMillis()%>">

</head>
<body>

    <style type="text/css">
        body{
            /**background-image:url("/b/images/amadeus-home.jpg");**/
            background-position:0px 0px;
            background-color:#fcfcfc;
            background-position:0px 0px;
            background: rgb(253,254,3);
            background: linear-gradient(36deg, rgba(253,254,3,1) 0%, rgba(253,254,3,1) 50%, rgba(84,175,255,1) 100%);
            background: rgb(253,254,3);
            background: linear-gradient(36deg, rgba(253,254,3,1) 0%, rgba(253,254,3,1) 13%, rgba(253,254,3,1) 20%, rgba(84,175,255,1) 20%, rgba(192,225,255,1) 100%) !important;/*-moz-box-shadow: 0px 4px 17px 0px rgba(0,0,0,.9);*/
            background: rgb(192,225,255);
            background: linear-gradient(36deg, rgba(192,225,255,1) 0%, rgba(84,175,255,1) 15%, rgba(253,254,3,1) 15%, rgba(253,254,3,1) 90%, rgba(84,175,255,1) 90%, rgba(192,225,255,1) 100%) !important;
            background: rgb(253,254,3);
            background: linear-gradient(60deg, rgba(253,254,3,1) 0%, rgba(253,254,3,1) 33%, rgba(253,254,3,1) 60%, rgba(84,175,255,1) 90%, rgba(192,225,255,1) 100%) !important;/*-moz-box-shadow: 0px 4px 17px 0px rgba(0,0,0,.9);*/
        }

        #guest-content-left{
            float:left;
        }

        #guest-content-right{
            float:right;
            color:#2b2b34 !important;
            width:27%;
            margin-right:4%;
            margin-top:3px;
            text-align:left;
            background:rgba(67, 167, 251, 1);
            background: linear-gradient(-31deg, rgba(67,136,251,1) 0%, rgba(67,179,251,1) 100%);
            background:rgba(255,255,255, 1);

            padding:20px 30px;

            border-radius: 7px !important;
            -moz-border-radius: 7px !important;
            -webkit-border-radius: 7px !important;

            -webkit-box-shadow: 1px 3px 12px 0px rgba(0,0,0,.17)
            -moz-box-shadow: 1px 3px 12px 0px rgba(0,0,0,.17);
            box-shadow: 1px 3px 12px 0px rgba(0,0,0,.17);
        }

        #guest-content-right p,
        #guest-content-right h2{
            color:#2b2b34;
        }

        #guest-content-right .light{
            color:#2b2b34;
        }

        #guest-content-container{
            margin:0px auto 0px auto;
            border:solid 0px #ddd;
            width:100%;
        }

        #amadeus-logo-container{
            float:left;
            margin-top:71px;
            position:relative;
            width:239px;
        }

        #amadeus-logo-deg{
            color:#617078 !important;
            margin-top:-181px;
            margin-left:-20px;
        }

        #logo-logo{
            color:#617078;
            font-family:Roboto-Bold !important;
        }

        #amadeus-logo-container h1{
            font-size:72px;
            font-family:Roboto-Black !important;
            margin-left:8px;
        }

        #amadeus-logo-container p{
            font-family:'RobotoBold';
        }

        #inspectasexajexa{
            top:3px;
            left:30px;
            position:absolute;
            height:40px;
            width:50px;
            cursor:pointer;
            cursor:hand;
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
        @media screen and (max-width: 590px) {
            #guest-content-container{
                width:100%;
                margin:0px auto 0px auto;
            }
            #guest-content-left{
                width:80% !important;
                float:none !important;
            }
            #guest-content-right{
                float:none !important;
                width:90% !important;
                margin:0px auto;
            }
            #get_code{
                display:none;
            }


            <% if(request.getServletPath().equals("/signup")){ %>
                 #amadeus-words {
                    display:none;
                 }
            <%}%>
        }

        #amadeus-logo-home{
            width:173px;
            height:173px;
        }
    </style>


    <div id="guest-content-container">

        <div id="guest-content-left" style="padding:3% 5%; width:46%;">
            <a href="/o/uno">
                <span style="display:inline-block;">
                    <svg id="amadeus-logo-home" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 171 171" width="171" height="171">
                        <path d="M73 108L38 108L92 21L107 21L91 67L129 67L74 154L58 154L73 108Z"/>
                    </svg>
                </span>
            </a>

            <p style="font-weight: normal; font-size:32px;font-family:Georgia !important;">Amadeus</p>

            <div id="amadeus-words">

                <p>Amadeus is a Social Networking app built with Love.</p>

                <p>Why should there be many to rule social
                    media when there is <strong>Amadeus</strong>! <a href="${pageContext.request.contextPath}/signup" class="href-dotted-black">Signup!</a></p>

            </div>

        </div>

        <parakeet:anonymous>
            <div id="guest-content-right">
                <decorator:body />
            </div>
        </parakeet:anonymous>
        <parakeet:isAuthenticated>

            <div style="float:right;margin:30px 30px 0px 0px;">
                Welcome back <strong>${sessionScope.account.nameUsername}</strong> my child!
                &nbsp;|&nbsp;
                <a href="${pageContext.request.contextPath}/signout" class="href-dotted-amadeus">Signout</a>
                <br/>
                <br/>
                <a href="${pageContext.request.contextPath}/" class="href-dotted-amadeus">Home</a>&nbsp;|&nbsp;
                    <a href="${pageContext.request.contextPath}/account/edit/${sessionScope.account.id}" class="href-dotted-amadeus">Edit Profile</a>
            </div>

        </parakeet:isAuthenticated>

        <br class="clear"/>
    </div>

    <div style="margin-bottom:167px;"></div>

<%--    <div style="background:#1b1b1b;height:130px;"></div>--%>

    <script async src="https://www.googletagmanager.com/gtag/js?id=UA-40862316-16"></script>
    <script>
      window.dataLayer = window.dataLayer || [];
      function gtag(){dataLayer.push(arguments);}
      gtag('js', new Date());

      gtag('config', 'UA-40862316-16');
    </script>

</body>
</html>