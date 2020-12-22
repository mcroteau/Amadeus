<%@ page import="java.util.Random" %>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!doctype html><%--${pageContext.response.locale}--%>
<html lang="en" dir="i18n">
<head>
    <title>Amadeus : Like. Share. Rock Me Amadeus!</title>

    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=0">

    <link rel="icon" type="image/png" href="/o/images/favicon.png?v=<%=System.currentTimeMillis()%>">

    <script type="text/javascript" src="/o/js/packages/angular.min.js"></script>
    <script type="text/javascript" src="/o/js/packages/angular-sanitize.js"></script>
    <script type="text/javascript" src="/o/js/packages/angular-animate.js"></script>
    <script type="text/javascript" src="/o/js/packages/angular-route.js"></script>
    <script type="text/javascript" src="/o/js/packages/anchorme.js"></script>
    <script type="text/javascript" src="/o/js/packages/jquery.js"></script>
    <script type="text/javascript" src="/o/js/packages/jquery.i18n.js"></script>
    <script type="text/javascript" src="/o/js/packages/jquery.i18n.messagestore.js"></script>

    <link rel="stylesheet" href="/o/css/app.gap.css?v=<%=System.currentTimeMillis()%>"/>
    <link rel="stylesheet" href="/o/css/app.gap.mobile.css?v=<%=System.currentTimeMillis()%>"/>

</head>

<body ng-app="app" ng-controller="baseController">

<%
    String[] vizs = {"/o/jsp/static/vis/candy.jsp",
                     "/o/jsp/static/vis/graph.jsp",
                     "/o/jsp/static/vis/pond.jsp",
                     "/o/jsp/static/vis/mucho.jsp",
                     "/o/jsp/static/vis/correct.jsp",
                     "/o/jsp/static/vis/space.jsp"};

    Random ran = new Random();
    int inx = ran.nextInt(vizs.length);
    String viz = vizs[inx];
%>


<%--    <iframe id="viz" src="<%=viz%>" style="z-index:1;position:fixed;bottom:0px;width:100%;height:79%;"></iframe>--%>
<iframe src="/o/jsp/static/vis/space.jsp" style="z-index:1;position:fixed;bottom:0px;width:100%;height:79%;"></iframe>
<%--    <canvas id="sugarcookie" style="z-index:1;position:fixed;bottom:0px;width:100%;height:79%;"></canvas>--%>


    <div ng-if="$root.renderModal" id="amadeus-modal">
        <div id="amadeus-model-content">
            <svg id="amadeus-modal-icon" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 171 171" width="171" height="171">
                <path d="M73 108L38 108L92 21L107 21L91 67L129 67L74 154L58 154L73 108Z"/>
            </svg>
        </div>
    </div>

    <div id="linear-indicator">
        <div class="indeterminate" style="width: 100%"></div>
    </div>

	<div ng-click="closeDialogs" id="layout-container" style="position:relative;">

		<div id="top-outer-container" ng-init="init()">

            <div id="logo-container" style="position:absolute;">
                <a ng-click="reloadActivities()" href="javascript:" id="logo-logo">
                    <svg id="amadeus-logo" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 171 171" width="171" height="171">
                        <path d="M73 108L38 108L92 21L107 21L91 67L129 67L74 154L58 154L73 108Z"/>
                    </svg>
                    <span id="latest-feed-total" class="notifications-count" style="display:inline-block; position:absolute;bottom:3px;left:54px;">{{data.newestCount}}</span></a>
            </div>

			<div id="top-inner-container">

                <div ng-if="!$root.profilePage" id="search-container" class="float-left" style="z-index:100">
