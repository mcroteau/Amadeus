<%@ taglib uri="http://www.opensymphony.com/sitemesh/decorator" prefix="decorator" %>
<html>
<head>
    <title>Amadeus : astrophysical*</title>

    <meta name="viewport" content="width=device-width, initial-scale=1">

    <link rel="icon" type="image/png" href="${pageContext.request.contextPath}/images/icon.png?v=<%=System.currentTimeMillis()%>">
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
            margin:0px auto 30px auto;
            padding:0px;
        }

        #amadeus-words{
            margin-bottom:30px;
        }

        #amadeus-home-logo{
            text-decoration: none;
            padding:26px 19px 14px 26px;
            display:inline-block;
            border-radius: 4px;
            -moz-border-radius: 4px;
            -webkit-border-radius: 4px;
            background: rgb(255,69,174);
            background: linear-gradient(180deg, rgba(255,69,174,1) 0%, rgba(249,255,34,1) 14%, rgba(45,245,0,1) 37%, rgba(84,175,255,1) 59%, rgba(113,11,170,1) 100%);

            background: rgb(253,254,3);
            background: linear-gradient(180deg, rgba(253,254,3,1) 0%, rgba(253,254,3,1) 7%, rgba(44,244,0,1) 7%, rgba(44,244,0,1) 25%, rgba(84,175,255,1) 25%, rgba(84,175,255,1) 62%, rgba(113,11,170,1) 62%, rgba(113,11,170,1) 91%, rgba(255,75,176,1) 92%);

            background: rgb(255,75,176);
            background: linear-gradient(90deg, rgba(255,75,176,1) 0%, rgba(253,254,3,1) 0%, rgba(253,254,3,1) 5%, rgba(44,244,0,1) 5%, rgba(44,244,0,1) 10%, rgba(84,175,255,1) 10%, rgba(84,175,255,1) 16%, rgba(10,1,15,1) 16%);


            -webkit-box-shadow: 0px 3px 49px 0px rgba(0,0,0,.29);
            -moz-box-shadow: 0px 3px 49px 0px rgba(0,0,0,.29);
            box-shadow: 0px 3px 49px 0px rgba(0,0,0,.29);
        }

        #amadeus-icon{
            fill:#fff;
            height:45px;
            width:45px;
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
    <decorator:body/>

    <p class="information" style="margin:30px auto 170px 140px">&copy 2021 Amadeus</p>

</body>
</html>
