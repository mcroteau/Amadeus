package social.amadeus.service;

import com.google.gson.Gson;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import social.amadeus.common.Constants;
import social.amadeus.common.Utils;
import social.amadeus.model.Account;
import social.amadeus.model.Post;
import social.amadeus.model.Role;
import social.amadeus.repository.AccountRepo;
import social.amadeus.repository.FriendRepo;
import social.amadeus.repository.PostRepo;
import social.amadeus.repository.RoleRepo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AccountService {

    Gson gson = new Gson();

    @Autowired
    private SyncService syncService;

    @Autowired
    private AccountRepo accountRepo;

    @Autowired
    private RoleRepo roleRepo;

    @Autowired
    private FriendRepo friendRepo;

    @Autowired
    private PostRepo postRepo;

    @Autowired
    private AuthService authService;

    @Autowired
    private PhoneService phoneService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private ReCaptchaService reCaptchaService;


    private String getAccountPermission(String id){
        return Constants.ACCOUNT_MAINTENANCE + id;
    }

    private Account hydrateAccount(HttpServletRequest req) {
        try {
            String payload = IOUtils.toString(req.getInputStream(), StandardCharsets.UTF_8);
            return gson.fromJson(payload, Account.class);
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return null;
    }

    private void preloadConnections(Account authenticatedAccount){
        Account adminAccount = accountRepo.findByUsername(Constants.ADMIN_USERNAME);
        friendRepo.saveConnection(adminAccount.getId(), authenticatedAccount.getId(), Utils.getDate());
    }

    private Account synchronizeImage(CommonsMultipartFile imageFile, Account account){
        try {
            String fileName = Utils.getGenericFileName(imageFile);
            String imageUri = Constants.HTTPS + Constants.DO_ENDPOINT + "/" + fileName;

            syncService.send(fileName, imageFile.getInputStream());
            account.setImageUri(imageUri);
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return account;
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
            synchronizeImage(imageFile, account);
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
            account.setPassword(Utils.dirty(account.getPassword()));
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


    public String register(String uri, String reCaptchaResponse, Account account, HttpServletRequest req, RedirectAttributes redirect) {

        if(account == null)hydrateAccount(req);

        if(account == null){
            redirect.addFlashAttribute("message", "a error on our end, please give it another go.");
            return "redirect:/signup?uri=" + uri;
        }

        if(!reCaptchaService.validates(reCaptchaResponse)){
            redirect.addFlashAttribute("account", account);
            redirect.addFlashAttribute("message", "Please be a valid human... check the box?");
            return "redirect:/signup?uri=" + uri;
        }

        if(!Utils.validMailbox(account.getUsername())){
            redirect.addFlashAttribute("account", account);
            redirect.addFlashAttribute("message", "Username must be a valid email.");
            return "redirect:/signup?uri=" + uri;
        }

        Account existingAccount = accountRepo.findByUsername(account.getUsername());
        if(existingAccount != null){
            redirect.addFlashAttribute("account", account);
            redirect.addFlashAttribute("message", "Account exists with same username.");
            return "redirect:/signup?uri=" + uri;
        }

        if(account.getName().equals("")){
            redirect.addFlashAttribute("account", account);
            redirect.addFlashAttribute("message", "Name must not be blank.");
            return "redirect:/signup?uri=" + uri;
        }

        if(account.getPassword().equals("")) {
            redirect.addFlashAttribute("account", account);
            redirect.addFlashAttribute("message", "Password cannot be blank");
            return "redirect:/signup?uri=" + uri;
        }

        if(account.getPassword().length() < 7){
            redirect.addFlashAttribute("account", account);
            redirect.addFlashAttribute("message", "Password must be at least 7 characters long.");
            return "redirect:/signup?uri=" + uri;
        }

        String password = account.getPassword();
        String passwordHashed = Utils.dirty(account.getPassword());

        try{

            account.setPassword(passwordHashed.toString());
            account.setImageUri(Utils.getProfileImageUri());
            accountRepo.save(account);

            Account savedAccount = accountRepo.findByUsername(account.getUsername());
            preloadConnections(savedAccount);

            Role defaultRole = roleRepo.find(Constants.ROLE_ACCOUNT);

            accountRepo.saveAccountRole(savedAccount.getId(), defaultRole.getId());
            accountRepo.savePermission(savedAccount.getId(), "account:maintenance:" + savedAccount.getId());


            String body = "<h1>Amadeus</h1>"+
                    "<p>Thank you for registering! Enjoy!</p>";

            emailService.send(savedAccount.getUsername(), "Successfully Registered", body);

            phoneService.support("Amadeus : Registration " + account.getName() + " " + account.getUsername());

        }catch(Exception e){
            e.printStackTrace();
            redirect.addFlashAttribute("account", account);
            redirect.addFlashAttribute("error", "Will you contact us? Email us with the subject, support@amadeus.social. Our programmers missed something. Gracias!");
            return("redirect:/signup?uri=" + uri);
        }


        if(!authService.signin(account.getUsername(), password)) {
            redirect.addFlashAttribute("message", "Thank you for registering. We hope you enjoy!");
            return "redirect:/?uri=" + uri;
        }

        req.getSession().setAttribute("account", account);
        req.getSession().setAttribute("imageUri", account.getImageUri());

        return "redirect:/?uri=" + uri;
    }


}
