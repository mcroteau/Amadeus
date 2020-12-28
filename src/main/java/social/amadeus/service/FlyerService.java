package social.amadeus.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import social.amadeus.common.Constants;
import social.amadeus.common.Utils;
import social.amadeus.model.Account;
import social.amadeus.model.Flyer;
import social.amadeus.repository.AccountRepo;
import social.amadeus.repository.FlyerRepo;

import java.util.List;

public class FlyerService {


    @Autowired
    private FlyerRepo flyerRepo;

    @Autowired
    private AccountRepo accountRepo;

    @Autowired
    private StripeService stripeService;

    @Autowired
    private AuthService authService;

    @Autowired
    private SyncService syncService;

    @Autowired
    private PhoneService phoneService;


    private Flyer synchronizeFlyerImage(Flyer flyer, CommonsMultipartFile flyerImage){
        try {
            String fileName = Utils.getGenericFileName(flyerImage);
            String imageUri = Constants.HTTPS + Constants.DO_ENDPOINT + "/" + fileName;

            syncService.send(fileName, flyerImage.getInputStream());
            flyer.setImageUri(imageUri);
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return flyer;
    }


    public String create() {
        if(!authService.isAuthenticated()){
            return "redirect:/uno";
        }

        return "flyer/create";
    }

    public String save(Flyer flyer, CommonsMultipartFile flyerImage, RedirectAttributes redirect) {
        if(!authService.isAuthenticated()){
            return "redirect:/uno";
        }

        if(flyer.getPageUri().contains("http://") ||
                flyer.getPageUri().contains("https://")){
            redirect.addFlashAttribute("message", "Page url cannot contain http:// or https://, please re-enter your page url minus http & https");
            return "redirect:/flyer/create";
        }

        if(!flyerImage.isEmpty() &&
                flyerImage.getSize() > 0){
            synchronizeFlyerImage(flyer, flyerImage);
        }

        Account authenticatedAccount = authService.getAccount();
        flyer.setAccountId(authenticatedAccount.getId());

        Flyer persistedFlyer = flyerRepo.save(flyer);
        accountRepo.savePermission(authenticatedAccount.getId(), Constants.FLYER_MAINTENANCE  + persistedFlyer.getId());

        return "redirect:/flyer/edit/" + persistedFlyer.getId();
    }

    public String staging(String id, ModelMap modelMap) {
        if(!authService.isAuthenticated()){
            return "redirect:/";
        }

        String permission = Constants.FLYER_MAINTENANCE + id;
        if(!authService.hasPermission(permission)) {
            return "redirect:/unauthorized";
        }

        Flyer flyer = flyerRepo.get(Long.parseLong(id));
        modelMap.put("flyer", flyer);

        return "flyer/staging";
    }

    public String edit(String id, ModelMap modelMap) {
        if(!authService.isAuthenticated()){
            return "redirect:/";
        }

        String permission = Constants.FLYER_MAINTENANCE + id;
        if(!authService.hasPermission(permission)) {
            return "redirect:/unauthorized";
        }

        Flyer flyer = flyerRepo.get(Long.parseLong(id));
        modelMap.put("flyer", flyer);

        return "flyer/edit";
    }

    public String start(String id, String stripeToken, ModelMap modelMap) {
        if(!authService.isAuthenticated()){
            return "redirect:/";
        }

        String permission = Constants.FLYER_MAINTENANCE + id;
        if(!authService.hasPermission(permission)) {
            return "redirect:/unauthorized";
        }

        Flyer flyer = flyerRepo.get(Long.parseLong(id));
        flyer.setStartDate(Utils.getDate());
        flyer.setActive(true);
        long adRuns = flyer.getAdRuns() + 1;
        flyer.setAdRuns(adRuns);
        flyerRepo.update(flyer);

        stripeService.charge(stripeToken);

        modelMap.put("flyer", flyer);
        phoneService.support("Super Duper!");

        return "redirect:/flyer/live/" + id;
    }

    public String live(String id, ModelMap modelMap) {
        if(!authService.isAuthenticated()){
            return "redirect:/";
        }

        String permission = Constants.FLYER_MAINTENANCE + id;
        if(!authService.hasPermission(permission)) {
            return "redirect:/unauthorized";
        }

        Flyer flyer = flyerRepo.get(Long.parseLong(id));
        modelMap.put("flyer", flyer);
        return "flyer/live";
    }

    public String update(Flyer flyer, CommonsMultipartFile flyerImage, RedirectAttributes redirect) {
        if(!authService.isAuthenticated()){
            return "redirect:/";
        }

        String permission = Constants.FLYER_MAINTENANCE + flyer.getId();
        if(!authService.hasPermission(permission)) {
            return "redirect:/unauthorized";
        }

        if(flyer.getPageUri().contains("http://") ||
                flyer.getPageUri().contains("https://")){
            redirect.addFlashAttribute("message", "Page url cannot contain http:// or https://, please re-enter your page url minus http & https");
            return "redirect:/flyer/edit/" + flyer.getId();
        }

        if(flyerImage != null &&
                flyerImage.getSize() > 0){
            synchronizeFlyerImage(flyer, flyerImage);
        }
        flyerRepo.update(flyer);

        return "redirect:/flyer/edit/" + flyer.getId();
    }

    public String flyers(ModelMap modelMap) {
        if(!authService.isAdministrator()){
            return "redirect:/unauthorized";
        }

        List<Flyer> flyers = flyerRepo.getFlyers();
        modelMap.put("flyers", flyers);

        return "flyer/list";
    }

    public String userFlyers(String id, ModelMap modelMap) {
        if(!authService.isAuthenticated()){
            return "redirect:/uno";
        }

        if(Long.parseLong(id) != authService.getAccount().getId())return Constants.UNAUTHORIZED_REDIRECT;

        List<Flyer> flyers = flyerRepo.getFlyers(Long.parseLong(id));
        modelMap.put("flyers", flyers);

        return "flyer/list";
    }
}