<%--                    <div id="search-label" data-i18n="search.text">Search:</div>--%>
                    <input ng-keyup="navigateSearch($event)" type="text" class="search-input" id="search-box" placeholder="Search:" autocomplete="off"/>
                </div>

				<br class="clear"/>

			</div>

            <div id="navigation-container" class="float-right">
                <a ng-click="toggleProfile()" href="javascript:" id="profile-actions-href" class="profile-popup" style="margin-right:37px;">
                    <img src="${sessionScope.imageUri}" id="profile-ref-image" style="z-index:1"/>
                    <span ng-show="data.messagesCount" id="base-notifications-count">{{data.messagesCount}}</span>
                </a>

                <div ng-show="showProfile" id="profile-picture-actions-container" class="global-shadow">
                    <a href="#!/profile/${sessionScope.account.id}" id="profile-href"  class="profile-popup-action"><span class="space"></span> <span data-i18n="profile.text">Profile</span></a>
                    <a ng-click="openChat()" href="javascript:" id="messages-href" class="profile-popup-action" ng-click="renderMessages(${sessionScope.account.id})"><span id="latest-messages-total" class="space">{{data.messagesCount}}</span> <span data-i18n="unread.text">Unread</span></a>
                    <a href="/o/signout" class="profile-popup-action" ><span class="space"></span> <span data-i18n="logout.text">Logout</span></a>
                </div>
            </div>

		</div>

	</div>

    <div id="mobile-outer-container">

        <div id="mobile-search-outer-container" style="position:relative;">

            <div id="mobile-search-container" class="float-left">
                <input ng-keyup="navigateSearch($event)" type="text" class="search-input" id="search-box" placeholder="Search:"/>
                <br class="clear"/>
            </div>
        </div>

        <div id="navigation-outer-container">
            <a ng-click="toggleProfile()" href="javascript:" id="profile-actions-href" class="profile-popup" style="margin-right:37px;">
                <img src="${sessionScope.imageUri}" id="profile-ref-image" style="z-index:1"/>
                <span ng-show="data.messagesCount" id="base-notifications-count">{{data.messagesCount}}</span>
            </a>

            <div ng-show="showProfile" id="profile-picture-actions-container" class="global-shadow">
                <a href="#!/profile/${sessionScope.account.id}" id="profile-href"  class="profile-popup-action"><span class="space"></span> <span data-i18n="profile.text">Profile</span></a>
                <a ng-click="openChat()" href="javascript:" id="messages-href" class="profile-popup-action render-desktop" ng-click="renderMessages(${sessionScope.account.id})"><span id="latest-messages-total" class="space">{{data.messagesCount}}</span> <span data-i18n="unread.text">Profile</span></a>
                <a href="/o/signout" class="profile-popup-action" ><span class="space"></span> <span data-i18n="logout.text">Logout</span></a>
            </div>
        </div>
        <br class="clear"/>
    </div>



    <div id="content-container" ng-view autoscroll="true"></div>



    <div ng-show="chatStarted" id="chat-session-outer-wrapper" class="global-shadow">
        <div id="chat-inner-wrapper">
            <div id="chat-session-header-wrapper">
                <a ng-href="#!/profile/{{recipientId}}"><img ng-src="{{imageUri}}" id="chat-header-img"/></a>
                <span ng-click="toggleChat()" id="close-chat-session" class="yella">x</span>
            </div>
            <div id="chat-session">
                <div class="chat-content-container" ng-repeat="message in messages">
                    <p class="chat-who"><span class="from">{{message.sender}}</span><span class="time-ago">{{message.timeAgo}}</span></p>
                    <p class="chat-content">{{message.content}}</p>
                </div>
            </div>
            <form id="chat-session-frm">
                <textarea ng-keyup="sendChat(recipientId, $event)" id="chat-input" placeholder="Begin chat..." name="content" style="width:186px;"></textarea>
            </form>
        </div>
    </div>

    <div ng-class="{'opened': chatOpened}" ng-click="openChat()" id="chat-launcher-popup" class="global-shadow chat-launcher chat-session-popup" >
        <div id="chat-header">
            <h2 id="friends-launcher" data-launched="false" class="chat-launcher">Messages</h2>
        </div>
        <div id="friends-wrapper-container">
            <table id="friends-wrapper">
                <tr ng-click="startChat(friend.friendId)"  ng-repeat="friend in friends" class="friend-wrapper">
                    <td><a href="javascript:" ng-class="" class="lightf chat-session-launcher">{{friend.name}}</a></td>
                    <td>
                        <img ng-src="{{friend.imageUri}}" class="chat-session-launcher" data-id="{{friend.friendId}}"/>
                        <span class="online-indicator" ng-class="{'online' : friend.isOnline}"></span>
                    </td>
                </tr>
            </table>
        </div>
    </div>


