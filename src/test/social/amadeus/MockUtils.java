package social.amadeus;

import io.github.mcroteau.resources.filters.CacheFilter;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import social.amadeus.common.Utils;
import social.amadeus.model.*;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MockUtils {

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

    public static CacheFilter mockRequestCycle(){
        CacheFilter filter = new CacheFilter();
        try {
            HttpServletRequest req = new MockHttpServletRequest();
            HttpServletResponse resp = new MockHttpServletResponse();

            FilterChain filterChain = Mockito.mock(FilterChain.class);
            FilterConfig config = Mockito.mock(FilterConfig.class);

            filter.init(config);
            filter.doFilter(req, resp, filterChain);
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return filter;
    }


}
