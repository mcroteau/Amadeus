<style>
    iframe,
    html,
    body{
        margin:0px !important;
        padding:0px !important;
        display: block;
    }
    /*body{*/
    /*    background: rgb(253,254,3);*/
    /*    background: linear-gradient(36deg, rgba(253,254,3,1) 9%, rgba(159,237,3,1) 20%, rgba(62,148,224,1) 50%, rgba(84,175,255,1) 100%);*/
    /*}*/
    #top-outer-container{
        height:7px;
    }
    #like-button{
        border-radius: 50px;
        background:#FDFE01;
        padding:17px 17px;
        margin-top:20px;
        margin-left:30px;
        font-family:Roboto-Medium !important;
    }
    #amadeus-like{
        height:30px;
        width:30px;
        fill:#000;
        clear:both;
    }
    #like-action-container{
        float:left;
        padding:20px;
        text-align: center;
        vertical-align: top;
    }
    #share-action-container{
        float:left;
        padding:20px;
        margin-left:52px;
        width:300px;
        vertical-align: top;
        text-align: center;
        background: rgb(249,249,249);
        background: linear-gradient(180deg, rgba(249,249,249,1) 0%, rgba(249,249,249,1) 30%, rgba(255,255,255,1) 58%, rgba(255,255,255,1) 100%);
        /*background:rgba(255,255,255,0.1);*/
    }
    #share-action-container textarea{
        height:119px !important;
        width:100% !important;
        font-size:14px !important;
        font-family: Roboto-Light !important;
        padding: 9px 47px 27px 11px;
        border-radius: 4px;
        -moz-border-radius: 4px;
        -webkit-border-radius: 4px;
        -webkit-box-shadow: 0px 2px 3px 0px rgba(0,0,0,.09);
        -moz-box-shadow: 0px 2px 3px 0px rgba(0,0,0,.09);
        box-shadow: 0px 2px 3px 0px rgba(0,0,0,.09);
        /*background:rgba(255,255,255,0.1) !important;*/
        /*border:solid 1px rgba(255,255,255, 0.1) !important;*/
        /*width: calc(100% - 40px) !important;*/
    }
    #share-action-container textarea::placeholder{
        font-size:14px;
    }
    #share-action-container .post-button{
        width: calc(100% - 40px) !important;
        width:100% !important;
    }
    #branding{
        width:13px;
        height:100%;
        float:right;
        background: rgb(253,254,3);
        background: linear-gradient(136deg, rgba(253,254,3,1) 22%, rgba(159,237,3,1) 44%, rgba(84,175,255,1) 66%, rgba(84,175,255,1) 79%, rgba(208,48,138,1) 94%);
    }
    h1{
        font-size:46px;
    }
    h3{
        font-size:21px;
    }
    p{
        font-size: 13px;
    }
</style>


<h1>Like or Share</h1>
<p>${uri}</p>

<div id="like-action-container" style="">
    <h3>Like</h3>
    <form action="${pageContext.request.contextPath}/action/like" id="like-form" method="post">
        <input type="hidden" name="uri" value="${uri}">
        <a href="javascript:" id="like-button" class="like-button global-shadow">
            <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 134 134" id="amadeus-like">
                <path d="M49 1L21 88L57 88L42 134L84 134L113 47L92 47L79 47L75 47L91 1L49 1Z" />
            </svg>
        </a>
    </form>
    <p class="tiny"><span id="likes-span"></span> likes</p>
</div>

<%--        <div class="float-left" style="margin:60px 30px 0px 30px; height:200px;">--%>
<%--            or--%>
<%--        </div>--%>

<div id="share-action-container">
    <h3>Share</h3>
    <form action="${pageContext.request.contextPath}/action/share" method="post">
        <input type="hidden" name="uri" value="${uri}">
        <textarea name="comment" id="voice" class="resource-text" placeholder="Write about ${uri}" style="width:352px;height:251px;border-radius: 4px;-moz-border-radius: 4px;-webkit-border-radius: 4px;"></textarea>
        <input type="submit" class="button retro post-button" value="Share!">
    </form>
</div>

<input type="hidden" value="${uri}" id="uri"/>

<br class="clear"/>

<script>
    var likeFrm = document.getElementById("like-form")
    var likeBtn = document.getElementById("like-button")
    likeBtn.addEventListener("click", function(event){
        event.preventDefault()
        likeFrm.submit()
    });

    var req = new Req()
    var uri = document.querySelector("#uri").value
    var likesSpan = document.querySelector("#likes-span")

    var likesUriProd = "https://www.amadeus.social/o/action/likes?uri=" + encodeURIComponent(uri);
    var sharesUriProd = "https://www.amadeus.social/o/action/shares?uri=" + encodeURIComponent(uri);

    var likesUri = "/o/action/likes?uri=" + encodeURIComponent(uri);
    var sharesUri = "/o/action/shares?uri=" + encodeURIComponent(uri);

    req.http(likesUri).then(updateLikesAction).catch(error)

    function updateLikesAction(request){
        console.log(request)
        var data = JSON.parse(request.responseText)
        likesSpan.innerHTML = data.likes
    }
</script>