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
import social.amadeus.common.Utilities;
import social.amadeus.model.Account;
import social.amadeus.model.Friend;
import social.amadeus.model.FriendInvite;
import social.amadeus.service.EmailService;
import social.amadeus.dao.FriendDao;
import social.amadeus.dao.MessageDao;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class FriendController extends BaseController {

    private static final Logger log = Logger.getLogger(FriendController.class);


    @Autowired
    private Utilities utilities;

    @Autowired
    private EmailService emailService;

    @Autowired
    private FriendDao friendDao;

    @Autowired
    private MessageDao messageDao;


    @RequestMapping(value="/friend/invitations", method=RequestMethod.GET, produces="application/json")
    public @ResponseBody String invitations(ModelMap model,
                                        HttpServletRequest request,
                                        final RedirectAttributes redirect){

        Map<String, Object> data = new HashMap<String, Object>();
        Gson gson = new Gson();

        if(!authenticated()){
            data.put("error", "Authentication required");
            return gson.toJson(data);
        }

        Account account = getAuthenticatedAccount();

        List<FriendInvite> invites = friendDao.invites(account.getId());
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

        if(!authenticated()){
            response.put("error", "authentication required");
            String error = gson.toJson(response);
            return error;
        }

        Account authenticatedAccount = getAuthenticatedAccount();

        if(friendDao.invite(authenticatedAccount.getId(), Long.parseLong(id), utilities.getCurrentDate())){
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

        if(!authenticated()){
            data.put("error", true);
            return gson.toJson(data);
        }

        Account authenticatedAccount = getAuthenticatedAccount();

        if(friendDao.accept(Long.parseLong(id), authenticatedAccount.getId(), utilities.getCurrentDate())) {
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

        if(!authenticated()){
            data.put("error", true);
            String error = gson.toJson(data);
            return error;
        }

        Account account = getAuthenticatedAccount();

        boolean ignored = friendDao.ignore(Long.parseLong(id), account.getId(), utilities.getCurrentDate());

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

        if(!authenticated()){
            data.put("error", true);
            String error = gson.toJson(data);
            return error;
        }

        Account account = getAuthenticatedAccount();
        boolean success = friendDao.removeConnection(account.getId(), Long.parseLong(id));

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

        if(!authenticated()){
            data.put("error", "authentication required");
            return gson.toJson(data);
        }

        List<Friend> friends = friendDao.getFriends(Long.parseLong(id));
        for(Friend friend : friends){
            if(messageDao.hasMessages(friend.getFriendId(), getAuthenticatedAccount().getId()))
                friend.setHasMessages(true);
        }

        return gson.toJson(friends);
    }

}
