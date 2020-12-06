<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<h1>Ad #${flyer.id}</h1>

<div id="form-container">

    <form action="/o/flyer/update" modelAttribute="flyer" method="post" enctype="multipart/form-data">

        <input type="hidden" name="id" value="${flyer.id}"/>

        <div class="form-row">
            <img src="/o/${flyer.imageUri}" style="width:300px;"/>
            <label>Pick Image</label>
            <input type="file" name="flyerImage"/>
            <p class="information">This will be the main entry point to your web page</p>
        </div>

        <div class="form-row">
            <label>Page Url</label>
            <input type="text" name="pageUri" placeholder="www.microsoft.com" value="www.c.com"/>
        </div>

        <div class="form-row">
            <label>Description</label>
            <textarea name="description" placeholder="Describe your web page in detail, leave an email and a phone number as well to help potential customers reach you">Some description</textarea>
        </div>

        <br class="clear"/>

        <input type="submit" value="Update Ad" class="button retro left-float"/>&nbsp;

        <c:if test="${!flyer.active}">
            <a href="/o/flyer/staging/${flyer.id}" class="button yella right-float">Pay Go Live</a>
        </c:if>

        <br class="clear"/>

    </form>

</div>



