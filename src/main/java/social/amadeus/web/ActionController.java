package social.amadeus.web;

import com.google.gson.Gson;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import social.amadeus.common.Constants;
import social.amadeus.common.Utils;
import social.amadeus.repository.*;
import social.amadeus.model.*;
import social.amadeus.service.ActionService;
import social.amadeus.service.AuthService;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Controller
public class ActionController {

    private static final Logger log = Logger.getLogger(ActionController.class);

    Gson gson = new Gson();

    @Autowired
    ActionService actionService;

    @RequestMapping(value="/action", method=RequestMethod.GET)
    public String getAction(ModelMap modelMap,
                            @RequestParam(value="uri", required=false) String uri){
        return actionService.getAction(uri, modelMap);
    }

    @CrossOrigin(origins="*")//Thank you!
    @RequestMapping(value="/action/like", method=RequestMethod.POST)
    public String likeWebsite(ModelMap modelMap,
                       @RequestParam(value="uri", required = true ) String uri){
        return actionService.likeWebsite(uri, modelMap);
    }

    @CrossOrigin(origins="*")
    @RequestMapping(value="/action/share", method=RequestMethod.POST)
    public String share(ModelMap modelMap,
                        @RequestParam(value="uri", required = true ) String uri,
                        @RequestParam(value="comment", required = true ) String comment){
        return actionService.shareWebsite(uri, comment, modelMap);
    }

    @CrossOrigin(origins="*")
    @RequestMapping(value="/action/likes", method=RequestMethod.GET, produces="application/json")
    public @ResponseBody String getLikes(@RequestParam(value="uri", required = true ) String uri){
        return gson.toJson(actionService.getLikes(uri));
    }


    @CrossOrigin(origins="*")
    @RequestMapping(value="/action/shares", method=RequestMethod.GET, produces="application/json")
    public @ResponseBody String shares(@RequestParam(value="uri", required=false) String uri){
        return gson.toJson(actionService.getSharesCount(uri));
    }

}
