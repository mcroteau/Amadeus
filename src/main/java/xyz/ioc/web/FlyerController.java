package xyz.ioc.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import xyz.ioc.common.Constants;
import xyz.ioc.common.Utilities;
import xyz.ioc.dao.AccountDao;
import xyz.ioc.dao.FlyerDao;
import xyz.ioc.model.Account;
import xyz.ioc.model.Flyer;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class FlyerController extends BaseController {

    @Autowired
    private Utilities utilities;

    @Autowired
    private FlyerDao flyerDao;

    @Autowired
    private AccountDao accountDao;

    @RequestMapping(value="/flyer/create", method=RequestMethod.GET)
    public String create(){

        if(!authenticated()){
            return "redirect:/uno";
        }

        return "flyer/create";
    }

    @RequestMapping(value="/flyer/save", method=RequestMethod.POST)
    public String save(HttpServletRequest req,
                          @ModelAttribute("flyer") Flyer flyer,
                          @RequestParam(value="flyerImage", required=false) CommonsMultipartFile flyerImage) {

        if(!flyerImage.isEmpty()){
            String imageUri = utilities.write(flyerImage, Constants.IMAGE_DIRECTORY);
            flyer.setImageUri(imageUri);
        }

        Account authenticatedAccount = getAuthenticatedAccount();
        flyer.setAccountId(authenticatedAccount.getId());

        Flyer persistedFlyer = flyerDao.save(flyer);
        accountDao.saveAccountPermission(authenticatedAccount.getId(), Constants.FLYER_MAINTENANCE  + persistedFlyer.getId());

        return "redirect:/flyer/edit/" + persistedFlyer.getId();
    }

    @RequestMapping(value="/flyer/edit/{id}", method=RequestMethod.GET)
    public String edit(ModelMap modelMap, @PathVariable String id){

        if(hasPermission(Constants.FLYER_MAINTENANCE + id)) {
            Flyer flyer = flyerDao.get(Long.parseLong(id));
            modelMap.put("flyer", flyer);
        }else{
            return "redirect:/unauthorized";
        }

        return "flyer/edit";
    }

    @RequestMapping(value="/flyer/update/{id}", method=RequestMethod.POST)
    public String update(@ModelAttribute("flyer") Flyer flyer,
                         HttpServletRequest request,
                         final RedirectAttributes redirect,
                         @RequestParam(value="flyerImage", required=false) CommonsMultipartFile flyerImage) {

        if(hasPermission(Constants.FLYER_MAINTENANCE + flyer.getId())) {
            if(!flyerImage.isEmpty()){
                String imageUri = utilities.write(flyerImage, Constants.IMAGE_DIRECTORY);
                flyer.setImageUri(imageUri);
            }
            flyerDao.update(flyer);
        }else{
            return "redirect:/unauthorized";
        }

        return "redirect:/flyer/edit/" + flyer.getId();
    }

    @RequestMapping(value="/admin/flyer/list", method=RequestMethod.GET)
    public String flyers(ModelMap modelMap){

        if(!administrator()){
            return "redirect:/unauthorized";
        }

        List<Flyer> flyers = flyerDao.getFlyers();
        modelMap.put("flyers", flyers);

        return "flyer/list";
    }

    @RequestMapping(value="/flyer/list/{id}", method=RequestMethod.GET)
    public String flyers(ModelMap modelMap, @PathVariable String id){

        if(!authenticated()){
            return "redirect:/uno";
        }

        List<Flyer> flyers = flyerDao.getFlyers(Long.parseLong(id));
        modelMap.put("flyers", flyers);

        return "flyer/list";
    }

}
