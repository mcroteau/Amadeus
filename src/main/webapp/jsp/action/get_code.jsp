<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div style="padding:20px;">

    <style>
        #copy-message{
            display:none;
        }
        #code-wrapper{
            font-size:19px !important;
            font-family: Monaco !important;
            height:160px;
            width:100%;
            background:#f9f9f9;
            border:solid 1px #eff2f3;
        }
    </style>

    <a href="/o" class="href-dotted">Home</a>
    <br class="clear"/>

    <h1 style="float:left">Get Code</h1>

    <br class="clear"/>

    <div style="float:left;margin-bottom:20px;">
        <span>Enter Web Address</span><br/>
        <input type="text" name="web" id="web-address" value="" placeholder="http://sap.com" style="width:500px">
        <a href="javascript:" class="button retro" style="margin-bottom:5px;display:block;text-align:center;" id="generate-code">Generate Code</a>
    </div>
    <br class="clear"/>

    <p class="yella" id="copy-message">Copy &amp; paste href to your website</p>

    <textarea id="code-wrapper">
<a href="https://amadeus.social/o/action?uri={{uri}}" id="launch-amadeus" target="_blank">
    <img src="https://amadeus.social/o/images/icon.png" style="width:45px"/>
</a>
    </textarea>

    <div style="text-align: right">
        <button class="button retro tiny" onclick="copy()">Copy Code</button>
    </div>

    <div id="example-wrapper" style="display:none">
        <span class="information">Generates</span>

        <a href="https://amadeus.social/o/action?uri={{uri}}" id="example-ref" target="_blank">
            <img src="https://amadeus.social/o/images/icon.png" style="width:45px"/>
        </a>
    </div>

</div>

<script>
    var example = document.querySelector("#example-ref")
    var exampleWrapper = document.querySelector("#example-wrapper")
    var webAddress = document.getElementById("web-address")
    var code = document.getElementById("code-wrapper")
    var generateButton = document.getElementById("generate-code")
    var copyMessage = document.getElementById("copy-message")

    generateButton.addEventListener("click", checkGenerateCode);

    function checkGenerateCode(){
        if(validUri(webAddress.value)){
            var value = webAddress.value
            var output = code.value.replace("{{uri}}", value)
            code.innerHTML = output
            copyMessage.style.display = "block"

            var href = example.getAttribute("href").replace("{{uri}}", value)
            example.setAttribute("href", href)
            exampleWrapper.style.display = "block"
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