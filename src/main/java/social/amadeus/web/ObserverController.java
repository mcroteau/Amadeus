package social.amadeus.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import social.amadeus.service.ObserverService;

@Controller
public class ObserverController {

    @Autowired
    ObserverService observerService;
}
