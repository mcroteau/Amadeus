var ResourceRequest = function(){

    var http = function(uri, method){

        var request = new XMLHttpRequest();
        request.onprogress = updateRequestProgress;

        return new Promise(function(resolve, reject){
            request.onreadystatechange = function () {

                if (request.readyState !== 4) return;
                if (request.status >= 200 && request.status < 300) {
                    resolve(request);
                } else {
                    reject({
                        status: request.status,
                        statusText: request.statusText
                    });
                }
            };
            request.open(method ? method : 'get', uri, true);
            request.send();
        });
    }

    var updateRequestProgress = function(arg){
    }

    return {
        http: http
    };
}

var popup = {}

var actionsWrapper = document.getElementById("amadeus-actions-wrapper")
var likesSpan = document.getElementById("likes-span")
var sharesSpan = document.getElementById("shares-span")

addActionStyles()

var req = new ResourceRequest()
var uri = actionsWrapper.getAttribute("data-uri")

var likesUriProd = "https://www.amadeus.social/o/resource/likes?uri=" + encodeURIComponent(uri);
var sharesUriProd = "https://www.amadeus.social/o/resource/shares?uri=" + encodeURIComponent(uri);

var likesUri = "/o/resource/likes?uri=" + encodeURIComponent(uri);
var sharesUri = "/o/resource/shares?uri=" + encodeURIComponent(uri);

req.http(likesUri).then(updateLikes).catch(error)
req.http(sharesUri).then(updateShares).catch(error)

var launcher = document.getElementById("launch-amadeus")

launcher.addEventListener("click", function(){

    var uri = actionsWrapper.getAttribute("data-uri")

    var srcPrd = "https://www.amadeus.social/o/resource?uri=" + encodeURIComponent(uri)
    var src = "http://localhost:8080/o/resource?uri=" + encodeURIComponent(uri)

    var height = 175;
    var width = 637
    var top = (screen.height - height) / 4;
    var left = (screen.width - width) / 2;

    popup = window.open("", "AmadeusResourceAction", 'top=100 left=' + left + ' width='  + width + ' height=' + height)
    popup.document.write('<iframe width="' + width + '" height="' + height + '"  allowTransparency="true" frameborder="0" scrolling="yes" style="width:100%;" src="'+ src +'" type="text/javascript"></iframe>');

});

function updateLikes(request){
    var data = JSON.parse(request.responseText)
    likesSpan.innerHTML = data.likes
}

function updateShares(request){
    var data = JSON.parse(request.responseText)
    sharesSpan.innerHTML = data.shares
}

function addActionStyles(){

    var css =  '#launch-amadeus:hover{ color: #617078 !important }' +
        '#amadeus-actions-wrapper{ text-align:left; display:inline-block; line-height:1.0em; width:140px; height:57px; }' +
        '#amadeus-inner-wrapper{ position:relative;width:140px; height:57px; }' +
        '#launch-amadeus{ color:#2cafed;font-size:39px;font-weight:bold;text-decoration:none; }' +
        '#amadeus-likes{ position:absolute; left:49px; top:0px; font-size:12px; font-weight:normal }' +
        '#amadeus-shares{ position:absolute; left:49px; top:20px; font-size:12px; font-weight:normal  }' +
        '#likes-span { font-size:12px; font-weight:normal;}' +
        '#shares-span { font-size:12px; font-weight:normal }' +
        '#amadeus { font-size:11px; margin-left:8px; display:block;clear:both; font-weight:normal;}';

    var style = document.createElement('style');

    if (style.styleSheet) {
        style.styleSheet.cssText = css;
    } else {
        style.appendChild(document.createTextNode(css));
    }

    document.getElementsByTagName('head')[0].appendChild(style);
}

var timer = setInterval(function () {
    if (popup.closed) {
        clearInterval(timer);
        window.location.reload();
    }
}, 1000);

function error(){
}






