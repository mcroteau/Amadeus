<%@ taglib uri="http://shiro.apache.org/tags" prefix="shiro"%>

<div id="uno-content">

    <c:if test="${not empty sessionScope.message}">
        <div class="span12">
            <p>${sessionScope.message}</p>
        </div>
    </c:if>


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

    <br class="clear"/>

    <a href="${pageContext.request.contextPath}/signup" class="button yella large">Sign Up !</a>

<%--    <a href="${pageContext.request.contextPath}/account/guest" class="button light large" style="display:block;margin-top:10px;margin-bottom:70px;">Check it Out !</a>--%>
</div>
