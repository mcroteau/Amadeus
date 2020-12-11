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
import social.amadeus.dao.NotificationDao;
import social.amadeus.model.Account;
import social.amadeus.dao.AccountDao;
import social.amadeus.dao.FriendDao;
import social.amadeus.service.AuthService;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Controller
public class NotificationController {

    private static final Logger log = Logger.getLogger(NotificationController.class);

    @Autowired
    private FriendDao friendDao;

    @Autowired
    private AccountDao accountDao;

    @Autowired
    private NotificationDao notificationDao;

    @Autowired
    private AuthService authService;
    

    @RequestMapping(value="/notifications/clear", method=RequestMethod.DELETE,  produces="application/json")
    public @ResponseBody String delete(ModelMap model,
                                       HttpServletRequest request,
                                       final RedirectAttributes redirect){

        Map<String, Object> data = new HashMap<String, Object>();
        Gson gson = new Gson();

        if(!authService.isAuthenticated()){
            data.put("error", "authentication required");
            return gson.toJson(data);
        }

        Account authenticatedAccount = authService.getAccount();

        if(notificationDao.clearNotifications(authenticatedAccount.getId())){
            data.put("success", true);
        }else{
            data.put("error", true);
        }

        return gson.toJson(data);
    }
}
