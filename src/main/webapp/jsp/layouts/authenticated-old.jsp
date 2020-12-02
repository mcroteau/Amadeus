<!doctype html>
<html>
<head>
    <title>Amadeus : Like. Share. Rock Me Amadeus!</title>

    <meta name="viewport" content="width=device-width, initial-scale=1">

    <link rel="icon" type="image/png" href="${pageContext.request.contextPath}/images/favicon.png?v=<%=System.currentTimeMillis()%>">

    <script type="text/javascript" src="${pageContext.request.contextPath}/js/packages/angular.min.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/js/packages/angular-route.js"></script>

    <script type="text/javascript" src="${pageContext.request.contextPath}/js/app.js"></script>


    <%--	<script type="text/javascript" src="${pageContext.request.contextPath}/js/lib/apexcharts/apexcharts.js"></script>--%>
    <%--	<link rel="stylesheet" href="${pageContext.request.contextPath}/js/lib/apexcharts/apexcharts.css"/>--%>

    <%--	<script type="text/javascript" src="${pageContext.request.contextPath}/js/lib/anchorme.min.js"></script>--%>

    <%--	<script type="text/javascript" src="${pageContext.request.contextPath}/js/Request.js"></script>--%>
    <%--	<script type="text/javascript" src="${pageContext.request.contextPath}/js/WebForm.js"></script>--%>

    <%--    <script type="text/javascript" src="${pageContext.request.contextPath}/js/lib/progressbar.js"></script>--%>

    <%--	<script type="text/javascript" src="${pageContext.request.contextPath}/js/lib/js.cookies.js"></script>--%>
    <%--	<script type="text/javascript" src="${pageContext.request.contextPath}/js/lib/mustache.js"></script>--%>

    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/app.css?v=<%=System.currentTimeMillis()%>"/>

</head>
<body ng-app="myApp">

<%--	<div id="layout-container">--%>

<%--		<div id="top-outer-container">--%>

<%--            <div id="logo-container" style="position:absolute;">--%>
<%--                <a href="javascript:" id="logo-logo">--%>
<%--                    <svg id="amadeus-logo">--%>
<%--                        <use xlink:href="#amadeus" />--%>
<%--                    </svg>--%>
<%--                    <span id="latest-feed-total" class="notifications-count" style="display:inline-block; position:absolute;bottom:3px;left:54px;"></span></a>--%>
<%--            </div>--%>

<%--			<div id="top-inner-container">--%>

<%--                <div id="search-label">Search:</div>--%>

<%--                <div id="search-container" class="float-left" style="z-index:100">--%>
<%--                    <input type="text" class="search-input" id="search-box" placeholder=""/>--%>
<%--				</div>--%>

<%--				<br class="clear"/>--%>

<%--                <div id="page-processing">--%>
<%--	                <img src="${pageContext.request.contextPath}/images/processing-dos.gif" style="height:50px; width:50px; position:absolute; right:240px; top:3px;"/>--%>
<%--                    <span class="information" id="processing-message"></span>--%>
<%--                </div>--%>

<%--			</div>--%>

<%--            <div id="navigation-container" class="float-right">--%>
<%--                <a href="javascript:" id="profile-actions-href" style="margin-right:37px;">--%>
<%--                    <img src="${pageContext.request.contextPath}/${sessionScope.imageUri}" id="profile-ref-image" style="z-index:1"/>--%>
<%--                    <span id="base-notifications-count">0</span>--%>
<%--                </a>--%>

<%--                <div id="profile-picture-actions-container" style="display:none;text-align:left;" data-id="${sessionScope.account.id}" class="global-shadow">--%>
<%--                    <a href="javascript:" id="profile-href"  class="profile-popup-action" data-id="${sessionScope.account.id}"><span class="space"></span> Profile</a>--%>
<%--                    <a href="javascript:" id="messages-href"  class="profile-popup-action" data-id="${sessionScope.account.id}"><span id="latest-messages-total" class="space">0</span> Unread</a>--%>
<%--                    <a href="${pageContext.request.contextPath}/signout" class="profile-popup-action" ><span class="space"></span> Logout</a>--%>
<%--                </div>--%>
<%--            </div>--%>

<%--		</div>--%>

<div class="ng-view"></div>

<%--	</div>--%>

<%--    <div id="site-refs" style="text-align:center;margin-top:191px;">--%>
<%--        <a href="${pageContext.request.contextPath}/get_code" class="page-ref href-dotted" >Get Code</a>--%>
<%--        <a href="javascript:" class="page-ref href-dotted" data-ref="about">About</a>--%>
<%--        <a href="${pageContext.request.contextPath}/invite" class="href-dotted" id="invite-people">Invite</a>--%>
<%--    </div>--%>

<%--    <p style="text-align:center;"><a href="mailto:support@amadeus.social" style="color:#17161b" class="href-dotted">support@amadeus.social</a>--%>
<%--        <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 134 134" id="amadeus">--%>
<%--            <path d="M49 1L21 88L57 88L42 134L84 134L113 47L92 47L79 47L75 47L91 1L49 1Z" />--%>
<%--        </svg>--%>
<%--    </p>--%>

<%--    <style>--%>
<%--        #amadeus{--%>
<%--            height:20px;--%>
<%--            width:20px;--%>
<%--        }--%>
<%--    </style>--%>

<%--    <div style="text-align:center;margin:20px auto 560px auto">--%>
<%--        <p style="text-align: center; font-size:12px;">&copy; 2020 Amadeus</p>--%>
<%--        <a href="http://tomcat.apache.org/" target="_blank" class="information">Powered by<br/>Tomcat</a>--%>
<%--    </div>--%>

<p><a href="#!/">Main</a></p>

<a href="#!red">Red</a>
<a href="#!green">Green</a>
<a href="#!blue">Blue</a>

<script>
    var app = angular.module("myApp", ["ngRoute"]);
    app.config(function($routeProvider) {
        $routeProvider
            .when("/", {
                templateUrl : "pages/activity.html"
            })
            .when("/red", {
                templateUrl : "pages/get.html"
            })
            .when("/green", {
                templateUrl : "pages/search.html"
            })
            .when("/blue", {
                templateUrl : "pages/activity.html"
            });
    });
</script>


</body>
</html>

