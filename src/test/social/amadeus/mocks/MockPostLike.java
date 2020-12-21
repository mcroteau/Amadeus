package social.amadeus.mocks;

import social.amadeus.common.Utils;
import social.amadeus.model.Account;
import social.amadeus.model.Post;
import social.amadeus.model.PostLike;

public class MockPostLike extends PostLike {

    public MockPostLike(Account account, Post post){
        this.setPostId(post.getId());
        this.setAccountId(account.getId());
        this.setDateLiked(Utils.getDate());
    }

}
