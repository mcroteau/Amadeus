package social.amadeus.service;

import com.google.gson.Gson;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import social.amadeus.common.Constants;
import social.amadeus.repository.AccountRepo;
import social.amadeus.model.Account;
import xyz.strongperched.Parakeet;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;

@Service
public class AuthService {

    private static final Logger log = Logger.getLogger(AuthService.class);

    Gson gson = new Gson();

//    @Autowired
//    private Parakeet parakeet;

    @Autowired
    private AccountRepo accountRepo;

    public boolean signin(String username, String password){
        return Parakeet.login(username, password);
    }

    public boolean signout(){
        return Parakeet.logout();
    }

    public boolean isAuthenticated(){
        return Parakeet.isAuthenticated();
    }

    public boolean isAdministrator(){
        return Parakeet.hasRole(Constants.ROLE_ADMIN);
    }

    public boolean hasPermission(String permission){
        return Parakeet.hasPermission(permission);
    }

    public boolean hasRole(String role){
        return Parakeet.hasRole(role);
    }

    public Account getAccount(){
        String username = Parakeet.getUser();
        Account account = accountRepo.getByUsername(username);
        return account;
    }

    public String authenticateUser(String uri, Account account, RedirectAttributes redirect, HttpServletRequest request) {

        try{
            if(account == null) {
                String payload = IOUtils.toString(request.getInputStream(), StandardCharsets.UTF_8);
                account = gson.fromJson(payload, Account.class);
            }

            if(!signin(account.getUsername(), account.getPassword())){
                redirect.addFlashAttribute("message", "Wrong username and password");
                return "redirect:/signin";
            }

            Account sessionAccount = accountRepo.getByUsername(account.getUsername());

            request.getSession().setAttribute("account", sessionAccount);
            request.getSession().setAttribute("imageUri", sessionAccount.getImageUri());

            if(uri != null &&
                    !uri.equals("")) {
                return "redirect:/action?uri=" + uri;
            }

        } catch ( Exception e ) {
            e.printStackTrace();
            redirect.addFlashAttribute("message", "Please yell at one of us, something is a little off.");
            return "redirect:/signin";
        }

        return "redirect:/signin";
    }

    public String unAuthenticateUser(RedirectAttributes redirect, HttpServletRequest request) {
        signout();
        redirect.addFlashAttribute("message", "Successfully signed out");
        request.getSession().setAttribute("account", "");
        request.getSession().setAttribute("imageUri", "");
        return "redirect:/";
    }
}
