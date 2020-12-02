<!doctype html>
<html>
<head>
    <title>Amadeus : Like. Share. Rock Me Amadeus!</title>

    <meta name="viewport" content="width=device-width, initial-scale=1">

    <link rel="icon" type="image/png" href="/o/images/favicon.png?v=<%=System.currentTimeMillis()%>">

    <script type="text/javascript" src="/o/js/packages/angular.min.js"></script>
    <script type="text/javascript" src="/o/js/packages/angular-sanitize.js"></script>
    <script type="text/javascript" src="/o/js/packages/angular-animate.js"></script>
    <script type="text/javascript" src="/o/js/packages/angular-route.js"></script>
    <script type="text/javascript" src="/o/js/packages/anchorme.js"></script>

    <link rel="stylesheet" href="/o/css/app.css?v=<%=System.currentTimeMillis()%>"/>
    <link rel="stylesheet" href="/o/css/app.mobile.css?v=<%=System.currentTimeMillis()%>"/>

</head>

<body ng-app="app" ng-controller="baseController">

    <style>
        .linear-activity {
            overflow: hidden;
            width: 100%;
            height: 2px;
            background-color: #B3E5FC;
            margin: 0px auto;
            position:absolute;
            left:0px;
            right:0px;
            top:0px;
            z-index:1201;
        }

        .indeterminate {
            position: relative;
            width: 100%;
            height: 100%;
        }

        .indeterminate:before {
            content: '';
            position: absolute;
            height: 100%;
            background-color: #03A9F4;
            animation: indeterminate_first 1.7s infinite ease-out;
        }

        @keyframes indeterminate_first {
            0% {
                left: -100%;
                width: 100%;
            }
            100% {
                left: 100%;
                width: 10%;
            }
        }
    </style>

    <div id="linear-indicator">
        <div class="indeterminate" style="width: 100%"></div>
    </div>

	<div id="layout-container" style="position:relative;">

		<div id="top-outer-container" ng-init="init()">

            <div id="logo-container" style="position:absolute;">
                <a ng-click="reloadActivities()" href="javascript:" id="logo-logo">
                    <svg id="amadeus-logo" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 171 171" width="171" height="171">
                        <path d="M73 108L38 108L92 21L107 21L91 67L129 67L74 154L58 154L73 108Z"/>
                    </svg>
                    <span id="latest-feed-total" class="notifications-count" style="display:inline-block; position:absolute;bottom:3px;left:54px;">{{data.latestPosts.length}}</span></a>
            </div>

			<div id="top-inner-container">
                <div id="search-label">Search:</div>

                <div id="search-container" class="float-left" style="z-index:100">
                    <input ng-keyup="navigateSearch($event)" type="text" class="search-input" id="search-box" placeholder=""/>
				</div>

				<br class="clear"/>

