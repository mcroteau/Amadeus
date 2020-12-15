var Req = function(){

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