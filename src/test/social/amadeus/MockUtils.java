package social.amadeus;

import org.apache.log4j.Logger;
import social.amadeus.model.Post;

public class MockUtils {

    private static final Logger log = Logger.getLogger(MockUtils.class);

    public static Post getPost(long accountId, long date){
        Post post = new Post();
        post.setAccountId(accountId);
        post.setContent("The Lazy Fox jumped over the yellow dog.");
        post.setDatePosted(date);
        post.setUpdateDate(date);
        post.setHidden(false);
        return post;
    }
}
