package social.amadeus.service;

import org.ocpsoft.prettytime.PrettyTime;
import org.springframework.beans.factory.annotation.Autowired;
import social.amadeus.common.Constants;
import social.amadeus.common.Utils;
import social.amadeus.model.Account;
import social.amadeus.model.Message;
import social.amadeus.model.OutputMessage;
import social.amadeus.repository.AccountRepo;
import social.amadeus.repository.MessageRepo;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessageService {

    @Autowired
    private MessageRepo messageRepo;

    @Autowired
    private AccountRepo accountRepo;

    @Autowired
    private AuthService authService;

    public Map<String, Integer> unread() {
        Map<String, Integer> respData = new HashMap<>();
        try {
            Account account = authService.getAccount();
            respData.put("count", messageRepo.unread(account.getId()));
        }catch(Exception e){
            respData.put("count", 0);
        }

        return respData;
    }

    public Map<String, Boolean> read(String id) {

        Map<String, Boolean> respData = new HashMap<>();

        try {
            Account recipient = authService.getAccount();
            Account sender = accountRepo.get(Long.parseLong(id));
            messageRepo.read(sender.getId(), recipient.getId());
        }catch(Exception e){
            respData.put("success", false);
        }

        respData.put("success", true);
        return respData;
    }

    public OutputMessage getMessages(String id) {
        OutputMessage outputMessage = new OutputMessage();

        try {
            Account recipient = authService.getAccount();
            Account sender = accountRepo.get(Long.parseLong(id));

            List<Message> messages = messageRepo.messages(sender.getId(), recipient.getId());


            for (Message m : messages) {
                SimpleDateFormat format = new SimpleDateFormat(Constants.DATE_SEARCH_FORMAT);
                Date date = format.parse(Long.toString(m.getDateSent()));
                PrettyTime p = new PrettyTime();
                m.setTimeAgo(p.format(date));

                Account s = accountRepo.get(m.getSenderId());
                Account r = accountRepo.get(m.getRecipientId());

                m.setSender(s.getName());
                m.setRecipient(r.getName());
            }

            outputMessage.setStatusMessage(Constants.SUCCESS);
            outputMessage.setRecipientId(sender.getId());
            outputMessage.setRecipientImageUri(sender.getImageUri());
            outputMessage.setMessages(messages);

        }catch(Exception e){
            outputMessage.setStatusMessage(Constants.X_MESSAGE);
        }
        return outputMessage;
    }

    public Map<String, Boolean> sendMessage(String id, Message message) {
        Account sender = authService.getAccount();
        Account recipient = accountRepo.get(Long.parseLong(id));

        message.setSenderId(sender.getId());
        message.setRecipientId(recipient.getId());
        message.setDateSent(Utils.getDate());
        messageRepo.send(message);

        Map<String, Boolean> respData = new HashMap<>();
        respData.put("success", true);

        return respData;
    }
}
