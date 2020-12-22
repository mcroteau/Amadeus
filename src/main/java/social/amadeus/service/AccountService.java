package social.amadeus.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import social.amadeus.common.Constants;
import social.amadeus.common.Utils;
import social.amadeus.model.Account;
import social.amadeus.model.Post;
import social.amadeus.repository.AccountRepo;
import social.amadeus.repository.PostRepo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AccountService {

    @Autowired
    private SyncService syncService;

    @Autowired
    private AccountRepo accountRepo;

    @Autowired
    private PostRepo postRepo;

    @Autowired
    private AuthService authService;


    private String getAccountPermission(String id){
        return Constants.ACCOUNT_MAINTENANCE + id;
    }

    public Account getAccountInfo(){
        if(!authService.isAuthenticated()){
            Account account = new Account();
            account.setFailMessage(Constants.AUTHENTICATION_REQUIRED);
            return account;
        }
        return authService.getAccount();
    }


    public String getAccounts(ModelMap modelMap){
        if(!authService.isAuthenticated()){
            return "redirect:/";
        }

        if(!authService.isAdministrator()){
            return Constants.UNAUTHORIZED_REDIRECT;
        }

        List<Account> accounts = accountRepo.findAll();
        modelMap.addAttribute("accounts", accounts);

        return "account/index";
    }

    public String getEditAccount(String id, ModelMap modelMap){
        String permission = getAccountPermission(id);
        if(!authService.isAdministrator() &&
                !authService.hasPermission(permission)){
            return "redirect:/";
        }

        Account account = accountRepo.get(Long.parseLong(id));
        modelMap.addAttribute("account", account);

        return "account/edit";
    }

    public String updateAccountGet(String id, ModelMap modelMap){
        String permission = getAccountPermission(id);
        if(!authService.isAdministrator() &&
                !authService.hasPermission(permission)){
            return "redirect:/";
        }

        Account account = accountRepo.get(Long.parseLong(id));
        modelMap.addAttribute("account", account);
        return "account/edit";
    }


    public String updateAccount(CommonsMultipartFile imageFile, Account account, ModelMap modelMap, RedirectAttributes redirect){

        String permission = getAccountPermission(Long.toString(account.getId()));
        if(!authService.isAdministrator() &&
                !authService.hasPermission(permission)){
            redirect.addFlashAttribute("You do not have permission");
            return "redirect:/";
        }

        if(imageFile != null &&
                imageFile.getSize() > 0) {
            try {
                String fileName = Utils.getGenericFileName(imageFile);
                String imageUri = Constants.HTTPS + Constants.DO_ENDPOINT + "/" + fileName;

                syncService.send(fileName, imageFile.getInputStream());
                account.setImageUri(imageUri);
            }catch(Exception ex){
                ex.printStackTrace();
            }
        }

        if(account.getImageUri().equals("")) {
            redirect.addFlashAttribute("message", "please include your profile image");
            return "redirect:/account/edit/" + account.getId();
        }

        accountRepo.update(account);
        Account savedAccount = accountRepo.get(account.getId());

        redirect.addFlashAttribute("message", "account successfully updated");
        modelMap.addAttribute("account", savedAccount);

        return "redirect:/account/edit/" + account.getId();
    }



    public String editPassword(String id, ModelMap modelMap) {

        String permission = getAccountPermission(id);
        if(!authService.isAdministrator() &&
                !authService.hasPermission(permission)){
            return "redirect:/";
        }

        Account account = accountRepo.get(Long.parseLong(id));
        modelMap.addAttribute("account", account);
        return "account/edit_password";
    }


    public String updatePassword(Account account, ModelMap modelMap, RedirectAttributes redirect) {

        String permission = getAccountPermission(Long.toString(account.getId()));
        if(!authService.isAdministrator() &&
                !authService.hasPermission(permission)){
            return "redirect:/";
        }

        if(account.getPassword().length() < 7){
            redirect.addFlashAttribute("error", "Passwords must be at least 7 characters long.");
            return "redirect:/signup";
        }

        if(!account.getPassword().equals("")){
            account.setPassword(Utils.hashit(account.getPassword()));
            accountRepo.updatePassword(account);
        }

        redirect.addFlashAttribute("message", "password successfully updated");
        return "redirect:/signout";

    }

    public String disableAccount(String id, ModelMap modelMap, RedirectAttributes redirect) {
        if(!authService.isAdministrator()){
            redirect.addFlashAttribute("error", "You don't have permission");
            return "redirect:/admin/accounts";
        }

        Account account = accountRepo.get(Long.parseLong(id));
        List<Post> posts = postRepo.getUserPosts(Long.parseLong(id));
        for(Post post : posts){
            postRepo.hide(post.getId());
            postRepo.removePostShares(post.getId());
        }

        account.setDisabled(true);
        account.setDateDisabled(Utils.getDate());
        accountRepo.suspend(account);

        redirect.addFlashAttribute("message", "Successfully disabled account");

        return "redirect:/admin/accounts";
    }

    public String signup(String uri, ModelMap modelMap) {
        authService.signout();
        modelMap.addAttribute("uri", uri);
        return "account/signup";
    }
}
