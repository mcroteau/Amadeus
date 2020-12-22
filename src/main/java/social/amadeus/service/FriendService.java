package social.amadeus.service;

import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import social.amadeus.common.Constants;
import social.amadeus.common.Utils;
import social.amadeus.model.Account;
import social.amadeus.model.Friend;
import social.amadeus.model.FriendInvite;
import social.amadeus.repository.FriendRepo;
import social.amadeus.repository.MessageRepo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FriendService {

    @Autowired
    private FriendRepo friendRepo;

    @Autowired
    private MessageRepo messageRepo;

    @Autowired
    private AuthService authService;

    public Map<String, Object> getInvites() {
        Map<String, Object> respData = new HashMap<String, Object>();

        if(!authService.isAuthenticated()){
            respData.put("status", Constants.AUTHENTICATION_REQUIRED);
            respData.put("invites", new ArrayList<FriendInvite>());
            return respData;
        }

        Account account = authService.getAccount();

        List<FriendInvite> invites = friendRepo.getInvites(account.getId());
        List<FriendInvite> finalInvites = new ArrayList<>();
        for(FriendInvite invite : invites){
            if(account.getId() != invite.getInviteeId()){
                finalInvites.add(invite);
            }
        }

        respData.put("invites", finalInvites);
        return respData;
    }


    public String sendInvite(String id) {
        if(!authService.isAuthenticated()){
            return Constants.AUTHENTICATION_REQUIRED;
        }

        Account authdAccount = authService.getAccount();
        if(!friendRepo.sendInvite(authdAccount.getId(), Long.parseLong(id), Utils.getDate())){
            return Constants.X_MESSAGE;
        }

        return Constants.SUCCESS;
    }

    public String acceptInvite(String id) {
        if(!authService.isAuthenticated()){
            return Constants.AUTHENTICATION_REQUIRED;
        }

        Account authdAccount = authService.getAccount();
        if(!friendRepo.acceptInvite(Long.parseLong(id), authdAccount.getId(), Utils.getDate())) {
            return Constants.X_MESSAGE;
        }

        return Constants.SUCCESS;
    }

    public String ignoreInvite(String id) {
        if(!authService.isAuthenticated()){
            return Constants.AUTHENTICATION_REQUIRED;
        }

        Account account = authService.getAccount();

        if(!friendRepo.ignoreInvite(Long.parseLong(id), account.getId(), Utils.getDate())){
            return Constants.X_MESSAGE;
        }

        return Constants.SUCCESS;
    }

    public String disconnectedFriend(String id) {
        if(!authService.isAuthenticated()){
            return Constants.AUTHENTICATION_REQUIRED;
        }

        Account account = authService.getAccount();
        if(friendRepo.removeConnection(account.getId(), Long.parseLong(id))){
            return Constants.X_MESSAGE;
        }

        return Constants.SUCCESS;
    }

    public Map<String, Object> getFriends(String id) {
        Map<String, Object> respData = new HashMap<>();

        if(!authService.isAuthenticated()){
            respData.put("status", Constants.AUTHENTICATION_REQUIRED);
            respData.put("friends", new ArrayList<Friend>());
            return respData;
        }

        List<Friend> friends = friendRepo.getFriends(Long.parseLong(id));
        for(Friend friend : friends){
            if(messageRepo.hasMessages(friend.getFriendId(), authService.getAccount().getId()))
                friend.setHasMessages(true);
        }

        respData.put("status", Constants.SUCCESS);
        respData.put("friends", friends);
        return respData;
    }
}
