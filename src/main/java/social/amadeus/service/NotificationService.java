package social.amadeus.service;

import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import social.amadeus.common.Constants;
import social.amadeus.common.Utils;
import social.amadeus.model.Account;
import social.amadeus.model.Notification;
import social.amadeus.repository.AccountRepo;
import social.amadeus.repository.FriendRepo;
import social.amadeus.repository.NotificationRepo;

import java.util.HashMap;
import java.util.Map;


@Service
public class NotificationService {


    @Autowired
    private NotificationRepo notificationRepo;

    @Autowired
    private AuthService authService;



    public Notification createNotification(long postAccountId, long authenticatedAccountId, long postId, boolean liked, boolean shared, boolean commented){
        Notification notification = new Notification();
        notification.setDateCreated(Utils.getDate());

        notification.setPostAccountId(postAccountId);
        notification.setAuthenticatedAccountId(authenticatedAccountId);
        notification.setPostId(postId);
        notification.setLiked(liked);
        notification.setShared(shared);
        notification.setCommented(commented);
        return notification;
    }

    public String deleteUserNotifications() {

        if(!authService.isAuthenticated()){
            return Constants.AUTHENTICATION_REQUIRED;
        }

        Account authenticatedAccount = authService.getAccount();
        if(!notificationRepo.clearNotifications(authenticatedAccount.getId())){
            return Constants.X_MESSAGE;
        }

        return Constants.SUCCESS;
    }
}
