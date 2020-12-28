package social.amadeus.mocks;

import social.amadeus.common.Utils;
import social.amadeus.model.Account;
import social.amadeus.model.Sheet;

public class MockSheet extends Sheet {
    public MockSheet(){
        this.setTitle(Utils.getRandomString(13));
        this.setDescription(Utils.getRandomString(3));
        this.setImageUri("https://cdn11.bigcommerce.com/s-c0w1eon/images/stencil/1280x1280/products/488/4276/us_outdoor_endura_tex_flag_3x51__58254.14593876731__33151.1578663295.jpg?c=2");
        this.setEndpoint("mycountrytisofthee");
        this.setDateCreated(Utils.getDate());
    }
}
