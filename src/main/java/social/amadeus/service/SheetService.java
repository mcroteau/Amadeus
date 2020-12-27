package social.amadeus.service;

import org.springframework.beans.factory.annotation.Autowired;
import social.amadeus.common.Constants;
import social.amadeus.model.Sheet;
import social.amadeus.repository.SheetRepo;

public class SheetService {

    @Autowired
    SheetRepo sheetRepo;

    @Autowired
    AuthService authService;

    public Sheet save(Sheet sheet) {
        if(!authService.isAuthenticated()){
            sheet.setStatus(Constants.AUTHENTICATION_REQUIRED);
            return sheet;
        }
        Sheet savedSheet = sheetRepo.save(sheet);
        savedSheet.setStatus(Constants.SUCCESS);
        return savedSheet;
    }
}
