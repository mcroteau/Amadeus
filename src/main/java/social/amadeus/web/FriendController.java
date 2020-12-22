package social.amadeus.web;

import com.google.gson.Gson;
import org.apache.log4j.Logger;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import social.amadeus.common.Utils;
import social.amadeus.model.Account;
import social.amadeus.model.Friend;
import social.amadeus.model.FriendInvite;
import social.amadeus.service.AuthService;
import social.amadeus.service.EmailService;
import social.amadeus.repository.FriendRepo;
import social.amadeus.repository.MessageRepo;
import social.amadeus.service.FriendService;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class FriendController {

    private static final Logger log = Logger.getLogger(FriendController.class);

    Gson gson = new Gson();

    @Autowired
    FriendService friendService;


    @RequestMapping(value="/friend/invites", method=RequestMethod.GET, produces="application/json")
    public @ResponseBody String getinvitiations(){
        return gson.toJson(friendService.getInvites());
    }

    @RequestMapping(value="/friend/invite/{id}", method=RequestMethod.POST, produces="application/json")
    public @ResponseBody String sendInvite(@PathVariable String id){
        return gson.toJson(friendService.sendInvite(id));
    }

    @RequestMapping(value="/friend/accept/{id}", method=RequestMethod.POST, produces="application/json")
    public @ResponseBody String acceptInvite(@PathVariable String id){
        return gson.toJson(friendService.acceptInvite(id));
    }

    @RequestMapping(value="/friend/ignore/{id}", method=RequestMethod.POST, produces="application/json")
    public @ResponseBody String ignoreInvite(@PathVariable String id){
        return gson.toJson(friendService.ignoreInvite(id));
    }

    @RequestMapping(value="/friend/remove/{id}", method=RequestMethod.POST, produces="application/json")
    public @ResponseBody String disconnectedFriend(@PathVariable String id){
        return gson.toJson(friendService.disconnectedFriend(id));
    }

    @RequestMapping(value="/friends/{id}", method=RequestMethod.GET, produces="application/json")
    public @ResponseBody String getFriends(@PathVariable String id){
        return gson.toJson(friendService.getFriends(id));
    }

}
