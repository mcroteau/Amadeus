package social.amadeus.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import social.amadeus.model.Account;

@Service
public class PortalService {

    @Autowired
    private AuthService authService;

    public String z(){

        if(!authService.isAuthenticated()){
            return "redirect:/home";
        }

        Account account = authService.getAccount();
        if(account.isDisabled()) {
            return "redirect:/account/edit/" + account.getId();
        }

        return "portal";
    }
}
