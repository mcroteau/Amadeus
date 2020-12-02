
    <div class="feed-content-container" id="post-{{post.id}}" ng-if="!post.hidden && !post.flagged">

        <span ng-if="post.postEditable && post.content != null" class="edit-click tiny yella edit-fade-in">Click inside<br/> Post to Edit</span>
        <span ng-if="post.postEditable && post.content != null"  class="author-ref tiny">Author</span>

        <div class="shared-post-container" ng-if="post.shared">

            <a href="#!{{post.sharedAccountId}}" class="shared-post-whois" data-id="{{post.sharedAccountId}}">
                <img ng-src="/o/{{post.sharedImageUri}}" style="width:40px; height:40px;"/>
            </a>

            <div class="shared-account-container">
                <span class="shared-account">{{post.sharedAccount}}</span>
                <span class="shared-time-ago">{{post.timeSharedAgo}}</span>
            </div>

            <span class="shared-descriptor">Shared</span>

            <br class="clear"/>

            <div class="shared-comment-container">
                <span class="shared-comment">{{post.sharedComment}}</span>
            </div>
        </div>

        <div class="feed-content shared" ng-ig="post.shared">

        <div class="feed-content" ng-if="post.shared == false">

            <div class="post-content">

                    <div ng-repeat="imageUri in post.imageFileUris" class="image-post-container" data-image-uri="{{imageUri}}" style="position:relative;">
                        <a href="javascript:" ng-if="post.shared && post.deletable" class="delete-image beauty-light edit-fade-in" data-uri="{{imageUri}}" data-id="{{post.id}}">x</a>
                        <a href="/o/{{imageUri}}" target="_blank">
                            <img ng-src="/o/{{imageUri}}" style="width:100%"/>
                        </a>
                    </div>

                    <video controls ng-if="post.videoFileUri" src="/o/{{post.videoFileUri}}" style="width:100%" data-video-uri="{{post.videoFileUri}}"></video>

                    <div ng-if="post.content && post.postEditable && !post.shared" style="white-space: pre-line;" class="post-comment" contenteditable="true" id="post-content-{{post.id}}" data-id="{{post.id}}" ng-bind-html="post.content"></div>

                    <div ng-if="post.content" style="white-space: pre-line;" class="post-comment" ng-bind-html="post.content"></div>

                </div>

                <div class="post-meta" style="position: relative">
                    <p class="edit-status tiny float-right" id="edit-status-{{id}}">Updating...</p>

                    <div ng-if="post.shared && post.deletable">
                        <a href="javascript:" class="button small retro edit-update-button edit-fade-in" id="edit-update-button-{{post.id}}" data-id="{{post.id}}">Update</a>
                        <div class="image-edit edit-fade-in">
                            <form id="edit-image-form-{{post.id}}">
                                <a href="javascript:" class="button small yella" id="edit-image-button-{{post.id}}">+ Images</a>
                                <input type="file" name="imageFiles" id="edit-image-select-{{post.id}}" class="edit-image-input" multiple="multiple" data-id="{{post.id}}"/>
                            </form>
                        </div>
                    </div>

                    <a href="javascript:" class="post-whois" data-id="{{post.accountId}}">
                        <img src="${pageContext.request.contextPath}/{{post.imageUri}}"/>
                    </a>
                    <span class="posted-by" data-id="{{post.accountId}}">{{post.name}}</span><br/>
                    <span class="post-date">{{post.timeAgo}}</span>

                    <br class="clear"/>
                </div>

                <div class="content-actions">

                    <div class="like-container float-right">
                        <div class="like-actions-count-container">
                            <span class="actions-count like-container-{{post.id}}" id="likes-{{post.id}}" ng-bind="post.likes"></span> <span class="actions-label">Likes</span>
                        </div>
                        <svg ng-if="post.liked" class="thunder like-button liked like-{{post.id}}" data-id="{{post.id}}">
                            <use xlink:href="#amadeus" data-id="{{post.id}}"/>
                        </svg>
                        <svg ng-if="!post.liked" class="thunder like-button like-{{post.id}}" data-id="{{post.id}}">
                            <use xlink:href="#amadeus" data-id="{{id}}"/>
                        </svg>
                    </div>


                    <div class="share-container float-left">
                        <div ng-if="post.shared">
                            <div class="share-actions-count-container">
                                <span class="actions-count" id="shares-{{post.postShareId}}" ng-bind="post.shares"> <span class="actions-label">Shares</span></span>
                            </div>
                            <a href="javascript:" class="share-shared-button" data-id="{{post.postShareId}}" data-post-id="{{post.id}}"><img src="/o/images/share.png" style="width:31px;" data-id="{{post.id}}"/></a>
                        </div>
                        <div ng-if="!post.shared">
                            <div class="share-actions-count-container">
                                <span class="actions-count" id="shares-{{id}}">{{shares}} <span class="actions-label">Shares</span></span>
                            </div>
                            <a href="javascript:" class="share-button" data-id="{{id}}"><img src="/o/images/share.png" style="width:32px;" data-id="{{id}}"/></a>
                        </div>
                    </div>

                    <br class="clear"/>

                </div>

                <div class="post-separators">
                    <div class="post-sep post-sep-uno"></div>
                    <div class="post-sep post-sep-dos"></div>
                    <div class="post-sep post-sep-tres"></div>
                </div>

                <div ng-if="post.shared" class="share-comment-container" id="share-shared-comment-{{post.postShareId}}" style="display:none">
                <!---->
                <div ng-if="!post.shared" class="share-comment-container" id="share-comment-{{post.id}}" style="display:none">

                    <form ng-if="post.shared" id="share-shared-post-form-{{post.ostShareId}}">
                    <!---->
                    <form ng-if="!post.shared" id="share-post-form-{{post.id}}">

                        <span class="share-comment-header" style="font-family:Roboto-Light !important;color:#617078">Share Post</span>
                        <textarea name="comment"></textarea>

                        <a ng-if="post.shared" href="javascript:" class="button modern small light-shadow float-right share-post-button" data-id="{{post.postShareId}}" data-post-id="{{post.id}}" data-shared="true">Share Post</a>
                        <!---->
                        <a ng-if="!post.shared" href="javascript:" class="button modern small light-shadow float-right share-post-button" data-id="{{post.id}}" data-post-id="{{post.id}}">Share Post</a>

                        <br class="clear"/>
                    </form>
                </div>

                <div ng-if="post.commentsOrShareComments" class="comments-container" style="margin-left:62px;">

                <div ng-if="!post.commentsOrShareComments" class="comments-container" style="margin-left:62px;">

                    <div ng-if="post.commentsOrShareComments" class="comments-header-spacer"></div>

                    <div class="post-comment-wrapper" ng-if="post.comments">

                        <div class="post-comment-meta">
                            <a href="javascript:" class="post-comment-whois left-float" data-id="{{post.accountId}}">
                                <img src="/o/{{post.accountImageUri}}" style="height:30px; width:30px;"/>
                            </a>
                            <div class="left-float" style="margin-left:10px;width:79%;">
                                <p>
                                    <a href="javascript:" class="post-comment-whois" data-id="{{post.accountId}}" style="text-decoration:none; color:#222; font-family:Roboto-Bold !important">{{post.accountName}}</a>
                                    <span class="comment-comment">{{post.comment}}</span>
                                    <a ng-if="post.commentDeletable" href="javascript:" class="comment-delete href-dotted" data-id="{{post.commentId}}" style="display:none">Delete</a>
                                </p>
                            </div>
                            <br class="clear"/>
                        </div>
                    </div>

                        <div class="post-comment-wrapper" ng-if="post.sharedComments">

                            <div class="post-comment-meta">
                                <a href="javascript:" class="post-comment-whois left-float" data-id="{{post.accountId}}">
                                    <img src="/o/{{post.accountImageUri}}" style="height:30px; width:30px;"/>
                                </a>
                                <div class="left-float" style="margin-left:10px;width:79%;">
                                    <p>
                                        <a href="javascript:" class="post-comment-whois" data-id="{{post.accountId}}" style="text-decoration:none; color:#222; font-family:Roboto-Bold !important">{{post.accountName}}</a>
                                        <span class="comment-comment">{{post.comment}}</span>
                                        <a ng-if="post.commentDeletable" href="javascript:" class="comment-delete href-dotted" data-id="{{post.commentId}}" style="display:none">Delete</a>
                                    </p>
                                </div>
                                <br class="clear"/>
                            </div>
                        </div>

                        <div class="comment-container" class="clear">

                            <form ng-if="post.shared" id="comment-shared-form-{{post.postShareId}}" class="comment-shared-form" data-id="{{post.postShareId}}">
                                <span class="comments-header information">Comment</span>
                                <input type="text" name="comment" id="comment-shared-{{post.postShareId}}" class="comment-shared-input" value=""/>
                                <span class="enter" style="display:none">Enter Key</span>
                            </form>

                            <form ng-if="!post.shared" id="comment-form-{{post.id}}" class="comment-form" data-id="{{post.id}}">
                                <span class="comments-header information">Comment</span>
                                <input type="text" name="comment" id="comment-{{post.id}}" value="" class="comment-input"/>
                                <span class="enter" style="display:none">Enter Key</span>
                            </form>
                        </div>

                        <br class="clear"/>
                    </div>
               </div>

        <div class="post-admin-actions-wrapper" ng-if="post.shared">&Xi;
            <div class="post-admin-actions-inner" style="position:relative;">
                <div class="post-admin-actions">
                    <a ng-if="post.deletable"  href="javascript:" class="delete-post-share" data-id="{{post.postShareId}}">Delete Post</a>
                    <a href="javascript:" class="flag-post" data-id="{{post.id}}" data-shared="true">Flag Post</a>
                    <a href="javascript:" class="hide-post" data-id="{{post.id}}">Hide Post</a>
                </div>
            </div>
        </div>
        <div class="post-admin-actions-wrapper" ng-if="!post.shared">&Xi;
            <div class="post-admin-actions-inner" style="position:relative;">
                <div class="post-admin-actions">
                    <a ng-if="post.deletable" href="javascript:" class="delete-post" data-id="{{post.id}}">Delete Post</a>
                    <a href="javascript:" class="flag-post" data-id="{{post.id}}" data-shared="false">Flag Post</a>
                    <a href="javascript:" class="hide-post" data-id="{{post.id}}">Hide Post</a>
                </div>
            </div>
        </div>
    </div>
