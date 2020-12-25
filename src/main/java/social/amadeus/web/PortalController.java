package social.amadeus.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import social.amadeus.service.PortalService;

@Controller
public class PortalController {

    @Autowired
    PortalService portalService;

    @RequestMapping(value="/", method= RequestMethod.GET)
    public String z(){
        return portalService.z();
    }

}
