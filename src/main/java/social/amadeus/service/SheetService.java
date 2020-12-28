package social.amadeus.service;

import org.apache.commons.text.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.ui.ModelMap;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import social.amadeus.common.Constants;
import social.amadeus.common.Utils;
import social.amadeus.model.Account;
import social.amadeus.model.Sheet;
import social.amadeus.repository.AccountRepo;
import social.amadeus.repository.SheetRepo;

import java.util.List;

public class SheetService {

    private static final Logger log = Logger.getLogger(SheetService.class);

    @Autowired
    SheetRepo sheetRepo;

    @Autowired
    AccountRepo accountRepo;

    @Autowired
    AuthService authService;

    @Autowired
    SyncService syncService;

    @Autowired
    Environment env;

    private String getSheetPermission(Long id) {
        return Constants.SHEET_MAINTENANCE + id;
    }


    private Sheet syncSheetImage(Sheet sheet, CommonsMultipartFile sheetImage){
        try {
            String fileName = Utils.getGenericFileName(sheetImage);
            String imageUri = Constants.HTTPS + Constants.DO_ENDPOINT + "/" + fileName;

            syncService.send(fileName, sheetImage.getInputStream());
            sheet.setImageUri(imageUri);
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return sheet;
    }

    public Sheet getData(Long id) {
        if(!authService.isAuthenticated()){
            Sheet sheet = new Sheet();
            sheet.setStatus(Constants.X_MESSAGE);
            return sheet;
        }

        Sheet sheet = sheetRepo.get(id);
        String permission = getSheetPermission(id);
        if(authService.hasPermission(permission)){
            sheet.setPermitted(true);
        }

        return sheet;
    }

    public String view(String endpoint, ModelMap modelMap) {
        Sheet sheet = sheetRepo.getByEndpoint(endpoint);
        sheet.setSheetViews(sheet.getSheetViews() + 1);
        sheetRepo.update(sheet);
        modelMap.put("sheet", sheet);
        return "sheet/gander";
    }

    public String create() {
        if(!authService.isAuthenticated()){
            return "redirect:/uno";
        }

        return "sheet/create";
    }

    public String save(Sheet sheet, CommonsMultipartFile sheetImage, RedirectAttributes redirect) {
        if(!authService.isAuthenticated()){
            return Constants.AUTHENTICATION_REQUIRED;
        }

        String endpoint = sheet.getEndpoint().replaceAll("[^\\w\\s]", "");
        log.info(endpoint);

        if(sheetRepo.getByEndpoint(endpoint) != null){
            redirect.addFlashAttribute("message", "Endpoint exists, please try another without special characters");
            redirect.addAttribute("endpoint", "make");
            redirect.addFlashAttribute("sheet", sheet);
            return "redirect:/sheet/create";
        }

        if(!Utils.isTestEnvironment(env) &&
                !sheetImage.isEmpty() &&
                    sheetImage.getSize() > 0){
            syncSheetImage(sheet, sheetImage);
        }

        Account authdAccount = authService.getAccount();
        sheet.setAccountId(authdAccount.getId());
        sheet.setDateCreated(Utils.getDate());

        Sheet savedSheet = sheetRepo.save(sheet);
        accountRepo.savePermission(authdAccount.getId(), getSheetPermission(savedSheet.getId()));

        redirect.addFlashAttribute("message", "Successfully saved Handout");
        return "redirect:/sheet/list/" + authdAccount.getId();
    }

    public String edit(Long id, ModelMap modelMap) {
        if(!authService.isAuthenticated()){
            return "redirect:/";
        }

        String permission = getSheetPermission(id);
        if(!authService.hasPermission(permission)) {
            return Constants.UNAUTHORIZED_REDIRECT;
        }

        Sheet sheet = sheetRepo.get(id);
        modelMap.put("sheet", sheet);

        return "sheet/edit";
    }

    public String delete(Long id, RedirectAttributes redirect) {
        if(!authService.isAuthenticated()){
            return "redirect:/";
        }

        String permission = getSheetPermission(id);
        if(!authService.hasPermission(permission)) {
            return Constants.UNAUTHORIZED_REDIRECT;
        }

        sheetRepo.delete(id);

        redirect.addFlashAttribute("message", "Successfully deleted Handout");
        Account authdAccount = authService.getAccount();
        return "redirect:/sheet/list/" + authdAccount.getId();
    }

    public String update(Sheet sheet, CommonsMultipartFile sheetImage, RedirectAttributes redirect){

        if(!authService.isAuthenticated()){
            return "redirect:/";
        }

        String permission = getSheetPermission(sheet.getId());
        if(!authService.hasPermission(permission)) {
            return Constants.UNAUTHORIZED_REDIRECT;
        }

        String endpoint = sheet.getEndpoint().replaceAll("[^\\w\\s]", "");
        log.info(endpoint);

        Sheet existingSheet = sheetRepo.getByEndpoint(endpoint);
        if(existingSheet != null &&
                existingSheet.getId() != sheet.getId()){
            redirect.addFlashAttribute("message", "Endpoint exists, please try another without special characters");
            redirect.addAttribute("endpoint", "make");
            redirect.addFlashAttribute("sheet", sheet);
            return "redirect:/sheet/edit/" + sheet.getId();
        }

        if(!Utils.isTestEnvironment(env) &&
                sheetImage != null &&
                sheetImage.getSize() > 0){
            syncSheetImage(sheet, sheetImage);
        }

        sheetRepo.update(sheet);
        return "redirect:/sheet/edit/" + sheet.getId();
    }

    public String getSheets(ModelMap modelMap) {

        if(!authService.isAuthenticated()){
            return "redirect:/";
        }

        if(!authService.isAdministrator()){
            return Constants.UNAUTHORIZED_REDIRECT;
        }

        List<Sheet> sheets = sheetRepo.getSheets();
        modelMap.put("sheets", sheets);

        return "sheet/list";
    }

    public String getUserSheets(Long id, ModelMap modelMap) {

        if(!authService.isAuthenticated()){
            return "redirect:/";
        }

        if(id != authService.getAccount().getId())return Constants.UNAUTHORIZED_REDIRECT;

        List<Sheet> sheets = sheetRepo.getSheets(authService.getAccount().getId());
        modelMap.put("sheets", sheets);

        return "sheet/list";
    }

}
