<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<h1>Ad #${sheet.id}</h1>

<c:if test="${not empty message}">
    <p>${message}</p>
</c:if>

<div id="form-container">

    <form action="/o/sheet/update" modelAttribute="sheet" method="post" enctype="multipart/form-data">

        <input type="hidden" name="id" value="${sheet.id}"/>
        <input type="hidden" name="imageUri" value="${sheet.imageUri}"/>
        <input type="hidden" name="active" value="${sheet.active}"/>


        <c:if test="${sheet.active}">
            <span class="yella">Running</span>
        </c:if>

        <div class="form-row">
            <c:if test="${not empty sheet.imageUri}">
                <img src="${sheet.imageUri}" style="width:300px;border-radius: 40px;"/>
            </c:if>
            <label>Pick Image</label>
            <input type="file" name="sheetImage"/>
            <p class="information">This will be the main entry point to your web page</p>

            <c:if test="${empty sheet.imageUri}">
                <span class="tiny yella">No image selected.</span>
            </c:if>
        </div>

        <div class="form-row">
            <label>Page Url</label>
            <input type="text" name="pageUri" placeholder="www.microsoft.com" value="${sheet.pageUri}"/>
            <p class="information">No http:// or https:// just start it with www or subdomain</p>
        </div>

<%--        <div class="form-row">--%>
<%--            <label>Description</label>--%>
<%--            <textarea name="description" placeholder="Describe your web page in detail, leave an email and a phone number as well to help potential customers reach you">${sheet.description}</textarea>--%>
<%--        </div>--%>

        <br class="clear"/>

        <input type="submit" value="Update Ad" class="button retro left-float"/>&nbsp;

        <c:if test="${!sheet.active}">
            <a href="/o/sheet/staging/${sheet.id}" class="button yella right-float">Pay Go Live</a>
        </c:if>

        <br class="clear"/>

    </form>

</div>



