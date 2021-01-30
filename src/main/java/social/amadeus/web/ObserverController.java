package social.amadeus.web;

import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import social.amadeus.service.ObserverService;

@Controller
public class ObserverController {

    Gson gson = new Gson();

    @Autowired
    ObserverService observerService;

    @RequestMapping(value="/observe/{id}", method= RequestMethod.POST, produces="application/json")
    public @ResponseBody
    String observe(@PathVariable Long id){
        return gson.toJson(observerService.observe(id));
    }

    @RequestMapping(value="/unobserve/{id}", method= RequestMethod.POST, produces="application/json")
    public @ResponseBody
    String unobserve(@PathVariable Long id){
        return gson.toJson(observerService.unobserve(id));
    }

    @RequestMapping(value="/observers/{id}", method=RequestMethod.GET, produces="application/json")
    public @ResponseBody String getFriends(@PathVariable Long id){
        return gson.toJson(observerService.getObservers(id));
    }

}
