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
import social.amadeus.service.AuthService;
import social.amadeus.service.PhoneService;
import social.amadeus.service.StripeService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class FlyerController {

    @Autowired
    private Utils utils;

    @Autowired
    private FlyerRepo flyerRepo;

    @Autowired
    private AccountRepo accountRepo;

    @Autowired
    private StripeService stripeService;

    @Autowired
    private PhoneService phoneService;

    @Autowired
    private AuthService authService;


    @RequestMapping(value="/flyer/create", method=RequestMethod.GET)
    public String create(){

        if(!authService.isAuthenticated()){
            return "redirect:/uno";
        }

        return "flyer/create";
    }

    @RequestMapping(value="/flyer/save", method=RequestMethod.POST)
    public String save(HttpServletRequest req,
                          RedirectAttributes redirect,
                          @ModelAttribute("flyer") Flyer flyer,
                          @RequestParam(value="flyerImage", required=false) CommonsMultipartFile flyerImage) {

        if(!authService.isAuthenticated()){
            return "redirect:/uno";
        }

        if(flyer.getPageUri().contains("http://") ||
                flyer.getPageUri().contains("https://")){
            redirect.addFlashAttribute("message", "Page url cannot contain http:// or https://, please re-enter your page url minus http & https");
            return "redirect:/flyer/create";
        }

        if(!flyerImage.isEmpty()){
            String imageUri = utils.write(flyerImage, Constants.IMAGE_DIRECTORY);
            flyer.setImageUri(imageUri);
        }

        Account authenticatedAccount = authService.getAccount();
        flyer.setAccountId(authenticatedAccount.getId());

        Flyer persistedFlyer = flyerRepo.save(flyer);
        accountRepo.savePermission(authenticatedAccount.getId(), Constants.FLYER_MAINTENANCE  + persistedFlyer.getId());

        return "redirect:/flyer/edit/" + persistedFlyer.getId();
    }


    @RequestMapping(value="/flyer/staging/{id}", method=RequestMethod.GET)
    public String staging(ModelMap modelMap, @PathVariable String id){

        String permission = Constants.FLYER_MAINTENANCE + id;
        if(authService.hasPermission(permission)) {
            Flyer flyer = flyerRepo.get(Long.parseLong(id));
            modelMap.put("flyer", flyer);
        }else{
            return "redirect:/unauthorized";
        }

        return "flyer/staging";
    }

    @RequestMapping(value="/flyer/edit/{id}", method=RequestMethod.GET)
    public String edit(ModelMap modelMap, @PathVariable String id){

        String permission = Constants.FLYER_MAINTENANCE + id;
        if(authService.hasPermission(permission)) {
            Flyer flyer = flyerRepo.get(Long.parseLong(id));
            modelMap.put("flyer", flyer);
        }else{
            return "redirect:/unauthorized";
        }

        return "flyer/edit";
    }


    @RequestMapping(value="/flyer/start", method=RequestMethod.POST)
    public String start(ModelMap modelMap,
                         @RequestParam(value="id") String id,
                         @RequestParam(value="stripeToken") String stripeToken){

        String permission = Constants.FLYER_MAINTENANCE + id;
        if(authService.hasPermission(permission)) {

            long date = utils.getCurrentDate();

            Flyer flyer = flyerRepo.get(Long.parseLong(id));
            flyer.setStartDate(date);
            flyer.setActive(true);
            long adRuns = flyer.getAdRuns() + 1;
            flyer.setAdRuns(adRuns);
            flyerRepo.update(flyer);

            stripeService.charge(stripeToken);

            modelMap.put("flyer", flyer);
            phoneService.support("Super Duper!");

        }else{
            return "redirect:/unauthorized";
        }

        return "redirect:/flyer/live/" + id;
    }


    @RequestMapping(value="/flyer/live/{id}", method=RequestMethod.GET)
    public String live(ModelMap modelMap, @PathVariable String id){

        String permission = Constants.FLYER_MAINTENANCE + id;
        if(authService.hasPermission(permission)) {
            Flyer flyer = flyerRepo.get(Long.parseLong(id));
            modelMap.put("flyer", flyer);
        }else{
            return "redirect:/unauthorized";
        }

        return "flyer/live";
    }

    @RequestMapping(value="/flyer/update", method=RequestMethod.POST)
    public String update(@ModelAttribute("flyer") Flyer flyer,
                         HttpServletRequest request,
                         final RedirectAttributes redirect,
                         @RequestParam(value="flyerImage", required=true) CommonsMultipartFile flyerImage) {

        String permission = Constants.FLYER_MAINTENANCE + flyer.getId();
        if(authService.hasPermission(permission)) {

            if(flyer.getPageUri().contains("http://") ||
                    flyer.getPageUri().contains("https://")){
                redirect.addFlashAttribute("message", "Page url cannot contain http:// or https://, please re-enter your page url minus http & https");
                return "redirect:/flyer/edit/" + flyer.getId();
            }

            if(flyerImage != null &&
                    flyerImage.getSize() > 0){
                String imageUri = utils.write(flyerImage, Constants.IMAGE_DIRECTORY);
                flyer.setImageUri(imageUri);
            }
            flyerRepo.update(flyer);
        }else{
            return "redirect:/unauthorized";
        }

        return "redirect:/flyer/edit/" + flyer.getId();
    }

    @RequestMapping(value="/admin/flyer/list", method=RequestMethod.GET)
    public String flyers(ModelMap modelMap){

        if(!authService.isAdministrator()){
            return "redirect:/unauthorized";
        }

        List<Flyer> flyers = flyerRepo.getFlyers();
        modelMap.put("flyers", flyers);

        return "flyer/list";
    }

    @RequestMapping(value="/flyer/list/{id}", method=RequestMethod.GET)
    public String flyers(ModelMap modelMap, @PathVariable String id){

        if(!authService.isAuthenticated()){
            return "redirect:/uno";
        }

        List<Flyer> flyers = flyerRepo.getFlyers(Long.parseLong(id));
        modelMap.put("flyers", flyers);

        return "flyer/list";
    }

}
