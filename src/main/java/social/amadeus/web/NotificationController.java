package social.amadeus.web;

import com.google.gson.Gson;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import social.amadeus.service.NotificationService;

@Controller
public class NotificationController {

    private static final Logger log = Logger.getLogger(NotificationController.class);

    Gson gson = new Gson();

    @Autowired
    NotificationService notificationService;

    @RequestMapping(value="/notifications/clear", method=RequestMethod.DELETE,  produces="application/json")
    public @ResponseBody String deleteUserNotifications(){
        return gson.toJson(notificationService.deleteUserNotifications());
    }
}
