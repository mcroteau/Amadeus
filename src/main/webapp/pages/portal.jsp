<%@ page import="java.util.Random" %>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!doctype html><%--${pageContext.response.locale}--%>
<html lang="en" dir="i18n">
<head>
    <title>Amadeus: Social Elite*</title>

    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=0">

    <link rel="icon" type="image/png" href="/o/assets/images/icon.png?v=<%=System.currentTimeMillis()%>">

    <script type="text/javascript" src="/o/assets/js/packages/angular.min.js"></script>
    <script type="text/javascript" src="/o/assets/js/packages/angular-sanitize.js"></script>
    <script type="text/javascript" src="/o/assets/js/packages/angular-animate.js"></script>
    <script type="text/javascript" src="/o/assets/js/packages/angular-route.js"></script>
    <script type="text/javascript" src="/o/assets/js/packages/anchorme.js"></script>
    <script type="text/javascript" src="/o/assets/js/packages/jquery.js"></script>
    <script type="text/javascript" src="/o/assets/js/packages/jquery.i18n.js"></script>
    <script type="text/javascript" src="/o/assets/js/packages/jquery.i18n.messagestore.js"></script>

    <link rel="stylesheet" href="/o/assets/css/app.gap.css?v=<%=System.currentTimeMillis()%>"/>
    <link rel="stylesheet" href="/o/assets/css/app.astro.css?v=<%=System.currentTimeMillis()%>"/>
    <link rel="stylesheet" href="/o/assets/css/app.gap.mobile.css?v=<%=System.currentTimeMillis()%>"/>

</head>

<body ng-app="app" ng-controller="baseController">

<%
    String[] vizs = {"/o/pages/static/vis/candy.jsp",
                     "/o/pages/static/vis/graph.jsp",
//                     "/o/pages/static/vis/pond.jsp",
                     "/o/pages/static/vis/rah.jsp",
                     "/o/pages/static/vis/correct.jsp",
                     "/o/pages/static/vis/space.jsp"};

    Random ran = new Random();
    int inx = ran.nextInt(vizs.length);
    String viz = vizs[inx];
%>


