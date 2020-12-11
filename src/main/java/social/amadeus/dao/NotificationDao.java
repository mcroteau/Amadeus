package social.amadeus.dao;

import java.util.List;

import social.amadeus.model.Account;
import social.amadeus.model.Notification;

public interface NotificationDao {

	public long count();

	public long countByAccount(Account authenticatedAccount);

	public Notification get(int id);
		
	public boolean save(Notification notification);
	
	public List<Notification> notifications(long notificationAccountId);

	public boolean clearNotifications(long notificationAccountId);

	public Notification getLikeNotification(long postId, long postAccountId, long authenticatedAccountId);

	public Notification getShareNotification(long postId, long postAccountId, long authenticatedAccountId);

	public Notification getCommentNotification(long postId, long postAccountId, long authenticatedAccountId);

	public boolean delete(long id);
}