<%--    <div ng-if="$root.renderFooter" id="footer">--%>
<%--        <div id="footer" style="z-index:2001">--%>
<%--            <div style="text-align:center;margin-top:10px;">--%>
<%--                <a href="${pageContext.request.contextPath}/get_code" class="page-ref href-dotted" >Get Code</a>--%>
<%--                <a href="javascript:" class="page-ref href-dotted" data-ref="about">About</a>--%>
<%--                <a href="${pageContext.request.contextPath}/invite" class="href-dotted" id="invite-people">Invite</a>--%>
<%--            </div>--%>

<%--            <p style="text-align:center;"><a href="mailto:support@amadeus.social" style="color:#17161b" class="href-dotted">support@amadeus.social</a>--%>
<%--                <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 134 134" id="amadeus-icon">--%>
<%--                    <path d="M49 1L21 88L57 88L42 134L84 134L113 47L92 47L79 47L75 47L91 1L49 1Z" />--%>
<%--                </svg>--%>
<%--            </p>--%>

<%--            <div style="text-align:center;margin:20px auto 109px auto">--%>
<%--                <p style="text-align: center; font-size:12px;">&copy; 2020 Amadeus</p>--%>
<%--                <a href="http://tomcat.apache.org/" target="_blank" class="information">Powered by<br/>Tomcat</a>--%>
<%--            </div>--%>
<%--        </div>--%>
<%--    </div>--%>

