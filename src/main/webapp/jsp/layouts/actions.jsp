<%@ taglib uri="http://www.opensymphony.com/sitemesh/decorator" prefix="decorator" %>
<html>
<head>
    <title>Amadeus : Rock Me Amadeus!</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/app.css?v=<%=System.currentTimeMillis()%>">
    <style>
        iframe,
        html,
        body{
            margin:0px !important;
            padding:0px !important;
            display: block !important;
            height: 100%;
            width:100%;
        }
    </style>

    <script type="text/javascript" src="/o/js/Req.js"></script>

</head>
<body>
    <decorator:body/>
</body>
</html>
