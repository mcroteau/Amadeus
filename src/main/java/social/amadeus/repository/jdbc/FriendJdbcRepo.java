package social.amadeus.repository.jdbc;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import org.springframework.stereotype.Repository;
import social.amadeus.repository.FriendRepo;
import social.amadeus.model.Account;
import social.amadeus.model.Friend;
import social.amadeus.model.FriendInvite;

@Repository
public class FriendJdbcRepo implements FriendRepo {

	@Autowired
	private JdbcTemplate jdbcTemplate;


	public long getCount() {
		String sql = "select count(*) from friends";
		long count = jdbcTemplate.queryForObject(sql, new Object[] { }, Long.class);
	 	return count; 
	}

	public long getCountInvites() {
		String sql = "select count(*) from friend_invites";
		long count = jdbcTemplate.queryForObject(sql, new Object[] { }, Long.class);
		return count;
	}

	public long getCountInvitesByAccount(Account authenticatedAccount) {
		String sql = "select count(*) from friend_invites where invited_id = ? and new_invite = ?";
		long count = jdbcTemplate.queryForObject(sql, new Object[] { authenticatedAccount.getId(), true }, Long.class);
		return count;
	}

	public FriendInvite getInvite(long invitee, long invited){
		FriendInvite friendInvite;
		try {

			String sql = "select * from friend_invites where account_id = ? and invited_id = ?";
			friendInvite = jdbcTemplate.queryForObject(sql, new Object[]{invitee, invited}, FriendInvite.class);

		}catch(Exception e){
			friendInvite = null;
		}
		return friendInvite;
	}

	public List<FriendInvite> getInvites(long invitedId){
		List<FriendInvite> invites = null;

		try {

			String sql = "select fi.invitee_id, fi.invited_id, " +
					"a.name, a.location, a.age, a.image_uri " +
					"from friend_invites fi inner join account a on fi.invitee_id = a.id " +
					"where invited_id = ? and new_invite = ?";

			invites = jdbcTemplate.query(sql, new Object[]{invitedId, true}, new BeanPropertyRowMapper<FriendInvite>(FriendInvite.class));

			if (invites == null) invites = new ArrayList<FriendInvite>();

		}catch(Exception e){
			e.printStackTrace();
		}

		return invites;
	}

	public boolean sendInvite(long inviteeId, long invitedId, long dateCreated){
		if(!isInvited(inviteeId, invitedId)) {
			String sql = "insert into friend_invites (invitee_id, invited_id, date_created, new_invite, accepted, ignored) values (?, ?, ?, ?, ?, ?)";
			try {
				jdbcTemplate.update(sql, new Object[]{
						inviteeId, invitedId, dateCreated, true, false, false
				});
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		return true;
	}

	public boolean isInvited(long inviteeId, long invitedId){
		String sql = "select * from friend_invites where invitee_id = ? and invited_id = ? and new_invite = true";
		FriendInvite friendInvite = null;

		try {
			friendInvite = jdbcTemplate.queryForObject(sql, new Object[]{inviteeId, invitedId},
					new BeanPropertyRowMapper<FriendInvite>(FriendInvite.class));
		}catch(Exception e){ }

		if(friendInvite != null){
			return true;
		}
		return false;
	}

	public boolean acceptInvite(long inviteeId, long invitedId, long currentDate){
		String sql = "update friend_invites set accepted = true, new_invite = false where invitee_id = ? and invited_id = ?";
		try {
			jdbcTemplate.update(sql, new Object[]{
					inviteeId, invitedId
			});
			saveConnection(inviteeId, invitedId, currentDate);
		}catch(Exception e){
			e.printStackTrace();
		}
		return true;
	}

	public boolean ignoreInvite(long accountId, long invitedId, long currentDate){
		String sql = "update friend_invites set ignored = true, new_invite = false where invitee_id = ? and invited_id = ?";
		try {
			jdbcTemplate.update(sql, new Object[]{
					accountId, invitedId
			});
		}catch(Exception e){
			e.printStackTrace();
		}
		return true;
	}

	public List<Friend> getFriends(long accountId) {
		String sql = "select f.account_id, f.friend_id, a.name, a.age, a.image_uri from friends f inner join account a on f.friend_id = a.id where account_id = ?";

		List<Friend> friends = jdbcTemplate.query(sql, new Object[] { accountId },
				new BeanPropertyRowMapper<Friend>(Friend.class));
		
		return friends;
	}

	public boolean saveConnection(long accountId1, long accountId2, long dateCreated) {
		String sql = "insert into friends (account_id, friend_id, date_created) values (?, ?, ?)";
		
		jdbcTemplate.update(sql, new Object[] { 
			accountId1, accountId2, dateCreated  
		});
		
		jdbcTemplate.update(sql, new Object[] { 
			accountId2, accountId1, dateCreated  
		});
		
		return true;
	}

	public boolean removeConnection(long accountId1, long accountId2) {
		String sql = "delete from friends where account_id = ? and friend_id = ?";

		jdbcTemplate.update(sql, new Object[] {
				accountId1, accountId2
		});

		jdbcTemplate.update(sql, new Object[] {
				accountId2, accountId1
		});

		return true;
	}

	public boolean isFriend(long accountId, long friendId){
		String sql = "select * from friends where account_id = ? and friend_id = ?";
		Friend friend = null;
		try {
			friend = jdbcTemplate.queryForObject(sql, new Object[]{accountId, friendId},
					new BeanPropertyRowMapper<Friend>(Friend.class));
		}catch(Exception e){}
		if(friend != null){
			return true;
		}
		return false;
	}

}
