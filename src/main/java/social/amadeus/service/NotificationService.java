package social.amadeus.service;

import org.springframework.beans.factory.annotation.Autowired;
import social.amadeus.common.Utils;
import social.amadeus.model.Notification;

public class NotificationService {

    @Autowired
    private Utils utilities;

    public Notification createNotification(long postAccountId, long authenticatedAccountId, long postId, boolean liked, boolean shared, boolean commented){
        Notification notification = new Notification();
        notification.setDateCreated(utilities.getCurrentDate());

        notification.setPostAccountId(postAccountId);
        notification.setAuthenticatedAccountId(authenticatedAccountId);
        notification.setPostId(postId);
        notification.setLiked(liked);
        notification.setShared(shared);
        notification.setCommented(commented);
        return notification;
    }

}
