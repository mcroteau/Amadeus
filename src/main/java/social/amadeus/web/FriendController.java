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

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class FriendController {

    private static final Logger log = Logger.getLogger(FriendController.class);


    @Autowired
    private Utils utils;

    @Autowired
    private EmailService emailService;

    @Autowired
    private FriendRepo friendRepo;

    @Autowired
    private MessageRepo messageRepo;

    @Autowired
    private AuthService authService;


    @RequestMapping(value="/friend/invitations", method=RequestMethod.GET, produces="application/json")
    public @ResponseBody String invitations(ModelMap model,
                                        HttpServletRequest request,
                                        final RedirectAttributes redirect){

        Map<String, Object> data = new HashMap<String, Object>();
        Gson gson = new Gson();

        if(!authService.isAuthenticated()){
            data.put("error", "Authentication required");
            return gson.toJson(data);
        }

        Account account = authService.getAccount();

        List<FriendInvite> invites = friendRepo.invites(account.getId());
        for(FriendInvite invite : invites){
            if(account.getId() == invite.getInviteeId()){
                invite.setOwnersAccount(true);
            }
        }

        return gson.toJson(invites);
    }



    @RequestMapping(value="/friend/invite/{id}", method=RequestMethod.POST, produces="application/json")
    public @ResponseBody String invite(ModelMap model,
                                       HttpServletRequest request,
                                       final RedirectAttributes redirect,
                                       @PathVariable String id){

        Map<String, Object> response = new HashMap<String, Object>();
        Gson gson = new Gson();

        if(!authService.isAuthenticated()){
            response.put("error", "authentication required");
            String error = gson.toJson(response);
            return error;
        }

        Account authenticatedAccount = authService.getAccount();

        if(friendRepo.invite(authenticatedAccount.getId(), Long.parseLong(id), utils.getCurrentDate())){
            response.put("success", true);
        }else{
            response.put("error", true);
        }

        return gson.toJson(response);
    }



    @RequestMapping(value="/friend/accept/{id}", method=RequestMethod.POST, produces="application/json")
    public @ResponseBody
    String accept(ModelMap model,
               HttpServletRequest request,
               final RedirectAttributes redirect,
               @PathVariable String id){

        Map<String, Object> data = new HashMap<String, Object>();
        Gson gson = new Gson();

        if(!authService.isAuthenticated()){
            data.put("error", true);
            return gson.toJson(data);
        }

        Account authenticatedAccount = authService.getAccount();

        if(friendRepo.accept(Long.parseLong(id), authenticatedAccount.getId(), utils.getCurrentDate())) {
            data.put("success", true);
            return gson.toJson(data);
        }
        else{
            data.put("error", true);
            return gson.toJson(data);
        }
    }


    @RequestMapping(value="/friend/ignore/{id}", method=RequestMethod.POST, produces="application/json")
    public @ResponseBody
    String ignore(ModelMap model,
               HttpServletRequest request,
               final RedirectAttributes redirect,
               @PathVariable String id){

        Map<String, Object> data = new HashMap<String, Object>();
        Gson gson = new Gson();

        if(!authService.isAuthenticated()){
            data.put("error", true);
            String error = gson.toJson(data);
            return error;
        }

        Account account = authService.getAccount();

        boolean ignored = friendRepo.ignore(Long.parseLong(id), account.getId(), utils.getCurrentDate());

        data.put("success", ignored);
        return gson.toJson(data);
    }


    @RequestMapping(value="/friend/remove/{id}", method=RequestMethod.POST, produces="application/json")
    public @ResponseBody String remove(ModelMap model,
                                       HttpServletRequest request,
                                       final RedirectAttributes redirect,
                                       @PathVariable String id){

        Map<String, Object> data = new HashMap<String, Object>();
        Gson gson = new Gson();

        if(!authService.isAuthenticated()){
            data.put("error", true);
            String error = gson.toJson(data);
            return error;
        }

        Account account = authService.getAccount();
        boolean success = friendRepo.removeConnection(account.getId(), Long.parseLong(id));

        data.put("success", success);
        String json = gson.toJson(data);

        return json;
    }



    @RequiresAuthentication
    @RequestMapping(value="/friends/{id}", method=RequestMethod.GET, produces="application/json")
    public @ResponseBody String friends(ModelMap model,
                                        final RedirectAttributes redirect,
                                        @PathVariable String id){
        Map<String, Object> data = new HashMap<String, Object>();
        Gson gson = new Gson();

        if(!authService.isAuthenticated()){
            data.put("error", "authentication required");
            return gson.toJson(data);
        }

        List<Friend> friends = friendRepo.getFriends(Long.parseLong(id));
        for(Friend friend : friends){
            if(messageRepo.hasMessages(friend.getFriendId(), authService.getAccount().getId()))
                friend.setHasMessages(true);
        }

        return gson.toJson(friends);
    }

}