<%--                <div id="page-processing">--%>
<%--	                <img src="/o/images/processing-dos.gif" style="height:50px; width:50px; position:absolute; right:240px; top:3px;"/>--%>
<%--                    <span class="information" id="processing-message"></span>--%>
<%--                </div>--%>

			</div>

            <div id="navigation-container" class="float-right">
                <a ng-click="toggleProfile()" href="javascript:" id="profile-actions-href" style="margin-right:37px;">
                    <img src="/o/${sessionScope.imageUri}" id="profile-ref-image" style="z-index:1"/>
                    <span ng-show="data.messagesCount" id="base-notifications-count">{{data.messagesCount}}</span>
                </a>

                <div ng-show="showProfile" id="profile-picture-actions-container" class="global-shadow">
                    <a href="#!/profile/${sessionScope.account.id}" id="profile-href"  class="profile-popup-action"><span class="space"></span> Profile</a>
                    <a href="javascript:" id="messages-href"  class="profile-popup-action" ng-click="renderMessages(${sessionScope.account.id})"><span id="latest-messages-total" class="space">{{data.messagesCount}}</span> Unread</a>
                    <a href="/o/signout" class="profile-popup-action" ><span class="space"></span> Logout</a>
                </div>
            </div>

		</div>

        <div id="content-container" ng-view autoscroll="true"></div>

	</div>


    <div ng-show="chatStarted" id="chat-session-outer-wrapper" class="global-shadow">
        <div id="chat-inner-wrapper">
            <div id="chat-session-header-wrapper">
                <a ng-href="#!/profile/{{recipientId}}"><img ng-src="/o/{{imageUri}}" id="chat-header-img"/></a>
                <span ng-click="toggleChat()" id="close-chat-session" class="yella">x</span>
            </div>
            <div id="chat-session">
                <div class="chat-content-container" ng-repeat="message in messages">
                    <p class="chat-who"><span class="from">{{message.sender}}</span><span class="time-ago">{{message.timeAgo}}</span></p>
                    <p class="chat-content">{{message.content}}</p>
                </div>
            </div>
            <form id="chat-session-frm">
                <textarea ng-keyup="sendChat(recipientId, $event)" id="chat-input" placeholder="Beginning chat..." name="content" style="width:186px;"></textarea>
            </form>
        </div>
    </div>

    <div ng-class="{'opened': chatOpened}" ng-click="openChat()" id="chat-launcher-popup" class="global-shadow chat-launcher" >
        <div id="chat-header">
            <h2 id="friends-launcher" data-launched="false" class="chat-launcher">Messages</h2>
        </div>
        <div id="friends-wrapper-container">
            <table id="friends-wrapper">
                <tr ng-click="startChat(friend.friendId)"  ng-repeat="friend in friends" class="friend-wrapper">
                    <td><a href="javascript:" ng-class="" class="lightf chat-session-launcher">{{friend.name}}</a></td>
                    <td>
                        <img ng-src="/o/{{friend.imageUri}}" class="chat-session-launcher" data-id="{{friend.friendId}}"/>
                        <span class="online-indicator" ng-class="{'online' : friend.isOnline}"></span>
                    </td>
                </tr>
            </table>
        </div>
    </div>

<%--    <h1 style="transform:  rotate(-90deg); opacity:0.1; position:absolute; left:-120px; top:270px;">Activity Feed</h1>--%>


    <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 134 134" id="amadeus-icon">
        <path d="M49 1L21 88L57 88L42 134L84 134L113 47L92 47L79 47L75 47L91 1L49 1Z" />
    </svg>

<%--    <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 171 171" width="171" height="171" id="amadeus-icon">--%>
<%--        <path d="M73 108L38 108L92 21L107 21L91 67L129 67L74 154L58 154L73 108Z"/>--%>
<%--    </svg>--%>


