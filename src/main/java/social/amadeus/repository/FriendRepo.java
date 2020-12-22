package social.amadeus.repository;

import java.util.List;

import social.amadeus.model.Account;
import social.amadeus.model.Friend;
import social.amadeus.model.FriendInvite;

public interface FriendRepo {

	long getCount();

	long getCountInvites();

	long getCountInvitesByAccount(Account authenticatedAccount);

	boolean sendInvite(long inviteeId, long invited, long dateCreated);

	List<FriendInvite> getInvites(long invitedId);

	boolean isInvited(long inviteeId, long invitedId);

	boolean acceptInvite(long inviteeId, long invitedId, long currentDate);

	boolean ignoreInvite(long inviteeId, long InvitedId, long currentDate);

	List<Friend> getFriends(long accountId);
	
	boolean saveConnection(long accountId1, long accountId2, long dateCreated);

	boolean removeConnection(long accountId1, long accountId2);

	boolean isFriend(long accountId, long friendId);

}

