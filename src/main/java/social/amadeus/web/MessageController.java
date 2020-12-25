package social.amadeus.web;

import com.google.gson.Gson;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import social.amadeus.model.Message;
import social.amadeus.service.MessageService;

@Controller
public class MessageController {

    private static final Logger log = Logger.getLogger(MessageController.class);

    Gson gson = new Gson();

    @Autowired
    MessageService messageService;


    @RequestMapping(value="/messages/unread", method=RequestMethod.GET, produces="application/json")
    public @ResponseBody String  unread() throws Exception {
        return gson.toJson(messageService.unread());
    }


    @RequestMapping(value="/messages/read/{id}", method=RequestMethod.POST, produces="application/json")
    public @ResponseBody String read( @PathVariable String id ) {
        return gson.toJson(messageService.read(id));
    }


    @RequestMapping(value="/messages/{id}", method=RequestMethod.GET, produces="application/json")
    public @ResponseBody String getMessages(@PathVariable String id) {
        return gson.toJson(messageService.getMessages(id));
    }


    @RequestMapping(value="/message/send/{id}", method=RequestMethod.POST, produces="application/json")
    public @ResponseBody String sendMessage(Message message,
                                            @PathVariable String id) {
        return gson.toJson(messageService.sendMessage(id, message));
    }

}
