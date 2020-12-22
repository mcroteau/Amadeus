package social.amadeus.mocks;

import social.amadeus.model.Account;
import social.amadeus.model.Post;
import social.amadeus.model.PostShare;

public class MockPostShare extends PostShare {

    public MockPostShare(Account acc, Post post, long date) {
        this.setPostId(post.getId());
        this.setAccountId(acc.getId());
        this.setDateShared(date);
        this.setComment(".");
    }

}
