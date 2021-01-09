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

var likesUri = "https://www.astrophysical.love/o/action/likes?uri=" + encodeURIComponent(uri);
var sharesUri = "https://www.astrophysical.love/o/action/shares?uri=" + encodeURIComponent(uri);

var likesUriDev = "/o/action/likes?uri=" + encodeURIComponent(uri);
var sharesUriDev = "/o/action/shares?uri=" + encodeURIComponent(uri);

req.http(likesUri).then(updateLikes).catch(error)
req.http(sharesUri).then(updateShares).catch(error)

var launcher = document.getElementById("launch-amadeus")

launcher.addEventListener("click", function(){

    var uri = actionsWrapper.getAttribute("data-uri")

    var src = "https://www.astrophysical.love/o/action?uri=" + encodeURIComponent(uri)
    var srcDev = "http://localhost:8080/o/action?uri=" + encodeURIComponent(uri)

    var mainHeight = 391;
    var mainWidth = 837;
    var height = 370;
    var width = 805;
    var top = (screen.height - height) / 4;
    var left = (screen.width - width) / 2;

    popup = window.open("", "AmadeusResourceAction", 'top=100 left=' + left + ' width='  + mainWidth + ' height=' + mainHeight)
    popup.document.write('<iframe width="100%" height="100%"  allowTransparency="true" frameborder="0" scrolling="yes" style="display:block;" src="'+ src +'" type="text/javascript"></iframe>');

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

    var css = '#likes-span, #shares-span, #amadeus-likes, #amadeus-shares { font-family: Arial !important }' +
        '#launch-amadeus:hover{ color: #617078 !important }' +
        '#amadeus-actions-wrapper{ text-align:left; display:inline-block; line-height:1.0em; width:140px; height:57px; }' +
        '#amadeus-inner-wrapper{ position:relative;width:140px; height:57px; }' +
        '#launch-amadeus{ color:#2cafed;font-size:39px;font-weight:bold;text-decoration:none; }' +
        '#amadeus-likes{ position:absolute; left:49px; top:0px; font-size:12px; font-weight:normal }' +
        '#amadeus-shares{ position:absolute; left:49px; top:20px; font-size:12px; font-weight:normal  }' +
        '#likes-span { font-size:12px; font-weight:bold;}' +
        '#shares-span { font-size:12px; font-weight:bold }';

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






