package social.amadeus.repository;

import java.util.List;

import social.amadeus.model.Account;
import social.amadeus.model.Friend;
import social.amadeus.model.FriendInvite;

public interface FriendRepo {

	long count();

	long countInvites();

	long countInvitesByAccount(Account authenticatedAccount);

	boolean invite(long inviteeId, long invited, long dateCreated);

	List<FriendInvite> invites(long invitedId);

	boolean invited(long inviteeId, long invitedId);

	boolean accept(long inviteeId, long invitedId, long currentDate);

	boolean ignore(long inviteeId, long InvitedId, long currentDate);

	List<Friend> getFriends(long accountId);
	
	boolean saveConnection(long accountId1, long accountId2, long dateCreated);

	boolean removeConnection(long accountId1, long accountId2);

	boolean isFriend(long accountId, long friendId);

}

