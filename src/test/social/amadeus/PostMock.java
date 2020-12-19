package social.amadeus;

import social.amadeus.model.Account;
import social.amadeus.model.Post;

public class PostMock {

    public static Post mock(Account account, long date){
        Post post = new Post();
        post.setAccountId(account.getId());
        post.setContent("It was the best of times, it was the worst of times, it was the age of wisdom.");
        post.setDatePosted(date);
        post.setUpdateDate(date);
        post.setHidden(false);
        return post;
    }
}
