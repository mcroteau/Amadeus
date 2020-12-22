package social.amadeus.web;

import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import social.amadeus.service.SearchService;

@Controller
public class SearchController {

    Gson gson = new Gson();

    @Autowired
    private SearchService searchService;

    @RequestMapping(value="/search", method= RequestMethod.GET, produces="application/json")
    public @ResponseBody
    String search(@RequestParam(value="q", required = false ) String q){
        return gson.toJson(searchService.queryBasic(q));
    }

}
