
<c:if test="${not empty message}">
    <p>${message}</p>
</c:if>

<h1>New Handout</h1>

<div id="form-container">

    <form action="${pageContext.request.contextPath}/sheet/save" id="sheet-form" modelAttribute="sheet" method="post" enctype="multipart/form-data">

        <div class="form-row">
            <label>Pick Image</label>
            <input type="file" name="sheetImage"/>
        </div>

        <div class="form-row">
            <label>Title</label>
            <input type="text" name="pageUri" placeholder="My Business Name" value="" style="width:100%;"/>
        </div>

        <div class="form-row">
            <label>Description</label>
            <textarea name="description" placeholder="Describe what it is you are promoting. Accepts Html" style="width:100%;height:230px;"></textarea>
        </div>

        <div class="form-row">
            <label>Endpoint</label>
            <input type="text" name="endpoint" placeholder="mybusiness" value="" style="width:100%;"/>
            <p class="information">*Optional : no special characters, all lowercase</p>
        </div>

    </form>

</div>

<div id="form-action-container">
    <input type="submit" id="save-button" value="Create Handout" class="button retro"/>
</div>

<script>

    var processing = false

    var form = document.querySelector("#sheet-form")
    var saveButton = document.querySelector("#save-button")

    saveButton.addEventListener("click", function(evt){
        evt.preventDefault();
        if(!processing){
            processing = true;
            form.submit();
        }
    })

</script>