<script>

    var app = angular.module("app", ['ngRoute', 'ngAnimate', 'ngSanitize'])

    app.config(function($sceProvider) {
        $sceProvider.enabled(false);
    });

    app.run(function ($rootScope) {
        $rootScope.indicator = document.querySelector("#linear-indicator")

        $rootScope.$on("$routeChangeStart", function () {
            $rootScope.indicator.style.display = 'block'
        });

        $rootScope.$on("$routeChangeSuccess", function () {
            $rootScope.indicator.style.display = 'none'
        });
    })

    app.config(function($routeProvider) {
        $routeProvider
            .when('/', {
                templateUrl: 'pages/activity.html',
                controller: 'activityController'
            })
            .when('/profile/:id', {
                templateUrl: 'pages/profile.html',
                controller: 'profileController'
            })
            .when('/search/:q', {
                templateUrl: 'pages/search.html',
                controller: 'searchController'
            })
            .when('/search', {
                templateUrl: 'pages/search.html',
                controller: 'searchController'
            })
            .when('/invitations', {
                templateUrl: 'pages/invitations.html',
                controller: 'invitationController'
            })
            .when('/get', {
                templateUrl: 'pages/get.html',
                controller: 'getController'
            })
            .otherwise({redirectTo:'/'});
    });

    app.controller('baseController', function($http, $route, $scope, $interval, $timeout, $location, $anchorScroll, $window, dataService) {

        $scope.chatInput = document.querySelector("#chat-input")
        $scope.chatSession = document.querySelector("#chat-session")

        $scope.navigateSearch = function($event){
            if($event.key == "Enter") {
                var q = document.querySelector("#search-box").value
                $window.location.href = '#!/search/' + q
            }
        }

        $scope.init = function(){
            $scope.headerPoll = $interval(getData, 2300)
        }

        var getData = function(){
            $http.get("/o/profile/data").then(function(data){
                $scope.data = data.data
            })
        }

        $scope.toggleNotifications = function(){
            $scope.showNotifications = $scope.showNotifications ? false : true
        }

        $scope.toggleProfile = function(){
            $scope.showProfile = $scope.showProfile ? false : true
        }

        $scope.reloadActivities = function(){
            if($route.current.loadedTemplateUrl != "pages/activity.html"){
                $location.path("/")
            }else{
                $route.reload()
            }
        }

        $scope.openChat = function(){
            $http.get('/o/friends/' + ${sessionScope.account.id}).then(function(response){
                $scope.friends = response.data
                $scope.chatOpened = $scope.chatOpened ? false : true
                if(!$scope.chatOpened)$interval.cancel($scope.chatInterval)
            })
        }

        $scope.startChat = function(friendId){
            $scope.friendId = friendId
            $http.get('/o/messages/' + friendId).then(function(response){
                $scope.messages = response.data.messages
                $scope.recipientId = response.data.recipientId
                $scope.imageUri = response.data.recipientImageUri
                $scope.chatStarted = $scope.chatStarted ? false : true
                $scope.chatSession.scrollBottom = $scope.chatSession.scrollHeight
                $scope.chatInterval = $interval(getChatMessages, 2300)
            })
        }

        $scope.sendChat = function(id, $event){
            if($event.key == "Enter") {
                var message = $scope.chatInput.value

                var fd = new FormData();
                fd.append('content', message)

                $http({
                    method: 'post',
                    url: '/o/message/send/' + id,
                    data: fd,
                    headers: {'Content-Type': undefined},
                }).then(function(){
                    dataService.getChatMessages($scope.friendId, function(response){
                        $scope.messages = response.data.messages
                        $scope.recipientId = response.data.recipientId
                        $scope.imageUri = response.data.recipientImageUri
                        $scope.chatInput.value = ''
                        $scope.chatSession.scrollTop = $scope.chatSession.scrollHeight
                    })
                })
            }
        }

        var getChatMessages = function(){
            console.log("twak ery 2300")
            dataService.getChatMessages($scope.friendId, function(response){
                console.log('here..', response)
                $scope.messages = response.data.messages
                $scope.recipientId = response.data.recipientId
                $scope.imageUri = response.data.recipientImageUri
                $scope.chatSession.scrollTop = $scope.chatSession.scrollHeight
            })
        }

        $scope.toggleChat = function(){
            $scope.chatStarted = $scope.chatStarted ? false : true
            if(!$scope.chatStarted)$interval.cancel($scope.chatInterval)
        }

    });


    app.controller('activityController', function($scope, $http, $route, $interval, $timeout, $location, $anchorScroll, $sce, dataService) {

        $scope.postButton = document.querySelector("#share-button")

        var navigatePost = function(id){
            console.log('navigate post', id)
            $location.hash('post-' + id);
            $anchorScroll();
        }

        var getData = function() {
            dataService.getActivities(setData)
        }

        var setData = function(response){
            $scope.activities = response.data.activities
            $scope.femsfellas = response.data.femsfellas
            $timeout(setAnchors, 1300)
        }

        $scope.shareWhatsup = function(){
            $scope.postButton.value = "Amadeus!"
            var content = document.querySelector("#whatsup").value
            var images = document.querySelector("#post-upload-image-files").files
            var videos = document.querySelector("#post-upload-video-files").files

            var fd = new FormData();
            angular.forEach(images, function(file){
                fd.append('imageFiles',file);
            });

            angular.forEach(videos, function(file){
                fd.append('videoFile',file);
            });

            fd.append('content', content)
            $http({
                method: 'post',
                url: '/o/post/share',
                data: fd,
                headers: {'Content-Type': undefined},
            }).then($route.reload)
        }

        $scope.clearNotifications = function(){
            $http.delete('/o/notifications/clear').then($route.reload)
        }

        document.querySelector("#whatsup").focus()

        getData()

    })

    app.controller('searchController', function($scope, $http, $location, $route, $window) {
        console.log("in search controller")

        var searchData = function(){
            var q = $route.current.params.q

            $http.get('/o/search?q=' + q).then(function(response){
                console.log(response.data)
                $scope.accounts = response.data.accounts
            })
        }

        $scope.sendInvite = function(id){
            $http.post('/o/friend/invite/' + id).then($route.reload)
        }

        searchData()
    });

    app.controller('invitationController', function($scope, $http, $route, dataService) {

        var getData = function() {
            dataService.getInvitations(setData)
        }

        var setData = function(response){
            $scope.invitations = response.data
        }

        $scope.ignoreInvitation = function(id){
            $http.post('/o/friend/ignore/' + id).then($route.reload)
        }

        $scope.acceptInvitation = function(id){
            $http.post('/o/friend/accept/' + id).then($route.reload)
        }

        getData()
    });

    app.controller('getController', function($scope) {
        $scope.pageClass = 'page-contact';
    });

    app.controller('mixController', function($scope, $sce, $route, $http, $location, $anchorScroll, dataService){

        $scope.navigatePost = function(id){
            $location.hash('post-' + id);
            $anchorScroll();
        }

        $scope.likePost = function(id){
            dataService.likePost(id, $route.reload)
        }

        $scope.toggle = [];
        $scope.toggleShare = function(inx) {
            $scope.toggle[inx] = $scope.toggle[inx] ? false : true;
        }

        $scope.sharePost = function(id, $event){
            var comment = document.querySelector("#share-comment-" + id).value
            var postId = $event.target.attributes['data-id'].value
            dataService.sharePost(postId, comment, $route.reload)
        }

        $scope.deletePost = function(id){
            var confirmed = confirm("Are you sure you want to delete this post?")
            if(confirmed)
                $http.delete("/o/post/remove/" + id).then($route.reload);
        }

        $scope.unsharePost = function(shareId){
            var confirmed = confirm("Are you sure you want to unshare this post?")
            if(confirmed)
                $http.delete("/o/post/unshare/" + shareId).then($route.reload);
        }

        $scope.flagPost = function(id, shared){
            var confirmed = confirm("Are you sure you want to flag this post?")
            if(confirmed)
                $http.post("/o/post/flag/" + id + "/" + shared).then($route.reload);
        }

        $scope.hidePost = function(id){
            var confirmed = confirm("Are you sure you want to hide this post?")
            if(confirmed)
                $http.post("/o/post/hide/" + id).then($route.reload);
        }

        $scope.saveComment = function(id){
            var comment = document.querySelector("#post-comment-" + id).value
            if(comment != '') {
                dataService.saveComment(id, comment, $route.reload)
            }
        }

        $scope.saveShareComment = function(id){
            var comment = document.querySelector("#post-share-comment-" + id).value
            if(comment != '') {
                dataService.saveShareComment(id, comment, $route.reload)
            }
        }

        $scope.deleteComment = function(id){
            dataService.deleteComment(id, $route.reload)
        }

        $scope.deleteShareComment = function(id){
            dataService.deleteShareComment(id, $route.reload)
        }

        $scope.uploadImages = function(id, $event){

            var images = $event.target.files;

            var fd = new FormData();
            angular.forEach(images, function(file){
                fd.append('imageFiles',file);
            });

            $http({
                method: 'post',
                url: '/o/post/image/add/' + id,
                data: fd,
                headers: {'Content-Type': undefined},
            }).then($route.reload)
        }

        $scope.deleteImage = function(id, imageUri){
            var fd = new FormData();
            fd.append('imageUri', imageUri)
            $http({
                method: 'post',
                url: '/o/image/delete/' + id,
                data: fd,
                headers: {'Content-Type': undefined},
            }).then($route.reload)
        }

        $scope.updatePost = function(id){
            var fd = new FormData();
            var content = document.querySelector("#post-content-" + id).innerHTML
            fd.append('content', content);

            $http({
                method: 'post',
                url: '/o/post/update/' + id,
                data: fd,
                headers: {'Content-Type': undefined},
            }).then($route.reload)
        }

    });


    app.controller('profileController', function($scope, $http, $route, $timeout, dataService) {
        var self = this


        $scope.unfriend = function(id){
            $http.post('/o/friend/remove/' + id).then($route.reload)
        }

        $scope.sendFriendRequest = function(id){
            $http.post('/o/friend/invite/' + id).then($route.reload)
        }

        $scope.toggeBlock = function(id){
            $http.post('/o/account/block/' + id).then(function(){
                $scope.personBlocked = $scope.personBlocked ? false : true
            })
        }

        var getData = function(id) {
            self.id = id;
            dataService.getBaseProfile(id, setData)
        }

        var setData = function(response){
            console.log(response)
            $scope.personBlocked = response.data.profile.blocked
            $scope.profile = response.data.profile
            $scope.friends = response.data.friends

            $http.get("/o/posts/" + self.id).then(function(response){
                $scope.activities = response.data
                $timeout(setAnchors, 1300)
            })
        }

        getData($route.current.params.id)
    });

    app.controller('getController', function($scope) {
        $scope.pageClass = 'page-contact';
    });


    angular.module('app')
        .service('dataService', function($http) {

            this.getChatMessages = function(friendId, callback){
                $http.get("/o/messages/" + friendId).then(callback)
            }

            this.dataPoll = function(){
                $http.get("/o/profile/data").then(callback)
            }

            this.getActivities = function(callback){
                $http.get("/o/activity").then(callback)
            }

            this.likePost = function(id, callback){
                $http.post("/o/post/like/" + id).then(callback);
            }

            this.saveComment = function(id, comment, callback){
                var postComment = { comment : comment };
                $http.post("/o/post/comment/" + id, postComment).then(callback);
            };

            this.deleteComment = function(id, callback){
                $http.delete("/o/post/delete_comment/" + id).then(callback);
            }

            this.saveShareComment = function(id, comment, callback){
                var postComment = { comment : comment };
                $http.post("/o/post_share/comment/" + id, postComment).then(callback);
            }

            this.sharePost = function(id, comment, callback){
                var shareComment = { comment : comment };
                $http.post("/o/post/share/" + id, shareComment).then(callback);
            }

            this.getBaseProfile = function(id, callback){
                $http.get("/o/profile/" + id).then(callback)
            }

            this.getInvitations = function(callback){
                $http.get("/o/friend/invitations").then(callback)
            };

            this.saveTodo = function(todo, callback) {
                $http.put(todosGrailsServerUri + '/' + todo.id,todo).then(callback);
            };
        });


        angular.module("app").directive("ngUploadChange",function(){
            return{
                scope:{
                    ngUploadChange:"&"
                },
                link:function($scope, $element, $attrs){
                    $element.on("change",function(event){
                        $scope.$apply(function(){
                            $scope.ngUploadChange({$event: event})
                        })
                    })
                    $scope.$on("$destroy",function(){
                        $element.off();
                    });
                }
            }
        });

        var setAnchors = function(){
            var comments = document.getElementsByClassName("post-comment")
            var commentsArr = Array.from(comments)

            commentsArr.forEach(function(comment, index){
                var data = {
                    input: comment.innerHTML,
                    options: {
                        attributes: {
                            target: "_blank",
                            class: "href-dotted"
                        },
                    }
                }
                var hyperlinked = anchorme(data)
                comment.innerHTML = hyperlinked
            })
        }
</script>
</body>
