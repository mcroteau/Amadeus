package social.amadeus;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import social.amadeus.common.Constants;
import social.amadeus.common.Utils;
import social.amadeus.mocks.*;
import social.amadeus.model.*;
import social.amadeus.repository.AccountRepo;
import social.amadeus.repository.NotificationRepo;
import social.amadeus.repository.PostRepo;
import social.amadeus.service.AuthService;
import social.amadeus.service.PostService;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:o-combined-test.xml")
public class PostActionTest {

    private static final Logger log = Logger.getLogger(PostPermissionsTest.class);

    Post savedPost;

    @Autowired
    private AuthService authService;

    @Autowired
    private AccountRepo accountRepo;

    @Autowired
    private PostService postService;

    @Autowired
    private PostRepo postRepo;

    @Autowired
    private NotificationRepo notificationRepo;

    @Before
    public void before(){
        TestUtils.mockRequestCycle();

        Account adminAcc = accountRepo.findByUsername(Constants.ADMIN_USERNAME);
        Post postPre = new MockPost(adminAcc, Utils.getDate());

        authService.signin(Constants.ADMIN_USERNAME, Constants.PASSWORD);
        savedPost = postService.savePost(postPre, null, null);
    }



    @Test
    public void testPublish(){
        String result = postService.publishPost(Long.toString(savedPost.getId()));
        assertEquals(1, postRepo.getCount());
    }


    @Test
    public void testUpdate(){
        savedPost.setContent(".");
        savedPost = postService.updatePost(Long.toString(savedPost.getId()), savedPost);
        Post updatedPost = postRepo.get(savedPost.getId());
        assertTrue(".".equals(updatedPost.getContent()));
    }


    @Test
    public void testDelete(){
        String result = postService.deletePost(Long.toString(savedPost.getId()));
        Post updatedPost = postRepo.get(savedPost.getId());
        assertTrue(updatedPost.isHidden());
    }


    @Test
    public void testAddComment(){
        Account adminAcc = accountRepo.findByUsername(Constants.ADMIN_USERNAME);
        PostComment postComment = new MockPostComment(adminAcc, savedPost);
        postService.savePostComment(Long.toString(savedPost.getId()), postComment);
        assertNotNull(postRepo.getPostCommentId());
        postService.deletePostComment(Long.toString(postRepo.getPostCommentId()));
    }

    @Test
    public void testDeleteComment(){
        Account adminAcc = accountRepo.findByUsername(Constants.ADMIN_USERNAME);
        PostComment postComment = new MockPostComment(adminAcc, savedPost);
        postService.savePostComment(Long.toString(savedPost.getId()), postComment);
        postService.deletePostComment(Long.toString(postRepo.getPostCommentId()));
        assertNull(postRepo.getPostCommentId());
    }


//    @Test
//    public void testUnshare(){
//        Account guestAcc = accountRepo.findByUsername(Constants.GUEST_USERNAME);
//        PostShare postShare = new MockPostShare(guestAcc, savedPost, Utils.getDate());
//        postService.sharePost(Long.toString(savedPost.getId()), postShare);
//
//        TestUtils.mockRequestCycle();
//        authService.signin(Constants.ADMIN_USERNAME, Constants.PASSWORD);
//        String result = postService.unsharePost(Long.toString(postRepo.getPostShareId()));
//        assertTrue(result.equals(Constants.REQUIRES_PERMISSION));
//    }
//
//    @Test
//    public void testDeleteShareComment(){
//        Account guestAcc = accountRepo.findByUsername(Constants.GUEST_USERNAME);
//        PostShare postShare = new MockPostShare(guestAcc, savedPost, Utils.getDate());
//        postService.sharePost(Long.toString(savedPost.getId()), postShare);
//
//        PostShare savedPostShare = postRepo.getPostShare(postRepo.getPostShareId());
//
//        Account adminAcc = accountRepo.findByUsername(Constants.ADMIN_USERNAME);
//        TestUtils.mockRequestCycle();
//        authService.signin(Constants.ADMIN_USERNAME, Constants.PASSWORD);
//
//        PostShareComment postShareComment = new MockPostShareComment(adminAcc, savedPostShare);
//        postService.savePostShareComment(Long.toString(postRepo.getPostShareId()), postShareComment);
//
//        log.info(postRepo.getPostShareCommentId());
//
//        TestUtils.mockRequestCycle();
//        authService.signin(Constants.GUEST_USERNAME, Constants.GUEST_PASSWORD);
//
//        String result = postService.deletePostShareComment(Long.toString(postRepo.getPostShareCommentId()));
//        log.info(result);
//        assertTrue(result.equals(Constants.REQUIRES_PERMISSION));
//
//        postRepo.deletePostShareComments(postRepo.getPostShareId());
//        postRepo.deletePostShare(postRepo.getPostShareId());
//    }
//
//
//    @Test
//    public void testSharePost(){
//        Account adminAcc = accountRepo.findByUsername(Constants.ADMIN_USERNAME);
//        postService.publishPost(Long.toString(savedPost.getId()));
//        postService.sharePost(Long.toString(savedPost.getId()), new MockPostShare(adminAcc, savedPost, Utils.getYesterday(7)));
//        ActivityOutput activityOutput = postService.getActivity();
//        assertEquals(3,3);
//    }



    @Test
    public void testSaveMultipleLikes(){
        postService.publishPost(Long.toString(savedPost.getId()));
        postService.likePost(Long.toString(savedPost.getId()));
        TestUtils.mockRequestCycle();
        authService.signin(Constants.GUEST_USERNAME, Constants.GUEST_PASSWORD);
        postService.likePost(Long.toString(savedPost.getId()));
        assertEquals(2, postRepo.likes(savedPost.getId()));
    }

    @After
    public void after(){
        Account adminAcc = accountRepo.findByUsername(Constants.ADMIN_USERNAME);
        Account guestAcc = accountRepo.findByUsername(Constants.GUEST_USERNAME);
        notificationRepo.clearNotifications(adminAcc.getId());
        if(postRepo.getPostShareId() != null)
            postRepo.deletePostShare(postRepo.getPostShareId());
        postRepo.unlike(new MockPostLike(adminAcc, savedPost));
        postRepo.unlike(new MockPostLike(guestAcc, savedPost));
        postRepo.delete(savedPost.getId());
    }

}
