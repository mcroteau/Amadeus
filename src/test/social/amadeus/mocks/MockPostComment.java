package social.amadeus.mocks;

import social.amadeus.common.Utils;
import social.amadeus.model.Account;
import social.amadeus.model.Post;
import social.amadeus.model.PostComment;

public class MockPostComment extends PostComment {

    public MockPostComment(Account acc, Post post) {
        this.setAccountId(acc.getId());
        this.setPostId(post.getId());
        this.setComment(".");
        this.setDateCreated(Utils.getDate());
    }

}
