package social.amadeus.web;

import com.google.gson.Gson;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import social.amadeus.repository.NotificationRepo;
import social.amadeus.model.Account;
import social.amadeus.repository.AccountRepo;
import social.amadeus.repository.FriendRepo;
import social.amadeus.service.AuthService;
import social.amadeus.service.NotificationService;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

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
