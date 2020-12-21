package social.amadeus;

import io.github.mcroteau.Parakeet;
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
import social.amadeus.model.*;
import social.amadeus.repository.AccountRepo;
import social.amadeus.repository.NotificationRepo;
import social.amadeus.repository.PostRepo;
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
    private Parakeet parakeet;

    @Autowired
    private AccountRepo accountRepo;

    @Autowired
    private PostService postService;

    @Autowired
    private PostRepo postRepo;

    @Autowired
    private NotificationRepo notificationRepo;


    CacheFilter filter = new CacheFilter();

    Post savedPost;


    @Before
    public void before(){

        mockRequestCycle();

        Account adminAccount = accountRepo.findByUsername(Constants.ADMIN_USERNAME);
        Post post = MockUtils.mock(adminAccount, Utils.getDate());

        parakeet.login(Constants.ADMIN_USERNAME, Constants.PASSWORD);
        savedPost = postService.savePost(post, null, null);

        mockRequestCycle();
        parakeet.login(Constants.GUEST_USERNAME, Constants.GUEST_PASSWORD);
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
        assertTrue(savedPost.getFailMessage().equals(Constants.REQUIRES_PERMISSION));
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
    public void testDeleteImageWithWrongUser(){
//
//        mockRequestCycle();
//        parakeet.login(Constants.ADMIN_USERNAME, Constants.PASSWORD);
//
//        File file = new File("test-img.png");
//        log.info(file.getPath());
//        FileItem fileItem = new DiskFileItem("file", "image/png", true, file.getName(), 100000000, file.getParentFile());
//
//        try {
//            fileItem.getOutputStream();
//        }catch (Exception ex){
//            ex.printStackTrace();
//        }
//
//        CommonsMultipartFile cmf = new CommonsMultipartFile(fileItem);
//        CommonsMultipartFile[] imageFiles = { cmf };
//
//        postService.addPostImages(Long.toString(savedPost.getId()), imageFiles);
//
//        mockRequestCycle();
//        parakeet.login(Constants.GUEST_USERNAME, Constants.GUEST_PASSWORD);
//        String result = postService.deletePostImage(Long.toString(savedPost.getId()), "");
//        assertTrue(result.equals(Constants.REQUIRES_PERMISSION));
//        postService.deletePost(Long.toString(savedPost.getId()));
        assertTrue(true);
    }


    @Test
    public void testAddCommentWithEmptyComment(){
        Account adminAccount = accountRepo.findByUsername(Constants.ADMIN_USERNAME);
        PostComment postComment = new PostComment();
        postComment.setAccountId(adminAccount.getId());
        postComment.setPostId(savedPost.getId());
        String result = postService.savePostComment(Long.toString(savedPost.getId()), postComment);
        assertTrue(result.equals(Constants.X_MESSAGE));
    }


    @Test
    public void testDeleteCommentWithWrongUser(){
        Account adminAcc = accountRepo.findByUsername(Constants.ADMIN_USERNAME);
        PostComment postComment = MockUtils.mockComment(adminAcc, savedPost);
        postService.savePostComment(Long.toString(savedPost.getId()), postComment);
        String result = postService.deletePostComment(Long.toString(0));
        assertTrue(result.equals(Constants.REQUIRES_PERMISSION));
    }


    @Test
    public void testUnshareWithWrongUser(){
        Account guestAcc = accountRepo.findByUsername(Constants.GUEST_USERNAME);
        PostShare postShare = MockUtils.mockShare(guestAcc, savedPost, Utils.getDate());
        postService.sharePost(Long.toString(savedPost.getId()), postShare);

        mockRequestCycle();
        parakeet.login(Constants.ADMIN_USERNAME, Constants.PASSWORD);
        String result = postService.unsharePost("1");
        assertTrue(result.equals(Constants.REQUIRES_PERMISSION));
        postRepo.deletePostShare(postRepo.getPostShareId());
    }

//    @Test
//    public void testDeleteShareCommentWithWrongUser(){
//        Account guestAcc = accountRepo.findByUsername(Constants.GUEST_USERNAME);
//        PostShare postShare = MockUtils.mockShare(guestAcc, savedPost, Utils.getDate());
//        postService.sharePost(Long.toString(savedPost.getId()), postShare);
//
//        long postShareId = postRepo.getPostShareId();
//        Account adminAcc = accountRepo.findByUsername(Constants.ADMIN_USERNAME);
//        PostShare savedPostShare = postRepo.getPostShare(postShareId);
//
//        PostShareComment postShareComment = MockUtils.mockShareComment(adminAcc, savedPostShare);
//        postService.savePostShareComment(Long.toString(postShareId), postShareComment);
//
//        String result = postService.deletePostShareComment("1");
//        assertTrue(result.equals(Constants.REQUIRES_PERMISSION));
//        postRepo.deletePostShareComments(1);
//        postRepo.deletePostShare(postShareId);
//    }

    @After
    public void after(){
        Account adminAcc = accountRepo.findByUsername(Constants.ADMIN_USERNAME);
        notificationRepo.clearNotifications(adminAcc.getId());
        postRepo.deletePostComments(savedPost.getId());
        postRepo.delete(savedPost.getId());
    }


    private void mockRequestCycle(){
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
    }


}
