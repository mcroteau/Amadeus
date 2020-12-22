package social.amadeus.mocks;

import social.amadeus.common.Utils;
import social.amadeus.model.Account;
import social.amadeus.model.PostShare;
import social.amadeus.model.PostShareComment;

public class MockPostShareComment extends PostShareComment {

    public MockPostShareComment(Account acc, PostShare postShare){
        this.setAccountId(acc.getId());
        this.setPostShareId(postShare.getId());
        this.setComment(".");
        this.setDateCreated(Utils.getDate());
    }

}
