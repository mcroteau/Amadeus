package social.amadeus.dao;

import social.amadeus.model.Account;
import social.amadeus.model.Message;

import java.util.List;

public interface MessageDao {

    public long count();

    public long countByAccount(Account authenticatedAccount);

    public long getId();

    public Message send(Message message);

    public List<Message> messages(long partipantOne, long participantDos);

    public boolean hasMessages(long sender, long recipient);

    public int unread(long recipientId);

    public boolean read(long sender, long recipientId);

}
