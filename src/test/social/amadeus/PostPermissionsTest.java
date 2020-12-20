package social.amadeus;

import io.github.mcroteau.Parakeet;
import io.github.mcroteau.resources.filters.CacheFilter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import social.amadeus.common.Constants;
import social.amadeus.common.Utils;
import social.amadeus.model.Account;
import social.amadeus.model.Post;
import social.amadeus.repository.AccountRepo;
import social.amadeus.repository.PostRepo;
import social.amadeus.service.PostService;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:o-combined-test.xml")
public class PostPermissionsTest {

    @Autowired
    private Utils utils;

    @Autowired
    private Parakeet parakeet;

    @Autowired
    private AccountRepo accountRepo;

    @Autowired
    private PostService postService;

    @Autowired
    private PostRepo postRepo;

    CacheFilter filter = new CacheFilter();

    Post savedPost;

    /**
     * create post
     * try to publish post with wrong user
     * try to update with wrong user
     * try to delete with wrong user
     * try to add images with wrong user
     * share post
     * try to unshare post with wrong user
     */
    //-> drop created entities <-//
    @Before
    public void before() throws Exception{

        mockRequestCycle();
        parakeet.login(Constants.ADMIN_USERNAME, Constants.ADMIN_USERNAME);
        Account adminAccount = accountRepo.findByUsername(Constants.ADMIN_USERNAME);

        Post post = PostMock.mock(adminAccount, utils.getCurrentDate());
        savedPost = postService.savePost(post, null, null);

        mockRequestCycle();
        parakeet.login(Constants.GUEST_USERNAME, Constants.GUEST_PASSWORD);
    }


    @Test
    public void testPublishWithWrongUser(){
        String result = postService.publishPost(Long.toString(savedPost.getId()));
        assertTrue(result.equals(Constants.REQUIRES_PERMISSION));
        postRepo.delete(savedPost.getId());
    }

    @Test
    public void testUpdateWithWrongUser(){
        savedPost = postService.updatePost(Long.toString(savedPost.getId()), savedPost);
        assertTrue(savedPost.getFailMessage().equals(Constants.REQUIRES_PERMISSION));
        postRepo.delete(savedPost.getId());
    }

    @Test
    public void testDeleteWithWrongUser(){
        String result = postService.deletePost(Long.toString(savedPost.getId()));
        assertTrue(result.equals(Constants.REQUIRES_PERMISSION));
        postRepo.delete(savedPost.getId());
    }

    @Test
    public void testAddImageWithWrongUser(){
        String result = postService.addPostImages(Long.toString(savedPost.getId()), null);
        assertTrue(result.equals(Constants.REQUIRES_PERMISSION));
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
