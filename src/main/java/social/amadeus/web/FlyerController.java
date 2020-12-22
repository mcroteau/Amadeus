package social.amadeus.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import social.amadeus.repository.FlyerRepo;
import social.amadeus.common.Constants;
import social.amadeus.common.Utils;
import social.amadeus.repository.AccountRepo;
import social.amadeus.model.Account;
import social.amadeus.model.Flyer;
import social.amadeus.service.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class FlyerController {

    @Autowired
    private FlyerService flyerService;

    @RequestMapping(value="/flyer/create", method=RequestMethod.GET)
    public String create(){
        return flyerService.create();
    }

    @RequestMapping(value="/flyer/save", method=RequestMethod.POST)
    public String save(RedirectAttributes redirect,
                       @ModelAttribute("flyer") Flyer flyer,
                       @RequestParam(value="flyerImage", required=false) CommonsMultipartFile flyerImage) {
        return flyerService.save(flyer, flyerImage, redirect);
    }


    @RequestMapping(value="/flyer/staging/{id}", method=RequestMethod.GET)
    public String staging(ModelMap modelMap, @PathVariable String id){
        return flyerService.staging(id, modelMap);
    }

    @RequestMapping(value="/flyer/edit/{id}", method=RequestMethod.GET)
    public String edit(ModelMap modelMap, @PathVariable String id){
        return flyerService.edit(id, modelMap);
    }


    @RequestMapping(value="/flyer/start", method=RequestMethod.POST)
    public String start(ModelMap modelMap,
                         @RequestParam(value="id") String id,
                         @RequestParam(value="stripeToken") String stripeToken){
        return flyerService.start(id, stripeToken, modelMap);
    }


    @RequestMapping(value="/flyer/live/{id}", method=RequestMethod.GET)
    public String live(ModelMap modelMap, @PathVariable String id){
        return flyerService.live(id, modelMap);
    }

    @RequestMapping(value="/flyer/update", method=RequestMethod.POST)
    public String update(@ModelAttribute("flyer") Flyer flyer,
                         final RedirectAttributes redirect,
                         @RequestParam(value="flyerImage", required=true) CommonsMultipartFile flyerImage) {
        return flyerService.update(flyer, flyerImage, redirect);
    }

    @RequestMapping(value="/admin/flyer/list", method=RequestMethod.GET)
    public String flyers(ModelMap modelMap){
        return flyerService.flyers(modelMap);
    }

    @RequestMapping(value="/flyer/list/{id}", method=RequestMethod.GET)
    public String userFlyers(ModelMap modelMap, @PathVariable String id){
        return flyerService.userFlyers(id, modelMap);
    }

}
