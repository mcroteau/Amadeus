<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<h1>Handout #${sheet.id}</h1>

<c:if test="${not empty message}">
    <p>${message}</p>
</c:if>

<div id="form-container">

    <form action="/o/sheet/update" modelAttribute="sheet" method="post" enctype="multipart/form-data">

        <input type="hidden" name="id" value="${sheet.id}"/>
        <input type="hidden" name="imageUri" value="${sheet.imageUri}"/>

        <div class="form-row">
            <c:if test="${not empty sheet.imageUri}">
                <img src="${sheet.imageUri}" style="width:300px;border-radius: 40px;"/>
            </c:if>
            <label>Pick Header Image</label>
            <input type="file" name="sheetImage"/>
            <p class="information">Perfect size 670px x 335px</p>

            <c:if test="${empty sheet.imageUri}">
                <span class="tiny yella">No image selected.</span>
            </c:if>
        </div>

        <div class="form-row">
            <label>Title</label>
            <input type="text" name="title" placeholder="My Business, Place or Event" value="${sheet.title}"/>
        </div>

        <div class="form-row">
            <label>Description</label>
            <textarea name="description" placeholder="Describe what it is you are promoting. Accepts Html" style="width:100%;height:230px;">${sheet.description}</textarea>
        </div>

        <div class="form-row">
            <label>Endpoint</label>
            <input type="text" name="endpoint" placeholder="mybusiness" value="${sheet.endpoint}" style="width:100%;"/>
            <p class="information">*Optional : no special characters, all lowercase</p>
        </div>

        <br class="clear"/>

        <input type="submit" value="Update Handout" class="button retro"/>&nbsp;

    </form>

    <div id="delete-form" style="margin:100px 0px 300px;">
        <form action="/o/sheet/delete/${sheet.id}" method="post">
            <input type="submit" class="button beauty-light" value="Delete Handout"/>
        </form>
    </div>
</div>



