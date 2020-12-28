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

        String endpoint = StringEscapeUtils.escapeJava(sheet.getEndpoint());
        log.info(endpoint);

        if(sheetRepo.getByEndpoint(endpoint)){
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

        redirect.addFlashAttribute("message", "Successfully saved Sheet");
        return "redirect:/sheet/list/" + savedSheet.getId();
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

        redirect.addFlashAttribute("message", "Successfully deleted Sheet");
        Account authdAccount = authService.getAccount();
        return "redirect:/sheet/list/" + authdAccount.getId();
    }

    public String update(Sheet sheet, CommonsMultipartFile sheetImage, RedirectAttributes redirect){

        String permission = getSheetPermission(sheet.getId());
        if(!authService.hasPermission(permission)) {
            return Constants.UNAUTHORIZED_REDIRECT;
        }

        String endpoint = StringEscapeUtils.escapeJava(sheet.getEndpoint());
        log.info(endpoint);

        if(sheetRepo.getByEndpoint(endpoint)){
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

}
