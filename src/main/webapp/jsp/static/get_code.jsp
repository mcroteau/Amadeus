<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div style="padding:20px;">

    <style>
        textarea{
            border:solid 1px #c7c7c7;
        }
        #copy-message{
            display:none;
        }
        #code-wrapper{
            font-size:10px !important;
            font-family: Monaco !important;
            height:160px;
            width:100%;
            background:#f9f9f9;
        }
    </style>


    <c:set var="dev" value="true" scope="page" />
    <c:if test="${dev}">
        <c:set var="launcherUrl" value="/o/images/icon.png" scope="page" />
        <c:set var="jsUrl" value="/o/js/Actions.js" scope="page" />
    </c:if>
    <c:if test="${!dev}">
        <c:set var="launcherUrl" value="https://www.amadeus.social/o/images/icon.png" scope="page" />
        <c:set var="jsUrl" value="https://www.amadeus.social/o/js/Actions.js" scope="page" />
    </c:if>

    <a href="/o" class="href-dotted">Home</a>
    <br class="clear"/>

    <h1 style="float:left">Get Code</h1>

    <div style="float:right">
        <span class="information">Generates</span>

        <div id="amadeus-actions-wrapper" data-uri="NOT READY, GO BACK ENTER URL!">
            <div id="amadeus-inner-wrapper">
                <a href="javascript:" id="launch-amadeus"><img src="${launcherUrl}" style="width:45px"/></a>
                <span id="amadeus-likes"><span id="likes-span"></span> likes</span>
                <span id="amadeus-shares"><span id="shares-span"></span> shares</span>
            </div>
        </div>
    </div>
    <script type="text/javascript" src="${jsUrl}"></script>
    <br class="clear"/>

    <div style="float:left;margin-bottom:20px;">
        <span>Enter Web Address</span><br/>
        <input type="text" name="web" id="web-address" value="" placeholder="http://sap.com" style="width:500px">
        <a href="javascript:" class="button retro" style="margin-bottom:5px;display:block;text-align:center;" id="generate-code">Generate Code</a>
    </div>
    <br class="clear"/>

    <p class="yella" id="copy-message">Copy &amp; paste code below to your website</p>

    <textarea id="code-wrapper">
<div id="amadeus-actions-wrapper" data-uri="{{uri}}">
    <div id="amadeus-inner-wrapper">
        <a href="javascript:" id="launch-amadeus">
            <img src="https://amadeus.social/o/images/icon.png" style="width:45px"/></a>
        <span id="amadeus-likes"><span id="likes-span"></span> likes</span>
        <span id="amadeus-shares"><span id="shares-span"></span> shares</span>
    </div>
</div>
<script type="text/javascript" src="https://amadeus.social/o/js/Actions.js"></script>
    </textarea>

    <div style="text-align: right">
        <button class="button retro tiny" onclick="copy()">Copy Code</button>
    </div>

</div>

<script>
    var webAddress = document.getElementById("web-address")
    var code = document.getElementById("code-wrapper")
    var amadeusActions = document.getElementById("amadeus-actions-wrapper")
    var generateButton = document.getElementById("generate-code")
    var copyMessage = document.getElementById("copy-message")
    var amadeusActions = document.getElementById("amadeus-actions-wrapper")

    generateButton.addEventListener("click", checkGenerateCode);

    function checkGenerateCode(){
        if(validUri(webAddress.value)){
            var value = webAddress.value
            var output = code.value.replace("{{uri}}", value)
            code.value = output
            copyMessage.style.display = "block"
            amadeusActions.setAttribute("data-uri", value)
        }else{
            alert("enter valid web address")
        }
    }

    function copy(event){
        code.select();
        code.setSelectionRange(0, 99999); /* For mobile devices */

        document.execCommand("copy");
        alert('Successfully copied code!')
    }

    function validUri(str) {
      var pattern = new RegExp('^(https?:\\/\\/)?'+ // protocol
        '((([a-z\\d]([a-z\\d-]*[a-z\\d])*)\\.)+[a-z]{2,}|'+ // domain name
        '((\\d{1,3}\\.){3}\\d{1,3}))'+ // OR ip (v4) address
        '(\\:\\d+)?(\\/[-a-z\\d%_.~+]*)*'+ // port and path
        '(\\?[;&a-z\\d%_.~+=-]*)?'+ // query string
        '(\\#[-a-z\\d_]*)?$','i'); // fragment locator
      return !!pattern.test(str);
    }

</script>