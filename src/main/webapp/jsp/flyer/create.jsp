<style>
    label{
        display:block;
        margin:20px 0px 0px 0px;
    }
    input[type="text"]{
        background:#fff;
        width:401px;
    }
    input[type="text"]:hover,
    input[type="text"]:focus{
        background: #fff;
    }
    textarea{
        height:170px;
        width:501px;
    }
    textarea{
        color: #17161b;
        font-family: Roboto-Light !important;
        font-size: 19px !important;
        background: #fff;
        line-height: 1.4em !important;
        padding: 12px 12px !important;
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
</style>

<h1>Start Ad</h1>

<div id="form-container">

    <form action="${pageContext.request.contextPath}/flyer/save" modelAttribute="flyer" method="post" enctype="multipart/form-data">

        <div class="form-row">
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
        <input type="submit" id="save-button" value="Begin Ad" class="button retro"/>

    </form>

</div>

<div id="form-action-container">
    <input type="submit" id="save-button" value="Begin Ad" class="button retro"/>
</div>

<script>

    // var processing = false
    //
    // var form = document.querySelector("#flyer-form")
    // var saveButton = document.querySelector("#save-button")
    //
    // saveButton.addEventListener("click", function(evt){
    //     evt.preventDefault();
    //     if(!processing){
    //         processing = true;
    //         form.submit();
    //     }
    // })

</script>

