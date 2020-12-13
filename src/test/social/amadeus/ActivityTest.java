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
import social.amadeus.common.Utilities;
import social.amadeus.repository.AccountRepo;
import social.amadeus.repository.PostRepo;
import social.amadeus.model.Account;
import social.amadeus.model.Post;
import social.amadeus.model.PostShare;
import social.amadeus.service.PostService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:o-combined-test.xml")
public class ActivityTest {

    private static final Logger log = Logger.getLogger(ActivityTest.class);

    @Autowired
    private Utilities utilities;

    @Autowired
    private PostRepo postRepo;

    @Autowired
    private AccountRepo accountRepo;

    @Autowired
    private PostService postService;

    Post savedPostUno;
    Post savedPostDos;
    Post savedPostTres;
    Post savedPostQuatro;
    PostShare savedPostShare;

    Account authdAccount;

    @Before
    public void before(){
        authdAccount = accountRepo.findByUsername(Constants.ADMIN_USERNAME);
        Post postUno = TestUtils.getPost(authdAccount.getId(), utilities.getPreviousDay(3));
        Post postDos = TestUtils.getPost(authdAccount.getId(), utilities.getPreviousDay(1));
        Post postTres = TestUtils.getPost(authdAccount.getId(), utilities.getPreviousDay(21));
        Post postQuatro = TestUtils.getPost(authdAccount.getId(), utilities.getPreviousDay(9));

        savedPostUno = postRepo.save(postUno);
        postRepo.publish(savedPostUno.getId());//uno

        savedPostDos = postRepo.save(postDos);
        postRepo.publish(savedPostDos.getId());//dos

        savedPostTres = postRepo.save(postTres);
        postRepo.publish(savedPostTres.getId());

        savedPostQuatro = postRepo.save(postQuatro);
    }

    @Test
    public void testGetActivity(){
        List<Post> activities = getActivities();
        assertEquals(2, activities.size());
    }

    @Test
    public void testGetActivityHidePost(){
        postRepo.hide(savedPostUno.getId());
        List<Post> activities = getActivities();
        assertEquals(1, activities.size());
    }

    @Test
    public void testGetActivityFlagPost(){
        savedPostUno.setFlagged(true);
        postRepo.updateFlagged(savedPostUno);
        List<Post> activities = getActivities();
        assertEquals(1, activities.size());
    }

    @Test
    public void testGetActivityDeletePost(){
        postRepo.delete(savedPostUno.getId());
        List<Post> activities = getActivities();
        assertEquals(1, activities.size());
    }

    @Test
    public void testGetUserActivity(){
        List<Post> userActivity = postService.getUserActivity(authdAccount, authdAccount);
        assertEquals(4, userActivity.size());
    }

    @Test
    public void testGetUserActivityOneHidden(){
        postRepo.hide(savedPostUno.getId());
        List<Post> userActivity = postService.getUserActivity(authdAccount, authdAccount);
        assertEquals(3, userActivity.size());
    }

    @Test
    public void testGetUserActivityOneFlagged(){
        savedPostUno.setFlagged(true);
        postRepo.updateFlagged(savedPostUno);
        List<Post> userActivity = postService.getUserActivity(authdAccount, authdAccount);
        assertEquals(3, userActivity.size());
    }

    @Test
    public void testGetUserActivityOneDeleted(){
        postRepo.delete(savedPostUno.getId());
        List<Post> userActivity = postService.getUserActivity(authdAccount, authdAccount);
        assertEquals(3, userActivity.size());
    }


    @Test
    public void testSharePost(){
        PostShare postShare = new PostShare();
        postShare.setAccountId(authdAccount.getId());
        postShare.setComment("Mock");
        postShare.setPostId(savedPostUno.getId());
        postShare.setDateShared(utilities.getCurrentDate());
        savedPostShare = postRepo.sharePost(postShare);
        List<Post> activity = getActivities();
        assertEquals(3, activity.size());
    }


    private List<Post> getActivities(){
        Account mockAuthdAccount = accountRepo.findByUsername(Constants.ADMIN_USERNAME);
        Map<String, Object> activityData = postService.getActivity(mockAuthdAccount);
        List<Post> activities = (ArrayList) activityData.get("activities");
        return activities;
    }


    @After
    public void after(){
        if(savedPostShare != null)
            postRepo.deletePostShare(savedPostShare.getId());
        postRepo.delete(savedPostUno.getId());
        postRepo.delete(savedPostDos.getId());
        postRepo.delete(savedPostTres.getId());
        postRepo.delete(savedPostQuatro.getId());
    }
}
