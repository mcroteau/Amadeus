package social.amadeus.mocks;

import social.amadeus.common.Constants;
import social.amadeus.common.Utils;
import social.amadeus.model.Account;

public class MockAccount extends Account {

    public MockAccount(int instance){
        this.setName("Marisa." + instance);
        this.setAge(Integer.toString(Utils.getRandomNumber(2)));
        this.setUsername("jf+"+instance + "@mail.com");
        this.setPassword(Constants.PASSWORD);
        this.setLocation(Utils.getRandomString(7));
    }
}
