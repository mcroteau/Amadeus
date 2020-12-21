package social.amadeus;

import social.amadeus.common.Utils;
import social.amadeus.model.*;

public class PostMock {

    public static Post mock(Account account, long date){
        Post post = new Post();
        post.setAccountId(account.getId());
        post.setContent("It was the best of times, it was the worst of times, it was the age of wisdom.");
        post.setDatePosted(date);
        post.setUpdateDate(date);
        return post;
    }

    public static PostShare mockShare(Account acc, Post post, long date){
        PostShare postShare = new PostShare();
        postShare.setPostId(post.getId());
        postShare.setAccountId(acc.getId());
        postShare.setDateShared(date);
        postShare.setComment(".");
        return postShare;
    }

    public static PostComment mockComment(Account acc, Post post){
        PostComment postComment = new PostComment();
        postComment.setAccountId(acc.getId());
        postComment.setPostId(post.getId());
        postComment.setComment(".");
        postComment.setDateCreated(Utils.getDate());
        return postComment;
    }


    public static PostShareComment mockShareComment(Account acc, PostShare postShare){
        PostShareComment postShareComment = new PostShareComment();
        postShareComment.setAccountId(acc.getId());
        postShareComment.setPostShareId(postShare.getId());
        postShareComment.setComment(".");
        postShareComment.setDateCreated(Utils.getDate());
        return postShareComment;
    }

}
