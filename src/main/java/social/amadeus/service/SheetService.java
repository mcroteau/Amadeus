package social.amadeus.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import social.amadeus.common.Constants;
import social.amadeus.model.Sheet;
import social.amadeus.repository.SheetRepo;

public class SheetService {

    @Autowired
    SheetRepo sheetRepo;

    @Autowired
    AuthService authService;

    public String save(Sheet sheet, RedirectAttributes redirect) {
        if(!authService.isAuthenticated()){
            Constants.AUTHENTICATION_REQUIRED
        }
        Sheet savedSheet = sheetRepo.save(sheet);

        redirect.addFlashAttribute("message", "Successfully saved Sheet");
        return "redirect:/sheet/list/" + savedSheet.getId();
    }

    public String edit(long id, ModelMap modelMap, RedirectAttributes redirect) {
        if(!authService.isAuthenticated()){

        }

    }
}
