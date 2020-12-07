<%@ taglib uri="http://www.opensymphony.com/sitemesh/decorator" prefix="decorator" %>

<html>
<head>
    <title>Amadeus : Like. Share. Amadeus!</title>

    <meta name="viewport" content="width=device-width, initial-scale=1">

    <link rel="icon" type="image/png" href="/o/images/favicon.png?v=<%=System.currentTimeMillis()%>">
	<link rel="stylesheet" href="/o/css/app.css?v=<%=System.currentTimeMillis()%>">

    <style>
        #logo-logo,
        #logo-logo:hover{
            background: rgb(253,254,3) !important;
            background: linear-gradient(60deg, rgba(253,254,3,1) 0%, rgba(253,254,3,1) 33%, rgba(253,254,3,1) 60%, rgba(84,175,255,1) 90%, rgba(192,225,255,1) 100%) !important;/*-moz-box-shadow: 0px 4px 17px 0px rgba(0,0,0,.9);*/
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
    </style>

</head>
<body>

    <div id="top-outer-container">
        <a href="/o/" id="logo-logo">
            <svg id="amadeus-logo" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 171 171" width="171" height="171">
                <path d="M73 108L38 108L92 21L107 21L91 67L129 67L74 154L58 154L73 108Z"/>
            </svg>
        </a>
    </div>

    <div id="content-container">
        <div id="content-header">

            <p>Hello <strong>${sessionScope.account.nameUsername}</strong>!</p>
            <a href="/o/account/edit/${sessionScope.account.id}" class="href-dotted">Edit Profile</a>&nbsp;|&nbsp;
            <a href="/o/flyer/list/${sessionScope.account.id}" class="href-dotted">Advertisements</a>

        </div>
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