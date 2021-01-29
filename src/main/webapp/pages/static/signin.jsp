<html>
<head>
    <title>Amadeus : Signin</title>
</head>
<body>

<div id="signup-form-container">

    <a href="/o/home" id="amadeus-home-logo">
        <svg id="amadeus-icon" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 70 70" width="70" height="70">
            <g id="amadeus">
                <path id="Shape 13" class="shp0" d="M46 31L46 36L57.01 36L57.06 40.91L62 41L62 31L46 31Z" />
                <path id="Shape 10" fill-rule="evenodd" class="shp1" d="M16 20L21 20L32 48L4 48L16 20ZM11 44L25 44L19 26L18 26L11 44Z" />
            </g>
            <path id="Shape 13" fill-rule="evenodd" class="shp0" d="M38.5 23C36.01 23 34 20.99 34 18.5C34 16.01 36.01 14 38.5 14C40.99 14 43 16.01 43 18.5C43 20.99 40.99 23 38.5 23ZM40.6 18.5C40.6 17.34 39.66 16.4 38.5 16.4C37.34 16.4 36.4 17.34 36.4 18.5C36.4 19.66 37.34 20.6 38.5 20.6C39.66 20.6 40.6 19.66 40.6 18.5Z" />
        </svg>
        <%--        <svg id="amadeus-logo" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 201 201" width="201" height="201">--%>
        <%--            <path id="Shape 11" class="shp0" d="M182.74 147.94C177.07 147.94 172.48 143.35 172.48 137.68C172.48 132.01 177.07 127.42 182.74 127.42C188.41 127.42 193 132.01 193 137.68C193 143.35 188.41 147.94 182.74 147.94Z" />--%>
        <%--            <path id="Shape 8 copy" class="shp0" d="M14 147.92L96.46 148.07L52.83 77.6L14.02 147.64L14 147.92Z" />--%>
        <%--            <path id="Shape 8" class="shp0" d="M119.21 147.82L146.82 147.82L77.79 34.86L76.54 34.86L64.36 59.61L119.21 147.82Z" />--%>
        <%--        </svg>--%>
        <%--        <svg id="amadeus-icon" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 130 130">--%>
        <%--            <path id="amadeus-icon-path" d="M57.46 78.91L36.35 78.87L54.53 30.08L75.83 30.16L68.2 52.86L92 52.85L57.55 115L56.95 115L57.46 78.91ZM57.46 78.91L36.31 78.99L54.62 30.16L75.81 30.07L68.2 52.86L92 52.86L57.55 115L56.95 115L57.46 78.91Z"/>--%>
        <%--        </svg>--%>
        <%--        <span class="medium" id="amadeus-symbol" style="display:block">&Delta;</span>--%>
    </a>

    <h1 style="font-size:32px;font-family: Roboto-Bold !important;margin-top:20px;">Amadeus</h1>

    <p id="amadeus-words">Social Networking</p>

    <h2 style="margin-bottom:20px;">Signin</h2>

    <form action="${pageContext.request.contextPath}/authenticate" modelAttribute="signon" method="post" >

        <input type="hidden" name="uri" value="${uri}"/>

        <div class="form-group">
            <label for="username">Username</label>
            <input type="text" name="username" class="form-control" id="username" placeholder=""  value=""  style="width:100%;">
        </div>

        <div class="form-group">
            <label for="password">Password</label>
            <input type="password" name="password" class="form-control" id="password" style="width:100%;" value=""  placeholder="&#9679;&#9679;&#9679;&#9679;&#9679;&#9679;">
        </div>

        <div style="text-align:right; margin-top:30px;">
            <input type="hidden" name="targetUri" value="${targetUri}" />
            <input type="submit" class="button retro" value="Signin" style="width:100%;">
        </div>

        <br/>

        <br/>
        <a href="${pageContext.request.contextPath}/account/reset" class="href-dotted">Forgot Password</a>&nbsp;&nbsp;

    </form>

    <div id="signup-container" style="text-align: center;margin-top:51px">
        <a href="${pageContext.request.contextPath}/signup" class="button modern large">Sign Up !</a>
    </div>
</div>

</body>
</html>