<%--    <iframe id="viz" src="<%=viz%>" style="z-index:1;position:fixed;bottom:0px;width:100%;height:79%;"></iframe>--%>
<%--        <iframe id="viz" src="/o/jsp/static/vis/space.jsp" style="overflow:hidden;z-index:1;position:fixed;bottom:0px;width:100%;height:300px"></iframe>--%>
<%--    <canvas id="sugarcookie" style="z-index:1;position:fixed;bottom:0px;width:100%;height:79%;"></canvas>--%>

    <div id="logo-mobile" style="display:none;">
        <div id="logo-container" style="position:absolute;">
            <a ng-click="toggleBaseNavigation()" ng-class="{'active' : data.newestCount > 0}" href="javascript:" id="logo-logo">
                <svg id="amadeus-logo" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 70 70" width="70" height="70">
                    <g>
                        <path d="M46 31L46 36L57.01 36L57.06 40.91L62 41L62 31L46 31Z" />
                        <path fill-rule="evenodd" d="M16 20L21 20L32 48L4 48L16 20ZM11 44L25 44L19 26L18 26L11 44Z" />
                    </g>
                    <path fill-rule="evenodd" d="M38.5 23C36.01 23 34 20.99 34 18.5C34 16.01 36.01 14 38.5 14C40.99 14 43 16.01 43 18.5C43 20.99 40.99 23 38.5 23ZM40.6 18.5C40.6 17.34 39.66 16.4 38.5 16.4C37.34 16.4 36.4 17.34 36.4 18.5C36.4 19.66 37.34 20.6 38.5 20.6C39.66 20.6 40.6 19.66 40.6 18.5Z" />
                </svg>
                <span ng-show="data.newestCount > 0" id="latest-feed-total" class="notifications-count" style="display:inline-block; position:absolute;bottom:3px;left:54px;">{{data.newestCount}}</span></a>
        </div>
    </div>

    <div ng-show="showBaseNavigation" id="base-navigation-container" class="global-shadow">
        <a ng-click="reloadActivities()" href="javascript:" data-i18n="post.feed">Post Feed ({{data.newestCount}})</a>
        <a href="#!/profile/${sessionScope.account.id}" id="profile-href"  class="profile-popup-action" data-i18n="profile.text">Profile</a>
        <a href="/o/signout" class="profile-popup-action" data-i18n="logout.text">Logout</a>
    </div>

    <div ng-if="$root.renderModal" id="amadeus-modal">
        <div id="amadeus-modal-content">
            <svg id="amadeus-logo-modal" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 70 70" width="70" height="70">
                <g>
                    <path d="M46 31L46 36L57.01 36L57.06 40.91L62 41L62 31L46 31Z" />
                    <path fill-rule="evenodd" d="M16 20L21 20L32 48L4 48L16 20ZM11 44L25 44L19 26L18 26L11 44Z" />
                </g>
                <path fill-rule="evenodd" d="M38.5 23C36.01 23 34 20.99 34 18.5C34 16.01 36.01 14 38.5 14C40.99 14 43 16.01 43 18.5C43 20.99 40.99 23 38.5 23ZM40.6 18.5C40.6 17.34 39.66 16.4 38.5 16.4C37.34 16.4 36.4 17.34 36.4 18.5C36.4 19.66 37.34 20.6 38.5 20.6C39.66 20.6 40.6 19.66 40.6 18.5Z" />
            </svg>
            <span class="tiny" style="color:#fff">Processing</span>
        </div>
    </div>

    <div id="linear-indicator">
        <div class="indeterminate" style="width: 100%"></div>
    </div>

    <div id="notifications-wrapper">

        <div id="notifications-href-wrapper">
            <div id="notifications-href-inner">
                <a ng-click="toggleNotifications()" href="javascript:" class="navigation-href notifications-popup" id="notifications-href" style="margin-top:0px;margin-bottom:10px;">N<span id="notifications-count">{{data.notificationsCount}}</span></a>
            </div>
        </div>

        <div ng-show="showNotifications" id="notifications-outer-container" class="main-shadow">

            <a ng-click="clearNotifications()" ng-if="data.notifications.length > 0"  href="javascript:" id="clear-notifications" class="right-float href-dotted-light" style="margin-bottom:10px; margin-right:10px;" data-id="${sessionScope.account.id}" data-i18n="clear.text">Clear</a>

            <div class="notification" ng-controller="mixController" ng-repeat="notification in data.notifications">
                <a ng-if="!notification.invite" ng-click="navigatePost(notification.postId)" href="javascript:">
                    <span ng-if="notification.liked">{{notification.name}} <span data-i18n="liked.post.text">liked your post.</span></span>
                    <span ng-if="notification.shared">{{notification.name}} <span data-i18n="shared.post.text">shared your post.</span></span>
                    <span ng-if="notification.commented">{{notification.name}} <span data-i18n="commented.post.text">commented your post.</span></span>
                </a>
            </div>

            <div class="notification"><a href="javascript:"><span ng-if="data.notifications.length == 0" data-i18n="no.notifications">No new notifications</span></a></div>
        </div>
    </div>

    <div id="logo-container">
        <a ng-click="reloadActivities()" href="javascript:" id="logo-logo">
            <svg id="amadeus-logo" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 70 70" width="70" height="70">
                <g>
                    <path d="M46 31L46 36L57.01 36L57.06 40.91L62 41L62 31L46 31Z" />
                    <path fill-rule="evenodd" d="M16 20L21 20L32 48L4 48L16 20ZM11 44L25 44L19 26L18 26L11 44Z" />
                </g>
                <path fill-rule="evenodd" d="M38.5 23C36.01 23 34 20.99 34 18.5C34 16.01 36.01 14 38.5 14C40.99 14 43 16.01 43 18.5C43 20.99 40.99 23 38.5 23ZM40.6 18.5C40.6 17.34 39.66 16.4 38.5 16.4C37.34 16.4 36.4 17.34 36.4 18.5C36.4 19.66 37.34 20.6 38.5 20.6C39.66 20.6 40.6 19.66 40.6 18.5Z" />
            </svg>
            <span ng-show="data.newestCount > 0" id="latest-feed-total" class="notifications-count" style="display:inline-block; position:absolute;bottom:3px;left:54px;">{{data.newestCount}}</span>
        </a>
    </div>

    <a ng-click="toggleProfile()" href="javascript:" id="profile-actions-href" class="profile-popup">
        <img src="${sessionScope.imageUri}" id="profile-ref-image"/>
        <span ng-show="data.messagesCount" id="base-notifications-count">{{data.messagesCount}}</span>
    </a>

    <div ng-show="showProfile" id="profile-picture-actions-container" class="main-shadow">
        <a href="#!/profile/${sessionScope.account.id}" id="profile-href"  class="profile-popup-action"><span class="space"></span> <span data-i18n="profile.text">Profile</span></a>
        <a href="/o/signout" class="profile-popup-action"><span class="space"></span> <span data-i18n="logout.text">Logout</span></a>
    </div>

	<div id="layout-container" style="position:relative;">

		<div id="top-outer-container" ng-init="init()">

			<div id="top-inner-container">

                <div id="search-container">
                    <span id="search-express" class="expressed information" data-i18n="express.text">Search.</span>
                    <input ng-keyup="navigateSearch($event)" ng-focus="focusSearch($event)" type="text" class="search-input" id="search-box" placeholder="Search:" autocomplete="off"/>
                </div>

                <a href="javascript:" ng-click="toggleMobileNotifications()" id="mobile-notifications">{{data.notificationsCount}}</a>

                <div ng-show="showMobileNotifications" id="notifications-outer-container" class="global-shadow">

                    <a ng-click="clearNotifications()" ng-if="data.notifications.length > 0"  href="javascript:" id="clear-notifications" class="right-float href-dotted-light" style="margin-bottom:10px; margin-right:10px;" data-id="${sessionScope.account.id}" data-i18n="clear.text">Clear</a>

                    <div class="notification" ng-controller="mixController" ng-repeat="notification in data.notifications">

                        <a ng-if="!notification.invite" ng-click="navigatePost(notification.postId)" href="javascript:">
                            <span ng-if="notification.liked">{{notification.name}} <span data-i18n="liked.post.text">liked your post.</span></span>
                            <span ng-if="notification.shared">{{notification.name}} <span data-i18n="shared.post.text">shared your post.</span></span>
                            <span ng-if="notification.commented">{{notification.name}} <span data-i18n="commented.post.text">commented your post.</span></span>
                        </a>

                        <a ng-href="#!/invitations" ng-if="notification.invite" href="javascript:" class="invite-ref">{{notification.name}} <span data-i18n="invited.connect.text">invited you to connect.</span></a>

                    </div>

                    <div class="notification"><a href="javascript:"><span ng-if="data.notifications.length == 0" data-i18n="no.notifications">No new notifications</span></a></div>
                </div>

			</div>

		</div>

	</div>

    <div id="content-container" ng-view autoscroll="true"></div>


    <button ng-click="openMobilePost($event)" class="button retro poster global-shadow">+</button>

    <div ng-click="hideMobilePost($event)" id="mobile-post-modal" class="mobile-post"></div>

    <div id="mobile-post-container" class="mobile-post global-shadow">

        <span id="expressed" class="expressed information left-float" data-i18n="express.text" style="margin-top:0px;">New Post.</span>

        <br class="clear"/>

        <div id="whatsup-container">
            <textarea placeholder="The Lazy Cow jumped over the Moon." id="mobile-whatsup"></textarea>
            <p ng-if="mediaSelected" id="media-selected" class="tiny" data-i18n="media.selected">Media selected!</p>
        </div>

        <div id="actions-container" class="float-right" style="margin-top:8px;">

            <div id="image-upload-post-container" class="file-upload-container">
                <a href="javascript:" class="button yella small upload-button" id="image-button" data-i18n="images.button">Images</a>
                <input type="hidden" name="content" id="image-upload-content" class="file-upload-content"/>
                <input ng-upload-change="onMediaSelected($event)" type="file" name="imageFiles" id="mobile-post-upload-image-files" class="file-upload-input" data-upload="image" multiple="multiple" data-message="Image must be Gif, PNG, or JPG"/>
            </div>

            <div id="video-upload-post-container" class="file-upload-container">
                <a href="javascript:" class="button light small upload-button" id="video-button" data-i18n="video.button">Video</a>
                <input type="hidden" name="content" id="video-upload-content" class="file-upload-content"/>
                <input ng-upload-change="onMediaSelected($event)" type="file" name="videoFile" id="mobile-post-upload-video-files" class="file-upload-input" data-upload="video" data-message="Video must be MP4"/>
            </div>

        </div>

        <br class="clear"/>

        <a ng-click="shareWhatsupMobile()" href="javascript:" class="button retro" id="share-button" data-i18n="share.button" style="text-align:center;width:calc(100% - 40px) !important">Share!</a>

        <br class="clear"/>

        <a ng-click="hideMobilePost($event)" href="javascript:" class="button sky" data-i18n="cancel.button" style="text-align:center;width:calc(100% - 40px) !important;margin-top:10px; display:block">Cancel</a>


        <br class="clear"/>

    </div>