<script>

    var app = angular.module("app", ['ngRoute', 'ngAnimate', 'ngSanitize'])

    app.config(function($sceProvider) {
        $sceProvider.enabled(false);
    }).factory('activityModel', function () {
        var factory = {};
        var model = {};

        factory.set = function (key, value) {
            model[key] = value;
        }
        factory.get = function (key) {
            return model[key];
        };
        return factory;
    })

    app.run(function ($rootScope, $location) {
        $rootScope.gettingData = false;
        $rootScope.indicator = document.querySelector("#linear-indicator")

        $rootScope.$on("$routeChangeStart", function () {
            $rootScope.indicator.style.display = 'block'
            $rootScope.renderFooter = false;
        });

        $rootScope.$on("$routeChangeSuccess", function () {
            $rootScope.indicator.style.display = 'none'
            console.log($location.path());

            if($location.path().includes('/profile')){
                console.log("profile page")
                $rootScope.profilePage = true;
            }else{
                $rootScope.profilePage = false;
                $rootScope.renderFooter = true;
            }


        });

        $rootScope.internationalize = function(){
            // $.i18n.debug = true;
            i18n = $.i18n()
            i18n.load().done(function(){
                $('[data-i18n]').each(function(idx, keyElm){
                    var key = $(keyElm).attr('data-i18n')
                    var value = $.i18n(key)
                    $(keyElm).html(value)
                })
            })
        }

    })

    app.config(function($routeProvider) {

        var t = new Date().getTime()

        $routeProvider
            .when('/', {
                templateUrl: 'pages/activity.html?v=' + t,
                controller: 'activityController'
            })
            .when('/profile/:id', {
                templateUrl: 'pages/profile.html?v=' + t,
                controller: 'profileController'
            })
            .when('/search/:q', {
                templateUrl: 'pages/search.html?v=' + t,
                controller: 'searchController'
            })
            .when('/search', {
                templateUrl: 'pages/search.html?v=' + t,
                controller: 'searchController'
            })
            .when('/invitations', {
                templateUrl: 'pages/invitations.html?v=' + t,
                controller: 'invitationController'
            })
            .otherwise({redirectTo:'/'});
    });

    app.controller('baseController', function($http, $rootScope, $route, $scope, $interval, $timeout, $location, $anchorScroll, $window, dataService) {

        $scope.showProfile = false
        $scope.showNotifications = false

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
            $rootScope.gettingData = true
            $http.get("/o/profile/data").then(function(data){
                if(data.error)$window.location.href= "/"
                $scope.data = data.data
                $rootScope.gettingData = false
            })
        }

        $scope.toggleNotifications = function(){
            $scope.showNotifications = !$scope.showNotifications
        }

        $scope.toggleProfile = function(){
            $scope.showProfile = !$scope.showProfile
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
                $scope.friends = response.data.friends
                $scope.chatOpened = !$scope.chatOpened
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
                $http.post('/o/messages/read/' + friendId).then(function(){})
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
            dataService.getChatMessages($scope.friendId, function(response){
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

        $scope.closeDialogs = function(event) {
            var $target = $(event.target);
            if (!$target.hasClass('profile-popup') &&
                    !$target.hasClass('notifications-popup') &&
                        !$target.hasClass('chat-session-popup')){
                $scope.chatOpened = false
                $scope.showProfile = false
                $scope.showNotifications = false
            }
        }

        $('body').click($scope.closeDialogs)

    });


    app.controller('activityController', function($scope, $rootScope, $http, $route, $interval, $timeout, $location, $anchorScroll, $sce, $window, activityModel, dataService) {

        $scope.whatsup = document.querySelector("#whatsup")
        $scope.postButton = document.querySelector("#share-button")

        var navigatePost = function(id){
            $location.hash('post-' + id);
            $anchorScroll();
        }

        var getData = function() {
            dataService.getActivities(setData)
        }

        var setData = function(response){
            $scope.activities = response.data.posts
            $scope.femsfellas = response.data.accounts
            $scope.memory = $scope.activities

            activityModel.set('memory', $scope.memory)
            activityModel.set('activities', $scope.activities)

            $timeout(setAnchors, 1300)
        }

        $scope.shareWhatsup = function(){
            var whatsup = document.querySelector('#whatsup')
            $scope.postButton.innerHtml = "Amadeus!"
            var content = $scope.whatsup.value
            var images = document.querySelector("#post-upload-image-files").files
            var videos = document.querySelector("#post-upload-video-files").files

            if(whatsup.value == '' &&
                images.length == 0 &&
                    videos.length == 0){
                alert('Express yourself!')
                return false;
            }

            $rootScope.renderModal = true

            var fd = new FormData();
            angular.forEach(images, function(file){
                fd.append('imageFiles',file);
            });

            angular.forEach(videos, function(file){
                fd.append('videoFile',file);
            });

            fd.append('content', content)

            if(!$rootScope.gettingData) {
                $http({
                    method: 'post',
                    url: '/o/post/save',
                    data: fd,
                    headers: {'Content-Type': undefined},
                }).then(function (response) {
                    document.querySelector('#whatsup').value = ''
                    $scope.beautiful = $scope.beautiful ? false : true
                    response.data.published = false
                    $scope.activities.unshift(response.data)
                    $rootScope.renderModal = false
                    $scope.mediaSelected = false
                    document.querySelector("#post-upload-image-files").value = ''
                    document.querySelector("#post-upload-video-files").value = ''
                })
            }
        }

        $scope.maintainView = function(list, id){
            angular.forEach(list, function(activity){
                if(activity.id == id) {
                    activity.live = false
                    activity.published = true
                }
            })
        }

        $scope.filterActivities = function(id){
            $scope.activities = []
            angular.forEach($scope.memory, function(activity){
                if(activity.accountId == id){
                    $scope.activities.push(activity)
                }
            })
        }

        $scope.clearNotifications = function(){
            $rootScope.renderModal = true
            $http.delete('/o/notifications/clear').then(function(response){
                $rootScope.renderModal = false
                $route.reload()
            }).catch(function(error){
                $rootScope.renderModal = false
                console.log(error)
            })
        }

        $scope.onMediaSelected = function($evt){
            $scope.mediaSelected = $scope.mediaSelected ? false : true
        }

        document.querySelector("#whatsup").focus()

        getData()

    })

    app.controller('searchController', function($scope, $rootScope, $http, $location, $route, $window) {
        var searchData = function(){
            var q = $route.current.params.q

            $http.get('/o/search?q=' + q).then(function(response){
                $scope.accounts = response.data.accounts
            })
        }

        $scope.sendInvite = function(id){
            $http.post('/o/friend/invite/' + id).then($route.reload)
        }

        searchData()
    });

    app.controller('invitationController', function($scope, $rootScope, $http, $route, dataService) {

        var getData = function() {
            dataService.getInvitations(setData)
        }

        var setData = function(response){
            $scope.invitations = response.data.invites
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

    app.controller('mixController', function($scope, $rootScope, $sce, $route, $http, $location, $window, $anchorScroll, activityModel, dataService){

        $scope.makeLive = function(id){
            $rootScope.renderModal = true
            $http.post("/o/post/publish/" + id).then(function(){
                $rootScope.renderModal = false
                $window.location.reload()
            });
        }

        $scope.navigatePost = function(id){
            $location.hash('post-' + id);
            $anchorScroll();
        }

        $scope.likePost = function(id){
            dataService.likePost(id, function(response){
                var id = response.data.id
                document.querySelector("#likes-" + id).innerHTML = response.data.likes
                document.querySelector("#post-like-" + id).classList.toggle("liked")
            })
        }

        $scope.toggle = []
        $scope.commentToggle = []
        $scope.toggleShare = function(idx) {
            $scope.toggle[idx] = $scope.toggle[idx] ? false : true;
            $scope.commentToggle[idx] = $scope.commentToggle[idx] ? false : true;
        }


        $scope.showActions = []
        $scope.toggleActions = function(idx){
            console.log('toggle actions')
            $scope.showActions[idx] = $scope.showActions[idx] ? false : true;
        }

        $scope.sharePost = function(id, $event){
            $rootScope.renderModal = true
            var comment = document.querySelector("#share-comment-" + id).value
            var postId = $event.target.attributes['data-id'].value
            dataService.sharePost(postId, comment, function(){
                $rootScope.renderModal = false
                $window.location.reload()
            })
        }

        $scope.deletePost = function(id) {
            var confirmed = confirm("Are you sure you want to delete this post?")

            if(confirmed){
                $rootScope.renderModal = true

                $http.delete("/o/post/delete/" + id).then(function (response) {
                    // var id = response.data.post.id
                    // $scope.removePost(id, activityModel.get('memory'))
                    // $scope.removePost(id, activityModel.get('activities'))
                    $rootScope.renderModal = false
                    $window.location.reload()
                });
            }
        }

        $scope.removePost = function(id, list){
            angular.forEach(list, function(element, index){
                if(element.id === id){
                    list.splice(index, 1)
                }
            })
        }

        $scope.unsharePost = function(shareId){
            var confirmed = confirm("Are you sure you want to unshare this post?")
            if(confirmed) {
                $http.delete("/o/post/unshare/" + shareId).then(function(){
                    $window.location.reload()
                });
            }
        }

        $scope.flagPost = function(id, shared){
            var confirmed = confirm("Are you sure you want to flag this post?")
            if(confirmed) {
                $http.post("/o/post/flag/" + id + "/" + shared).then(function(){
                    $window.location.reload()
                });
            }
        }

        $scope.hidePost = function(id){
            var confirmed = confirm("Are you sure you want to hide this post?")
            if(confirmed) {
                $http.post("/o/post/hide/" + id).then(function(){
                    $window.location.reload()
                });
            }
        }

        $scope.saveComment = function(id){
            var comment = document.querySelector("#post-comment-" + id).value
            if(comment != '') {
                dataService.saveComment(id, comment, function(){
                    $window.location.reload()
                })
            }
        }

        $scope.saveShareComment = function(id){
            var comment = document.querySelector("#post-share-comment-" + id).value
            if(comment != '') {
                dataService.saveShareComment(id, comment, function(resp){
                    $window.location.reload()
                })
            }
        }

        $scope.deleteComment = function(id){
            dataService.deleteComment(id, function(){
                $window.location.reload()
            })
        }

        $scope.deleteShareComment = function(id){
            dataService.deleteShareComment(id, function(){
                $window.location.reload()
            })
        }

        $scope.uploadImages = function(id, $event){

            $rootScope.renderModal = true

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
            }).then(function(){
                $rootScope.renderModal = false
                $window.location.reload()
            })
        }

        $scope.deleteImage = function(id, imageUri){

            var confirmed = confirm("Are you sure you want to delete this image?")

            if(confirmed) {
                var fd = new FormData();
                fd.append('imageUri', imageUri)
                $http({
                    method: 'post',
                    url: '/o/post/image/delete/' + id,
                    data: fd,
                    headers: {'Content-Type': undefined},
                }).then(function () {
                    $window.location.reload()
                })
            }
        }

        $scope.updatePost = function(id){
            $rootScope.renderModal = true

            var fd = new FormData();
            var content = document.querySelector("#post-content-" + id).innerHTML

            fd.append('content', content);

            $http({
                method: 'post',
                url: '/o/post/update/' + id,
                data: fd,
                headers: {'Content-Type': undefined},
            }).then(function (response) {
                $scope.maintainView($scope.memory, response.data.id)
                $scope.maintainView($scope.activities, response.data.id)
                $rootScope.renderModal = false
            })
        }

        $scope.maintainView = function(list, id){
            angular.forEach(list, function(activity){
                if(activity.id == id) activity.published = true
            })
        }

    });




    app.controller('profileController', function($scope, $rootScope, $http, $route, $timeout, dataService) {
        var self = this

        $scope.unfriend = function(id){
            $http.post('/o/friend/remove/' + id).then($route.reload)
        }

        $scope.sendFriendRequest = function(id){
            $http.post('/o/friend/invite/' + id).then($route.reload)
        }

        $scope.toggleBlock = function(id){
            $http.post('/o/account/block/' + id).then(function(){
                $scope.personBlocked = $scope.personBlocked ? false : true
            })
        }

        var getData = function(id) {
            self.id = id;
            dataService.getBaseProfile(id, setData)
        }

        var setData = function(response){
            $scope.personBlocked = response.data.profile.blocked
            $scope.profile = response.data.profile
            $scope.friends = response.data.friends

            $http.get("/o/post/account/" + self.id).then(function(response){
                $scope.activities = response.data.posts
                $timeout(setAnchors, 1300)
            })
            $http.get("/o/profile/data/views").then(function(response){
                $scope.week = response.data.week
                $scope.month = response.data.month
                $scope.all = response.data.all
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
                $http.get("/o/post/activity").then(callback)
            }

            this.likePost = function(id, callback){
                $http.post("/o/post/like/" + id).then(callback);
            }

            this.saveComment = function(id, comment, callback){
                var postComment = { comment : comment };
                $http.post("/o/post/comment/" + id, postComment).then(callback);
            };

            this.deleteComment = function(id, callback){
                $http.delete("/o/post/comment/delete/" + id).then(callback);
            }
            this.saveShareComment = function(id, comment, callback){
                var postComment = { comment : comment };
                $http.post("/o/post_share/comment/" + id, postComment).then(callback);
            }

            this.deleteShareComment = function(id, callback){
                $http.delete("/o/post_share/comment/delete/" + id).then(callback);
            }

            this.sharePost = function(id, comment, callback){
                var shareComment = { comment : comment };
                $http.post("/o/post/share/" + id, shareComment).then(callback);
            }

            this.getBaseProfile = function(id, callback){
                $http.get("/o/profile/" + id).then(callback)
            }

            this.getInvitations = function(callback){
                $http.get("/o/friend/invites").then(callback)
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

        function unwrap(wrapper) {
            var docFrag = document.createDocumentFragment();
            while (wrapper.firstChild) {
                var child = wrapper.removeChild(wrapper.firstChild);
                docFrag.appendChild(child);
            }
            wrapper.parentNode.replaceChild(docFrag, wrapper);
        }

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
