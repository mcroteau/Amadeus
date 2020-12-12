package social.amadeus;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import social.amadeus.common.Constants;
import social.amadeus.common.Utilities;
import social.amadeus.dao.AccountDao;
import social.amadeus.dao.PostDao;
import social.amadeus.model.Account;
import social.amadeus.model.Post;
import social.amadeus.service.PostService;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:o-combined-test.xml")
public class BaseActivityTest {

    private static final Logger log = Logger.getLogger(BaseActivityTest.class);

    @Autowired
    private Utilities utilities;

    @Autowired
    private PostDao postDao;

    @Autowired
    private AccountDao accountDao;

    @Autowired
    private PostService postService;

    private Post savedPostUno;
    private Post savedPostDos;
    private Post savedPostTres;

    @Before
    public void before(){
        Account account = accountDao.findByUsername(Constants.ADMIN_USERNAME);
        Post postUno = TestUtils.getPost(account.getId(), utilities.getPreviousDay(3));
        Post postDos = TestUtils.getPost(account.getId(), utilities.getPreviousDay(21));
        Post postTres = TestUtils.getPost(account.getId(), utilities.getPreviousDay(10));

        savedPostUno = postDao.save(postUno);
        postDao.publish(savedPostUno.getId());//uno

        savedPostDos = postDao.save(postDos);

        savedPostTres = postDao.save(postTres);
        postDao.publish(savedPostTres.getId());//dos
    }

    @Test
    public void testBaseActivity(){
        List<Post> activities = getActivities();
        assertEquals(2, activities.size());
    }

    @Test
    public void testHidePost(){
        postDao.hide(savedPostUno.getId());
        List<Post> activities = getActivities();
        assertEquals(1, activities.size());
    }

    @Test
    public void testFlagPost(){
        savedPostUno.setFlagged(true);
        postDao.updateFlagged(savedPostUno);
        List<Post> activities = getActivities();
        assertEquals(1, activities.size());
    }

    @Test
    public void testDeletePost(){
        postDao.delete(savedPostUno.getId());
        List<Post> activities = getActivities();
        assertEquals(1, activities.size());
    }

    @Test
    public void testGetUserPosts(){
        Account account = accountDao.findByUsername(Constants.ADMIN_USERNAME);
        List<Post> userPosts = postDao.getUserPosts(account.getId());
        assertEquals(3, userPosts.size());
    }

    @Test
    public void testGetUserPostsOneHidden(){
        postDao.hide(savedPostUno.getId());
        Account account = accountDao.findByUsername(Constants.ADMIN_USERNAME);
        List<Post> userPosts = postDao.getUserPosts(account.getId());
        assertEquals(2, userPosts.size());
    }

    @Test
    public void testGetUserPostsOneFlagged(){
        savedPostUno.setFlagged(true);
        postDao.updateFlagged(savedPostUno);
        Account account = accountDao.findByUsername(Constants.ADMIN_USERNAME);
        List<Post> userPosts = postDao.getUserPosts(account.getId());
        assertEquals(2, userPosts.size());
    }

    @Test
    public void testGetUserPostsOneDeleted(){
        postDao.delete(savedPostUno.getId());
        Account account = accountDao.findByUsername(Constants.ADMIN_USERNAME);
        List<Post> userPosts = postDao.getUserPosts(account.getId());
        assertEquals(2, userPosts.size());
    }

    private List<Post> getActivities(){
        Account account = accountDao.findByUsername(Constants.ADMIN_USERNAME);
        HttpServletRequest req =  new MockHttpServletRequest();
        Map<String, Object> activityData = postService.getActivity(account, req);
        List<Post> activities = (ArrayList) activityData.get("activities");
        return activities;
    }


    @After
    public void after(){
        postDao.delete(savedPostUno.getId());
        postDao.delete(savedPostDos.getId());
        postDao.delete(savedPostTres.getId());
    }
}
