<%@ taglib uri="http://www.opensymphony.com/sitemesh/decorator" prefix="decorator" %>
<%@ page import="xyz.strongperched.Parakeet" %>
<%@ page import="social.amadeus.common.BeanLookup" %>

<html>
<head>
    <title>Amadeus : astrophysical*</title>

    <meta name="viewport" content="width=device-width, initial-scale=1">

    <link rel="icon" type="image/png" href="${pageContext.request.contextPath}/images/icon.png?v=<%=System.currentTimeMillis()%>">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/css/app.gap.css?v=<%=System.currentTimeMillis()%>">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/app.astro.css?v=<%=System.currentTimeMillis()%>">
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

        #header{
            text-align: center;
            margin:0px auto 40px;
        }

        #auth-container{
            width:490px;
        }

        #content-container{
            width:490px;
            margin:0px auto 101px auto;
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
            font-size: 49px;
            text-align: left;
        }

        h2{
            font-size:32px;
            font-family:Roboto-Bold !important;
            line-height:1.3em;
        }

        p{
            font-size: 19px;
            font-family:Roboto !important;
        }
        a{
            font-family:Roboto-Medium !important;
        }

        /*#logo-container,*/
        /*#logo-container:hover{*/
        /*    background: rgb(214,200,137);*/
        /*    background: linear-gradient(117deg, rgba(214,200,137,1) 14%, rgba(59,29,18,1) 100%) !important;*/
        /*}*/
        #amadeus-logo{
            fill:#fff;
        }
        .form-row label{
            display:block;
            margin:0px;
        }
        .form-row{
            margin:0px auto 10px;
        }
    </style>

    <div id="top-outer-container">
        <div id="logo-container">
            <a href="/o/" id="logo-logo">
                <%--            <span class="medium" id="amadeus-symbol">&Delta;</span>--%>
                <%--            <svg id="amadeus-logo" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 130 130">--%>
                <%--                <path id="amadeus-icon-path" d="M57.46 78.91L36.35 78.87L54.53 30.08L75.83 30.16L68.2 52.86L92 52.85L57.55 115L56.95 115L57.46 78.91ZM57.46 78.91L36.31 78.99L54.62 30.16L75.81 30.07L68.2 52.86L92 52.86L57.55 115L56.95 115L57.46 78.91Z"/>--%>
                <%--            </svg>--%>
                <svg id="amadeus-logo" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 201 201" width="201" height="201">
                    <path id="Shape 11" class="shp0" d="M182.74 147.94C177.07 147.94 172.48 143.35 172.48 137.68C172.48 132.01 177.07 127.42 182.74 127.42C188.41 127.42 193 132.01 193 137.68C193 143.35 188.41 147.94 182.74 147.94Z" />
                    <path id="Shape 8 copy" class="shp0" d="M14 147.92L96.46 148.07L52.83 77.6L14.02 147.64L14 147.92Z" />
                    <path id="Shape 8" class="shp0" d="M119.21 147.82L146.82 147.82L77.79 34.86L76.54 34.86L64.36 59.61L119.21 147.82Z" />
                </svg>
            </a>
        </div>
        <br class="clear"/>
    </div>

    <div id="content-container">
        <div id="header">

            <br class="clear"/>

            <%if(Parakeet.isAuthenticated()){%>

                Hello <strong>${sessionScope.account.nameUsername}</strong>!
                &nbsp;|&nbsp;
                <a href="${pageContext.request.contextPath}/signout" class="href-dotted">Signout</a>
                <br/>
                <br/>
                <a href="${pageContext.request.contextPath}/account/edit/${sessionScope.account.id}" class="href-dotted">Profile</a>&nbsp;|&nbsp;
            <a href="${pageContext.request.contextPath}/sheet/list/${sessionScope.account.id}" class="href-dotted">Folios</a>&nbsp;|&nbsp;
            <a href="${pageContext.request.contextPath}/flyer/list/${sessionScope.account.id}" class="href-dotted">Advertise</a>

            <%}else{%>
                <a href="${pageContext.request.contextPath}/?uri=${uri}" class="href-dotted">Signin</a>
                &nbsp;
                <a href="${pageContext.request.contextPath}/signup?uri=${uri}" class="href-dotted">Signup!</a>
            <%}%>

        </div>

        <div id="auth-container">
            <decorator:body />
        </div>

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