<%--    <div ng-show="chatStarted" id="chat-session-outer-wrapper" class="global-shadow">--%>
<%--        <div id="chat-inner-wrapper">--%>
<%--            <div id="chat-session-header-wrapper">--%>
<%--                <a ng-href="#!/profile/{{recipientId}}"><img ng-src="{{imageUri}}" id="chat-header-img"/></a>--%>
<%--                <span ng-click="toggleChat()" id="close-chat-session" class="yella">x</span>--%>
<%--            </div>--%>
<%--            <div id="chat-session">--%>
<%--                <div class="chat-content-container" ng-repeat="message in messages">--%>
<%--                    <p class="chat-who"><span class="from">{{message.sender}}</span><span class="time-ago">{{message.timeAgo}}</span></p>--%>
<%--                    <p class="chat-content">{{message.content}}</p>--%>
<%--                </div>--%>
<%--            </div>--%>
<%--            <form id="chat-session-frm">--%>
<%--                <textarea ng-keyup="sendChat(recipientId, $event)" id="chat-input" placeholder="Begin chat..." name="content" style="width:186px;"></textarea>--%>
<%--            </form>--%>
<%--        </div>--%>
<%--    </div>--%>

<%--    <div ng-class="{'opened': chatOpened}" ng-click="openChat()" id="chat-launcher-popup" class="global-shadow chat-launcher chat-session-popup" >--%>
<%--        <div id="chat-header">--%>
<%--            <h2 id="friends-launcher" data-launched="false" class="chat-launcher">Messages</h2>--%>
<%--        </div>--%>
<%--        <div id="friends-wrapper-container">--%>
<%--            <table id="friends-wrapper">--%>
<%--                <tr ng-click="startChat(friend.friendId)"  ng-repeat="friend in friends" class="friend-wrapper">--%>
<%--                    <td><a href="javascript:" ng-class="" class="lightf chat-session-launcher">{{friend.name}}</a></td>--%>
<%--                    <td>--%>
<%--                        <img ng-src="{{friend.imageUri}}" class="chat-session-launcher" data-id="{{friend.friendId}}"/>--%>
<%--                        <span class="online-indicator" ng-class="{'online' : friend.isOnline}"></span>--%>
<%--                    </td>--%>
<%--                </tr>--%>
<%--            </table>--%>
<%--        </div>--%>
<%--    </div>--%>


