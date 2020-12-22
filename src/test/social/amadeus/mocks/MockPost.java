package social.amadeus.mocks;

import social.amadeus.model.Account;
import social.amadeus.model.Post;

public class MockPost extends Post {

    public MockPost(Account account, long date){
        this.setAccountId(account.getId());
        this.setContent("It was the best of times, it was the worst of times, it was the age of wisdom.");
        this.setDatePosted(date);
        this.setUpdateDate(date);
    }

}
