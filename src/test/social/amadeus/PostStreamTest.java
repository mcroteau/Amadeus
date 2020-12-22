package social.amadeus;

import io.github.mcroteau.Parakeet;
import org.apache.log4j.Logger;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import social.amadeus.common.Constants;
import social.amadeus.common.Utils;
import social.amadeus.mocks.MockPost;
import social.amadeus.mocks.MockPostLike;
import social.amadeus.model.Account;
import social.amadeus.model.ActivityOutput;
import social.amadeus.model.LikesOutput;
import social.amadeus.model.Post;
import social.amadeus.repository.AccountRepo;
import social.amadeus.repository.NotificationRepo;
import social.amadeus.repository.PostRepo;
import social.amadeus.service.AuthService;
import social.amadeus.service.PostService;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:o-combined-test.xml")
public class PostStreamTest {

    private static final Logger log = Logger.getLogger(PostPermissionsTest.class);

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


    Post savedPost;

    @Before
    public void before(){
        TestUtils.mockRequestCycle();

        Account adminAcc = accountRepo.findByUsername(Constants.ADMIN_USERNAME);
        Post postPre = new MockPost(adminAcc, Utils.getDate());

        authService.signin(Constants.ADMIN_USERNAME, Constants.PASSWORD);
        savedPost = postService.savePost(postPre, null, null);
    }

    @Test
    public void testSaveWithoutPublish(){
        ActivityOutput activityOutput = postService.getActivity();
        assertEquals(0, activityOutput.getPosts().size());
    }

    @Test
    public void testSavePublished(){
        postService.publishPost(Long.toString(savedPost.getId()));
        ActivityOutput activityOutput = postService.getActivity();
        assertEquals(1, activityOutput.getPosts().size());
    }

    @Test
    public void testSaveHidden(){
        postService.publishPost(Long.toString(savedPost.getId()));
        postService.hidePost(Long.toString(savedPost.getId()));
        ActivityOutput activityOutput = postService.getActivity();
        assertEquals(0, activityOutput.getPosts().size());
    }

    @Test
    public void testSaveFlagged(){
        postService.publishPost(Long.toString(savedPost.getId()));
        postService.flagPost(Long.toString(savedPost.getId()), false);
        ActivityOutput activityOutput = postService.getActivity();
        assertEquals(0, activityOutput.getPosts().size());
    }

    @Test
    public void testSaveDeleted(){
        postService.publishPost(Long.toString(savedPost.getId()));
        postService.deletePost(Long.toString(savedPost.getId()));
        ActivityOutput activityOutput = postService.getActivity();
        assertEquals(0, activityOutput.getPosts().size());
    }


    @After
    public void after(){
        Account adminAcc = accountRepo.findByUsername(Constants.ADMIN_USERNAME);
        Account guestAcc = accountRepo.findByUsername(Constants.GUEST_USERNAME);
        notificationRepo.clearNotifications(adminAcc.getId());
        postRepo.deletePostShare(postRepo.getPostShareId());
        postRepo.unlike(new MockPostLike(adminAcc, savedPost));
        postRepo.unlike(new MockPostLike(guestAcc, savedPost));
        postRepo.deletePostFlag(savedPost.getId(), adminAcc.getId());
        postRepo.deleteHiddenPost(savedPost.getId(), adminAcc.getId());
        postRepo.delete(savedPost.getId());
    }

}
