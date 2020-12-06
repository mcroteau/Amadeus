<%@ taglib uri="http://www.opensymphony.com/sitemesh/decorator" prefix="decorator" %>

<html>
<head>
    <title>Amadeus : Like. Share. Rock!</title>

    <meta name="viewport" content="width=device-width, initial-scale=1">

    <link rel="icon" type="image/png" href="/o/images/favicon.png?v=<%=System.currentTimeMillis()%>">
	<link rel="stylesheet" href="/o/css/app.css?v=<%=System.currentTimeMillis()%>">
</head>
<body>

    <div id="top-outer-container">
        <a href="/o/" id="logo-logo">
            <svg id="amadeus-logo" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 171 171" width="171" height="171">
                <path d="M73 108L38 108L92 21L107 21L91 67L129 67L74 154L58 154L73 108Z"/>
            </svg>
        </a>
    </div>

    <style>
        #content-header{
            text-align: center;
        }
        #content-container a{
            font-family: Roboto-Bold !important;
        }
        h1{
            font-size:59px;
        }
    </style>


    <div id="content-container">
        <div id="content-header">

            <p>Hello <strong>${sessionScope.account.nameUsername}</strong>!</p>
            <a href="/o/flyer/list/${sessionScope.account.id}" class="href-dotted">Advertisements</a>&nbsp;|&nbsp;
            <a href="/o/account/edit/${sessionScope.account.id}" class="href-dotted">Edit Profile</a>

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