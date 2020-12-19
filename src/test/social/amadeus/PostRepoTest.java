package social.amadeus;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import social.amadeus.common.Constants;
import social.amadeus.common.Utils;
import social.amadeus.model.*;
import social.amadeus.repository.AccountRepo;
import social.amadeus.repository.PostRepo;

import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:o-combined-test.xml")
public class PostRepoTest {

    private static final Logger log = Logger.getLogger(PostRepoTest.class);

    @Autowired
    private Utils utils;

    @Autowired
    private AccountRepo accountRepo;

    @Autowired
    private PostRepo postRepo;

    Account authdAccount;

    @Before
    public void before(){
        this.authdAccount = accountRepo.findByUsername(Constants.ADMIN_USERNAME);
    }

    @Test
    public void testSavePost(){
        Post postPre = PostMock.mock(authdAccount, utils.getCurrentDate());
        Post post = postRepo.save(postPre);
        List<Post> posts = postRepo.getPosts(authdAccount);
        assertEquals(1, posts.size());
        postRepo.delete(post.getId());
    }


    @Test
    public void testPostLike(){
        Account guestAccount = accountRepo.findByUsername(Constants.GUEST_USERNAME);
        Post postPre = PostMock.mock(authdAccount, utils.getCurrentDate());
        Post post = postRepo.save(postPre);

        PostLike postLike = new PostLike();
        postLike.setAccountId(guestAccount.getId());
        postLike.setPostId(post.getId());
        postLike.setDateLiked(utils.getCurrentDate());

        postRepo.like(postLike);
        assertEquals(1, postRepo.likes(post.getId()));

        postRepo.unlike(postLike);
        assertEquals(0, postRepo.likes(post.getId()));
        postRepo.delete(post.getId());
    }

}
