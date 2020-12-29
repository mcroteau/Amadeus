<%@ taglib uri="http://www.opensymphony.com/sitemesh/decorator" prefix="decorator" %>

<html>
<head>
    <title>Amadeus : Social Networking</title>

    <meta name="viewport" content="width=device-width, initial-scale=1">

    <link rel="icon" type="image/png" href="/o/images/icon.png?v=<%=System.currentTimeMillis()%>">
	<link rel="stylesheet" href="/o/css/app.gap.css?v=<%=System.currentTimeMillis()%>">
    <link rel="stylesheet" href="/o/css/app.astro.css?v=<%=System.currentTimeMillis()%>">

    <style>
        #logo-logo,
        #logo-logo:hover{
        }
        #content-header{
            text-align: center;
        }
        #content-container a{
            font-family: Roboto-Bold !important;
        }
        h1{
            font-size:32px;
        }
        p{
            padding:0px;
            line-height:1.3em;
            margin:7px 0px 7px 0px;
        }
        label{
            font-size:12px;
            display:block;
            margin:10px 0px 0px 0px;
        }
        input[type="text"]{
            background:#fff;
            width:401px;
            padding:8px;
            font-size:17px;
        }
        input[type="text"]:hover,
        input[type="text"]:focus{
            background: #fff;
        }
        textarea{
            height: 115px;
            width: 300px;
        }
        textarea{
            color: #17161b;
            font-family: Roboto-Light !important;
            font-size: 18px !important;
            background: #fff;
            line-height: 1.4em !important;
            padding: 8px 8px !important;
            border: solid 1px #ccc !important;
            -webkit-border-radius: 3px !important;
            -moz-border-radius: 3px !important;
            border-radius: 3px !important;
            box-shadow: none;
            -moz-box-shadow: none;
            -webkit-box-shadow: none;
        }

        textarea:focus{
            border: solid 1px #ccc !important;
        }

        #form-action-container{
            margin-top:30px;
            text-align: right;
        }
        #content-container{
            width:570px;
        }
        /*#logo-logo{*/
        /*    background: #efefef !important;*/
        /*}*/
        /*#logo-logo:hover{*/
        /*    background: #efefef !important;*/
        /*}*/
    </style>

</head>
<body>

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
    </div>

    <div id="content-container">
        <decorator:body />
    </div>


    <script async src="https://www.googletagmanager.com/gtag/js?id=UA-40862316-16"></script>
    <script>
      window.dataLayer = window.dataLayer || [];
      function gtag(){dataLayer.push(arguments);}
      gtag('js', new Date());

      gtag('config', 'UA-40862316-17');
    </script>

</body>
</html>