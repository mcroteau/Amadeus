package social.amadeus;

import io.github.mcroteau.resources.filters.CacheFilter;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import social.amadeus.common.Constants;
import social.amadeus.common.Utils;
import social.amadeus.mocks.MockPost;
import social.amadeus.mocks.MockPostComment;
import social.amadeus.mocks.MockPostShare;
import social.amadeus.mocks.MockPostShareComment;
import social.amadeus.model.*;
import social.amadeus.repository.AccountRepo;
import social.amadeus.repository.NotificationRepo;
import social.amadeus.repository.PostRepo;
import social.amadeus.service.AuthService;
import social.amadeus.service.PostService;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.junit.Assert.assertTrue;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:o-combined-test.xml")
public class PostPermissionsTest {

    private static final Logger log = Logger.getLogger(PostPermissionsTest.class);

    @Autowired
    private PostRepo postRepo;

    @Autowired
    private AccountRepo accountRepo;

    @Autowired
    private NotificationRepo notificationRepo;

    @Autowired
    private AuthService authService;

    @Autowired
    private PostService postService;



    CacheFilter filter = new CacheFilter();

    Post savedPost;


    @Before
    public void before(){
        TestUtils.mockRequestCycle();

        Account adminAccount = accountRepo.getByUsername(Constants.ADMIN_USERNAME);
        Post post = new MockPost(adminAccount, Utils.getDate());

        authService.signin(Constants.ADMIN_USERNAME, Constants.PASSWORD);
        savedPost = postService.savePost(post, null, null);

        TestUtils.mockRequestCycle();
        authService.signin(Constants.GUEST_USERNAME, Constants.GUEST_PASSWORD);
    }


    @Test
    public void testPublishWithWrongUser(){
        String result = postService.publishPost(Long.toString(savedPost.getId()));
        log.info("testPublishWithWrongUser : " + result);
        assertTrue(result.equals(Constants.REQUIRES_PERMISSION));

    }

    @Test
    public void testUpdateWithWrongUser(){
        savedPost = postService.updatePost(Long.toString(savedPost.getId()), savedPost);
        assertTrue(savedPost.getStatusMessage().equals(Constants.REQUIRES_PERMISSION));
    }

    @Test
    public void testDeleteWithWrongUser(){
        String result = postService.deletePost(Long.toString(savedPost.getId()));
        assertTrue(result.equals(Constants.REQUIRES_PERMISSION));
    }

    @Test
    public void testAddImageWithWrongUser(){
        String result = postService.addPostImages(Long.toString(savedPost.getId()), null);
        assertTrue(result.equals(Constants.REQUIRES_PERMISSION));
    }


    @Test
    public void testAddCommentWithEmptyComment(){
        Account adminAccount = accountRepo.getByUsername(Constants.ADMIN_USERNAME);
        PostComment postComment = new PostComment();
        postComment.setAccountId(adminAccount.getId());
        postComment.setPostId(savedPost.getId());
        String result = postService.savePostComment(Long.toString(savedPost.getId()), postComment);
        assertTrue(result.equals(Constants.X_MESSAGE));
    }


    @Test
    public void testDeleteCommentWithWrongUser(){
        Account adminAcc = accountRepo.getByUsername(Constants.ADMIN_USERNAME);
        PostComment postComment = new MockPostComment(adminAcc, savedPost);
        postService.savePostComment(Long.toString(savedPost.getId()), postComment);
        String result = postService.deletePostComment(Long.toString(0));
        assertTrue(result.equals(Constants.REQUIRES_PERMISSION));
    }


    @Test
    public void testUnshareWithWrongUser(){
        Account guestAcc = accountRepo.getByUsername(Constants.GUEST_USERNAME);
        PostShare postShare = new MockPostShare(guestAcc, savedPost, Utils.getDate());
        postService.sharePost(Long.toString(savedPost.getId()), postShare);

        TestUtils.mockRequestCycle();
        authService.signin(Constants.ADMIN_USERNAME, Constants.PASSWORD);
        String result = postService.unsharePost(Long.toString(postRepo.getPostShareId()));
        assertTrue(result.equals(Constants.REQUIRES_PERMISSION));
    }

    @Test
    public void testDeleteShareCommentWithWrongUser(){
        Account guestAcc = accountRepo.getByUsername(Constants.GUEST_USERNAME);
        PostShare postShare = new MockPostShare(guestAcc, savedPost, Utils.getDate());
        postService.sharePost(Long.toString(savedPost.getId()), postShare);

        PostShare savedPostShare = postRepo.getPostShare(postRepo.getPostShareId());

        Account adminAcc = accountRepo.getByUsername(Constants.ADMIN_USERNAME);
        TestUtils.mockRequestCycle();
        authService.signin(Constants.ADMIN_USERNAME, Constants.PASSWORD);

        PostShareComment postShareComment = new MockPostShareComment(adminAcc, savedPostShare);
        postService.savePostShareComment(Long.toString(postRepo.getPostShareId()), postShareComment);

        log.info(postRepo.getPostShareCommentId());

        TestUtils.mockRequestCycle();
        authService.signin(Constants.GUEST_USERNAME, Constants.GUEST_PASSWORD);

        String result = postService.deletePostShareComment(Long.toString(postRepo.getPostShareCommentId()));
        log.info(result);
        assertTrue(result.equals(Constants.REQUIRES_PERMISSION));

        postRepo.deletePostShareComments(postRepo.getPostShareId());
        postRepo.deletePostShare(postRepo.getPostShareId());
    }

    @After
    public void after(){
        Account adminAcc = accountRepo.getByUsername(Constants.ADMIN_USERNAME);
        Account guestAcc = accountRepo.getByUsername(Constants.GUEST_USERNAME);

        notificationRepo.clearNotifications(guestAcc.getId());
        notificationRepo.clearNotifications(adminAcc.getId());

        if(postRepo.getPostShareCommentId() != null)
            postService.deletePostShareComment(Long.toString(postRepo.getPostShareCommentId()));

        if(postRepo.getPostShareId() != null)
            postRepo.deletePostShare(postRepo.getPostShareId());

        postRepo.deletePostComments(savedPost.getId());
        postRepo.delete(savedPost.getId());
    }

}
