package social.amadeus.service;

import com.google.gson.Gson;
import io.github.mcroteau.Parakeet;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import social.amadeus.common.Constants;
import social.amadeus.repository.AccountRepo;
import social.amadeus.model.Account;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;

public class AuthService {

    private static final Logger log = Logger.getLogger(AuthService.class);

    Gson gson = new Gson();

    @Autowired
    private Parakeet parakeet;

    @Autowired
    private AccountRepo accountRepo;

    public boolean signin(String username, String password){
        return parakeet.login(username, password);
    }

    public boolean signout(){
        return parakeet.logout();
    }

    public boolean isAuthenticated(){
        return parakeet.isAuthenticated();
    }

    public boolean isAdministrator(){
        return parakeet.hasRole(Constants.ROLE_ADMIN);
    }

    public boolean hasPermission(String permission){
        return parakeet.hasPermission(permission);
    }

    public boolean hasRole(String role){
        return parakeet.hasRole(role);
    }

    public Account getAccount(){
        String username = parakeet.getUser();
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
                return "redirect:/";
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
            return "redirect:/";
        }

        return "redirect:/";
    }

    public String unAuthenticateUser(RedirectAttributes redirect, HttpServletRequest request) {
        signout();
        redirect.addFlashAttribute("message", "Successfully signed out");
        request.getSession().setAttribute("account", "");
        request.getSession().setAttribute("imageUri", "");
        return "redirect:/";
    }
}
