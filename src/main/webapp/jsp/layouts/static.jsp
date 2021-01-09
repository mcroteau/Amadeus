<%@ taglib uri="http://www.opensymphony.com/sitemesh/decorator" prefix="decorator" %>

<html>
<head>
    <title>Amadeus : Social Networking</title>

    <meta name="viewport" content="width=device-width, initial-scale=1">

    <link rel="icon" type="image/png" href="/o/images/icon.png?v=<%=System.currentTimeMillis()%>">
	<link rel="stylesheet" href="/o/css/app.gap.css?v=<%=System.currentTimeMillis()%>">
    <link rel="stylesheet" href="/o/css/app.astro.css?v=<%=System.currentTimeMillis()%>">

    <script type="text/javascript" src="/o/js/Req.js"></script>

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
            <a href="https://astrophysical.love" id="logo-logo">
                <svg id="amadeus-logo" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 70 70" width="70" height="70">
                    <g id="amadeus">
                        <path id="Shape 13" class="shp0" d="M46 31L46 36L57.01 36L57.06 40.91L62 41L62 31L46 31Z" />
                        <path id="Shape 10" fill-rule="evenodd" class="shp1" d="M16 20L21 20L32 48L4 48L16 20ZM11 44L25 44L19 26L18 26L11 44Z" />
                    </g>
                    <path id="Shape 13" fill-rule="evenodd" class="shp0" d="M38.5 23C36.01 23 34 20.99 34 18.5C34 16.01 36.01 14 38.5 14C40.99 14 43 16.01 43 18.5C43 20.99 40.99 23 38.5 23ZM40.6 18.5C40.6 17.34 39.66 16.4 38.5 16.4C37.34 16.4 36.4 17.34 36.4 18.5C36.4 19.66 37.34 20.6 38.5 20.6C39.66 20.6 40.6 19.66 40.6 18.5Z" />
                </svg>
    <%--            <span class="medium" id="amadeus-symbol">&Delta;</span>--%>
    <%--            <svg id="amadeus-logo" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 130 130">--%>
    <%--                <path id="amadeus-icon-path" d="M57.46 78.91L36.35 78.87L54.53 30.08L75.83 30.16L68.2 52.86L92 52.85L57.55 115L56.95 115L57.46 78.91ZM57.46 78.91L36.31 78.99L54.62 30.16L75.81 30.07L68.2 52.86L92 52.86L57.55 115L56.95 115L57.46 78.91Z"/>--%>
    <%--            </svg>--%>
<%--                <svg id="amadeus-logo" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 201 201" width="201" height="201">--%>
<%--                    <path id="Shape 11" class="shp0" d="M182.74 147.94C177.07 147.94 172.48 143.35 172.48 137.68C172.48 132.01 177.07 127.42 182.74 127.42C188.41 127.42 193 132.01 193 137.68C193 143.35 188.41 147.94 182.74 147.94Z" />--%>
<%--                    <path id="Shape 8 copy" class="shp0" d="M14 147.92L96.46 148.07L52.83 77.6L14.02 147.64L14 147.92Z" />--%>
<%--                    <path id="Shape 8" class="shp0" d="M119.21 147.82L146.82 147.82L77.79 34.86L76.54 34.86L64.36 59.61L119.21 147.82Z" />--%>
<%--                </svg>--%>
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