<%--    <div ng-if="$root.renderFooter" id="footer">--%>
<%--        <div id="footer" style="z-index:2001">--%>
<%--            <div style="text-align:center;margin-top:10px;">--%>
<%--                <a href="${pageContext.request.contextPath}/get_code" class="page-ref href-dotted" >Get Code</a>--%>
<%--            </div>--%>

<%--            <p style="text-align:center;"><a href="mailto:mail@astrophysical.love" style="color:#17161b" class="href-dotted">support@amadeus.social</a>--%>
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

    var t = new Date().getTime()

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

            if($location.path().includes('/profile')){
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
        $routeProvider
            .when('/', {
                templateUrl: 'pages/leafs/activity.html?v=' + t,
                controller: 'activityController'
            })
            .when('/profile/:id', {
                templateUrl: 'pages/leafs/profile.html?v=' + t,
                controller: 'profileController'
            })
            .when('/search/:q', {
                templateUrl: 'pages/leafs/search.html?v=' + t,
                controller: 'searchController'
            })
            .when('/search', {
                templateUrl: 'pages/leafs/search.html?v=' + t,
                controller: 'searchController'
            })
            .when('/folio/:id', {
                templateUrl: 'pages/leafs/folio.html?v=' + t,
                controller: 'folioController'
            })
            .otherwise({redirectTo:'/'});
    });

    app.controller('baseController', function($http, $rootScope, $route, $scope, $interval, $timeout, $location, $anchorScroll, $window, activityModel, dataService) {

        $scope.showProfile = false
        $scope.showNotifications = false
        $scope.showMobileNotifications = false
        $scope.showBaseNavigation = false

        $scope.chatInput = document.querySelector("#chat-input")
        $scope.chatSession = document.querySelector("#chat-session")

        $scope.navigateSearch = function($event){
            if($event.key == "Enter") {
                var q = document.querySelector("#search-box").value
                $window.location.href = '#!/search/' + q
            }
        }

        $scope.init = function(){
            $scope.headerPoll = $interval(getBaseData, 2300)
        }

        var getBaseData = function(){
            $rootScope.gettingData = true
            $http.get("/o/profile/data").then(function(data){
                if(data.error)$window.location.href= "/"
                $scope.data = data.data
                $rootScope.gettingData = false
            })
        }

        $scope.toggleProfile = function(){
            $scope.showProfile = !$scope.showProfile
        }

        $scope.toggleNotifications = function(){
            $scope.showNotifications = !$scope.showNotifications
        }

        $scope.toggleMobileNotifications = function(){
            $scope.showMobileNotifications = !$scope.showMobileNotifications
        }


        $scope.toggleBaseNavigation = function(){
            $scope.showBaseNavigation = !$scope.showBaseNavigation
        }

        $scope.reloadActivities = function(){
            if($location.path().includes('/search') ||
                $location.path().includes('profile') ||
                    $location.path().includes('folio')) {
                $location.path("/")
            }else{
                $window.location.reload()
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
                }).then(function () {
                    dataService.getChatMessages($scope.friendId, function (response) {
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

        $scope.clearNotifications = function(){
            $rootScope.renderModal = true
            $http.delete('/o/notifications/clear').then(function(response){
                $rootScope.renderModal = false
                $route.reload()
            }).catch(function(error){
                $rootScope.renderModal = false
            })
        }

        var ids = [
            'logo-logo',
            'amadeus-logo',
            'mobile-notifications',
            'profile-ref-image',
            'notifications-href',
            'friends-launcher'
        ]

        $scope.closeDialogs = function(event) {
            var $target = $(event.target)
            var id = $target.attr('id')
            if (!ids.includes(id)){
                $scope.chatOpened = false
                $scope.showProfile = false
                $scope.showNotifications = false
                $scope.showBaseNavigation = false
                $scope.showMobileNotifications = false;
            }
        }

        $scope.unfocusSearch = function($event){
            // $event.target.value = ''
            $($event.target).attr('placeholder', '...')
        }

        $scope.focusSearch = function($event){
            $($event.target).attr('placeholder', 'Search:')
        }

        $('body').click($scope.closeDialogs)

        $scope.toggleSearch = function(){
            $location.path("/")
            $rootScope.renderSearch();
        }

        $(window).resize(function(event){
            $scope.resizeFrames();
        })

        $scope.resizeFrames = function(){
            var frames = $('.feed-content-container iframe')
            var width = $(window).width()
            if (width < 690){
                $(frames).width(width - 30)
            }else{
                $(frames).width(465)
                $('#mobile-post-container').css('bottom', '-471px')
                $('#mobile-post-modal').hide()
            }
            $(frames).css('margin-top', '-6px')
        }


        $scope.shareWhatsupMobile = function(){
            $rootScope.renderModal = true

            var whatsup = document.querySelector('#mobile-whatsup')
            var images = document.querySelector("#mobile-post-upload-image-files").files
            var videos = document.querySelector("#mobile-post-upload-video-files").files

            if(whatsup.value == '' &&
                images.length == 0 &&
                videos.length == 0){
                alert('Express yourself!')
                return false;
            }


            var fd = new FormData();
            angular.forEach(images, function(file){
                fd.append('imageFiles',file);
            });

            angular.forEach(videos, function(file){
                fd.append('videoFile',file);
            });

            fd.append('content', whatsup.value)

            if(!$rootScope.gettingData) {
                $http({
                    method: 'post',
                    url: '/o/post/save',
                    data: fd,
                    headers: {'Content-Type': undefined},
                }).then(function (response) {
                    response.data.published = false
                    $rootScope.renderModal = false
                    $scope.mediaSelected = false
                    document.querySelector('#mobile-whatsup').value = ''
                    document.querySelector("#mobile-post-upload-image-files").value = ''
                    document.querySelector("#mobile-post-upload-video-files").value = ''
                    $scope.hideMobilePost()
                    alert("Successfully submitted new post.")
                    $route.reload()
                })
            }
        }

        $scope.openMobilePost = function(){
            $('#mobile-post-container').animate({
                bottom:"-40px"
            }, 100)
            $('#mobile-post-modal').show()
        }

        $scope.hideMobilePost = function(){
            $('#mobile-post-container').animate({
                bottom:"-503px"
            }, 200)
            $('#mobile-post-modal').hide()
        }

        $scope.resizeFrames();

    });

    app.controller('folioController', function($scope, $http, $route, $window, dataService){

        $scope.getData = function(id){
            $http.get('/o/sheet/' + id).then($scope.setData)
        }

        $scope.setData = function(resp){
            $scope.sheet = resp.data
        }

        $scope.getData($route.current.params.id)

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

            fd.append('content', whatsup.value)

            if(!$rootScope.gettingData) {
                $http({
                    method: 'post',
                    url: '/o/post/save',
                    data: fd,
                    headers: {'Content-Type': undefined},
                }).then(function (response) {
                    response.data.published = false
                    $scope.activities.unshift(response.data)
                    $rootScope.renderModal = false
                    $scope.mediaSelected = false
                    document.querySelector('#whatsup').value = ''
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

        $scope.onMediaSelected = function($evt){
            $scope.mediaSelected = $scope.mediaSelected ? false : true
        }

        getData()

        document.querySelector("#whatsup").focus()

    })

    app.controller('searchController', function($scope, $rootScope, $http, $location, $route, $window) {
        $scope.searchData = function(funkt){
            var q = $route.current.params.q

            $http.get('/o/search?q=' + q).then(function(resp){
                $scope.accounts = resp.data.accounts
                $scope.sheets = resp.data.sheets
                document.querySelector('#search-box').value = q

                funkt.call()
            })
        }

        $scope.observe = function(id){
            $http.post('/o/observe/' + id).then($route.reload)
        }

        $scope.unobserve = function(id){
            $http.post('/o/unobserve/' + id).then($route.reload)
        }

        $scope.toggleSearch = function(evt) {
            $scope.searchData($scope.transitionPage(evt));
        }

        $scope.transitionPage = function (evt) {
            return function() {
                var id = $(evt.target).attr('data-id')
                $('.toggle-search').removeClass('active')
                $(evt.target).addClass('active')
                $('.search-page').fadeOut(0, $.noop)
                $('#' + id).fadeIn(200, $.noop)
            }
        }

        $('.toggle-search').click($scope.toggleSearch)

        $scope.searchData($.noop)
    });

    app.controller('getController', function($scope) {
        $scope.pageClass = 'page-contact';
    });

    app.controller('mixController', function($scope, $rootScope, $sce, $route, $http, $location, $timeout, $window, $anchorScroll, activityModel, dataService){

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
            $timeout(function(){
                var fd = new FormData();
                var content = document.querySelector("#post-content-" + id).innerHTML

                fd.append('content', content);
                $http({
                    method: 'post',
                    url: '/o/post/update/' + id,
                    data: fd,
                    headers: {'Content-Type': undefined},
                }).then(function (response) {
                    $rootScope.renderModal = false
                    $scope.maintainView($scope.memory, response.data.id)
                    $scope.maintainView($scope.activities, response.data.id)
                    alert("Successfully updated post!")
                })
            }, 201)

        }

        $scope.maintainView = function(list, id){
            angular.forEach(list, function(activity){
                if(activity.id == id) activity.published = true
            })
        }
    });




    app.controller('profileController', function($scope, $rootScope, $http, $route, $timeout, dataService) {
        var self = this

        $scope.observe = function(id){
            $http.post('/o/observe/' + id).then($route.reload)
        }

        $scope.unobserve = function(id){
            $http.post('/o/unobserve/' + id).then($route.reload)
        }

        $scope.toggleBlock = function(id){
            $http.post('/o/account/block/' + id).then(function(){
                $scope.personBlocked = $scope.personBlocked ? false : true
            })
        }

        $scope.getData = function(id) {
            self.id = id;
            dataService.getBaseProfile(id, setData)
        }

        var setData = function(response){
            $scope.personBlocked = response.data.profile.blocked
            $scope.profile = response.data.profile
            $scope.observing = response.data.observing

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

        $scope.getData($route.current.params.id)
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
                var data = getHrefData(comment)
                var hyperlinked = anchorme(data)
                comment.innerHTML = hyperlinked
            })
        }

        var getHrefData = function(comment){
            return {
                input: comment.innerHTML,
                options: {
                    attributes: {
                        target: "_blank",
                        class: "href-dotted"
                    },
                }
            }
        }


        var motives = [
            'Take these broken wings\n' +
            'And learn to fly again\n' +
            'And learn to live so free\n' +
            'When we hear the voices sing\n' +
            'The book of love will open up\n' +
            'And let us in'
        ]



</script>
</body>
