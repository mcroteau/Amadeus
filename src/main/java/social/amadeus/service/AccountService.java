package social.amadeus.service;

import com.google.gson.Gson;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import social.amadeus.AppStartup;
import social.amadeus.common.Constants;
import social.amadeus.common.Utils;
import social.amadeus.model.*;
import social.amadeus.repository.*;
import xyz.strongperched.Parakeet;

import javax.servlet.http.HttpServletRequest;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
public class AccountService {

    private static final Logger log = Logger.getLogger(AccountService.class);

    Gson gson = new Gson();

    @Autowired
    private AccountRepo accountRepo;

    @Autowired
    private RoleRepo roleRepo;

    @Autowired
    private ObserverRepo observerRepo;

    @Autowired
    private PostRepo postRepo;

    @Autowired
    private NotificationRepo notificationRepo;

    @Autowired
    private MessageRepo messageRepo;

    @Autowired
    private AuthService authService;

    @Autowired
    private PhoneService phoneService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private ReCaptchaService reCaptchaService;

    @Autowired
    S3Service s3Service;

    @Autowired
    private Environment env;

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


    private Account synchronizeImage(CommonsMultipartFile imageFile, Account account){
        try {
            String fileName = Utils.getGenericFileName(imageFile);
            String imageUri = Constants.HTTPS + Constants.DO_ENDPOINT + "/" + fileName;

            s3Service.send(fileName, imageFile.getInputStream());
            account.setImageUri(imageUri);
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return account;
    }

    public List<Notification> getNotifications(Account account){
        List<Notification> notifications = notificationRepo.notifications(account.getId());
        for(Notification notification: notifications){
            Account a = accountRepo.get(notification.getAuthenticatedAccountId());
            notification.setName(a.getName());
        }

        Comparator<Notification> comparator = new Comparator<Notification>() {
            @Override
            public int compare(Notification a1, Notification a2) {
                Long p1 = new Long(a1.getDateCreated());
                Long p2 = new Long(a2.getDateCreated());
                return p2.compareTo(p1);
            }
        };

        Collections.sort(notifications, comparator);

        return notifications;
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

        if(!reCaptchaService.validates(reCaptchaResponse) &&
                !Utils.isTestEnvironment(env)){
            redirect.addFlashAttribute("message", "Did you forget to check the box thing?");
            return "redirect:/signup?uri=" + uri;
        }

        if(!Utils.validMailbox(account.getUsername())){
            redirect.addFlashAttribute("message", "Username must be a valid email.");
            return "redirect:/signup?uri=" + uri;
        }

        Account existingAccount = accountRepo.getByUsername(account.getUsername());
        if(existingAccount != null){
            redirect.addFlashAttribute("message", "Account exists with same username.");
            return "redirect:/signup?uri=" + uri;
        }

        if(account.getName().equals("")){
            redirect.addFlashAttribute("message", "Name must not be blank.");
            return "redirect:/signup?uri=" + uri;
        }

        if(account.getPassword().equals("")) {
            redirect.addFlashAttribute("message", "Password cannot be blank");
            return "redirect:/signup?uri=" + uri;
        }

        if(account.getPassword().length() < 7){
            redirect.addFlashAttribute("message", "Password must be at least 7 characters long.");
            return "redirect:/signup?uri=" + uri;
        }

        String password = account.getPassword();
        String passwordHashed = Parakeet.dirty(account.getPassword());

        try{

            account.setPassword(passwordHashed.toString());
            account.setImageUri(Utils.getProfileImageUri());
            accountRepo.save(account);

            Account savedAccount = accountRepo.getByUsername(account.getUsername());
            Role defaultRole = roleRepo.find(Constants.ROLE_ACCOUNT);

            accountRepo.saveAccountRole(savedAccount.getId(), defaultRole.getId());
            String permission = Constants.ACCOUNT_MAINTENANCE + savedAccount.getId();
            accountRepo.savePermission(savedAccount.getId(), permission);


            String body = "<h1>Amadeus</h1>"+
                    "<p>Thank you for registering! Enjoy!</p>";

            if(!Utils.isTestEnvironment(env)) {
                emailService.send(savedAccount.getUsername(), "Successfully Registered", body);
                phoneService.support("Amadeus : Registration " + account.getName() + " " + account.getUsername());
            }

        }catch(Exception e){
            e.printStackTrace();
            log.info(e.getMessage());
            redirect.addFlashAttribute("message", "Will you contact us? Email us with the subject, support@amadeus.social. Our programmers missed something. Gracias!");
            return("redirect:/signup?uri=" + uri);
        }

        log.info(accountRepo.getCount() + " " + authService + " " + account.getUsername());
        if(!authService.signin(account.getUsername(), password)) {
            redirect.addFlashAttribute("message", "Thank you for registering. We hope you enjoy!");
            return "redirect:/?uri=" + uri;
        }

        req.getSession().setAttribute("account", account);
        req.getSession().setAttribute("imageUri", account.getImageUri());

        return "redirect:/?uri=" + uri;
    }


    public ProfileOutput profile(String id, ModelMap modelMap) {

        ProfileOutput profileOutput = new ProfileOutput();

        if(!authService.isAuthenticated()){
            profileOutput.setStatus(Constants.AUTHENTICATION_REQUIRED);
            return profileOutput;
        }

        Account account = accountRepo.get(Long.parseLong(id));
        Account authdAccount = authService.getAccount();

        if(account.getId() == authdAccount.getId()){
            account.setOwnersAccount(true);
        }

        Observed observed = new Observed();
        observed.setObservedId(Long.parseLong(id));
        observed.setObserverId(authdAccount.getId());
        if(observerRepo.isObserved(observed))
            account.setObserving(true);

        List<Observed> observingList = observerRepo.getObserving(Long.parseLong(id));
        List<Observed> observing = new ArrayList<>();
        for(Observed o: observingList){
            Account a = accountRepo.get(o.getObservedId());
            o.setName(a.getName());
            o.setImageUri(a.getImageUri());
            observing.add(o);
        }

        AccountBlock accountBlock = new AccountBlock();
        accountBlock.setPersonId(authdAccount.getId());
        accountBlock.setBlockerId(account.getId());

        boolean blocked = accountRepo.blocked(accountBlock);
        account.setBlocked(blocked);


        long likes = accountRepo.likes(account.getId());
        if(likes > 0){
            account.setLiked(true);
        }
        account.setLikes(likes);

        if(authdAccount.getId() != Long.parseLong(id)) {
            ProfileView view = new ProfileView.Builder()
                    .profile(account.getId())
                    .viewer(authdAccount.getId())
                    .date(Utils.getDate())
                    .build();

            accountRepo.incrementViews(view);
        }

        profileOutput.setStatus(Constants.SUCCESS);
        profileOutput.setProfile(account);
        profileOutput.setObserving(observing);


        return profileOutput;

    }

    public String sendReset(String username, RedirectAttributes redirect, HttpServletRequest req) {

        try {

            Account account = accountRepo.getByUsername(username);
            if (account == null) {
                redirect.addFlashAttribute("message", "Unable to find account.");
                return ("redirect:/account/reset");
            }

            String resetUuid = Utils.getRandomString(13);
            account.setUuid(resetUuid);
            accountRepo.updateUuid(account);

            StringBuffer url = req.getRequestURL();

            String[] split = url.toString().split("/o/");
            String httpSection = split[0];

            String resetUrl = httpSection + "/o/account/confirm_reset?";
            String params = "username=" + URLEncoder.encode(account.getUsername(), "utf-8") + "&uuid=" + resetUuid;
            resetUrl += params;

            String body = "<h1>Amadeus</h1>" +
                    "<p>Reset Password :" +
                    "<a href=\"" + resetUrl + "\">" + resetUrl + "</a></p>";

            emailService.send(account.getUsername(), "Reset Password", body);

        }catch(Exception e){
            e.printStackTrace();
        }

        return "account/send_reset";
    }

    public String resetView(String uuid, String username, ModelMap modelMap,RedirectAttributes redirect) {

        Account account = accountRepo.getByUsernameAndUuid(username, uuid);
        if (account == null) {
            redirect.addFlashAttribute("error", "Unable to locate account.");
            return "redirect:/account/reset";
        }

        modelMap.addAttribute("account", account);
        return "account/confirm";
    }

    public String resetPassword(Account account, ModelMap modelMap, RedirectAttributes redirect) {

        if(account.getPassword().length() < 7){
            redirect.addFlashAttribute("account", account);
            redirect.addFlashAttribute("message", "Passwords must be at least 7 characters long.");
            return "redirect:/account/confirm?username=" + account.getUsername() + "&uuid=" + account.getUuid();
        }

        if(!account.getPassword().equals("")){
            String password = Utils.dirty(account.getPassword());
            account.setPassword(password);
            accountRepo.updatePassword(account);
        }

        redirect.addFlashAttribute("message", "Password successfully updated");
        return "account/success";
    }

    public Map<String, Object> heart(String id) {

        Map<String, Object> respData = new HashMap<>();

        if(!authService.isAuthenticated()){
            respData.put("error", Constants.AUTHENTICATION_REQUIRED);
            return respData;
        }

        Account account = authService.getAccount();

        ProfileLike profileLike = new ProfileLike();
        profileLike.setLikerId(account.getId());
        profileLike.setProfileId(Long.parseLong(id));
        profileLike.setDateLiked(Utils.getDate());

        boolean existingProfileLike = accountRepo.liked(profileLike);

        if(existingProfileLike) {
            accountRepo.unlike(profileLike);
        }else{
            accountRepo.like(profileLike);
        }

        long likes = accountRepo.likes(Long.parseLong(id));

        respData.put("id", id);
        respData.put("success", "true");
        respData.put("likes", likes);

        return respData;
    }

    public Map<String, Object> blockUser(String id) {

        Map<String, Object> respData = new HashMap<String, Object>();

        if(!authService.isAuthenticated()){
            respData.put("error", Constants.AUTHENTICATION_REQUIRED);
            return respData;
        }

        Account account = authService.getAccount();
        if(account.getId() == Long.parseLong(id)){
            respData.put("error", "cannot block yourself b");
            return respData;
        }

        AccountBlock blok = new AccountBlock.Builder()
                .forPerson(Long.parseLong(id))
                .byBlocker(account.getId())
                .atDateBlocked(Utils.getDate())
                .build();

        if(accountRepo.blocked(blok)){
            accountRepo.unblock(blok);
        }else{
            accountRepo.block(blok);
        }

        respData.put("success", true);
        return respData;
    }


    public Map<String, Object> getProfileData(HttpServletRequest request) {
        Map<String, Object> respData = new HashMap<>();

        if(!authService.isAuthenticated()){
            respData.put("error", Constants.AUTHENTICATION_REQUIRED);
            return respData;
        }

        Account account = authService.getAccount();
        long newestCount = 0;
        if (request.getSession().getAttribute(Constants.ACTIVITY_REQUEST_TIME) != null) {
            long start = (Long) request.getSession().getAttribute(Constants.ACTIVITY_REQUEST_TIME);
            long end = Utils.getDate();
            newestCount = postRepo.getNewestCount(start, end, account.getId());
        }

        List<Notification> notifications = getNotifications(account);
        long messagesCount = messageRepo.countByAccount(account);
        long notificationsCount = notificationRepo.countByAccount(account);

        respData.put("newestCount", newestCount);
        respData.put("messagesCount", messagesCount);
        respData.put("notifications", notifications);
        respData.put("notificationsCount", notificationsCount);

        return respData;
    }


    public Map<String, Object> viewsData() {

        Map<String, Object> respData = new HashMap<String, Object>();

        if(!authService.isAuthenticated()){
            respData.put("error", Constants.AUTHENTICATION_REQUIRED);
            return respData;
        }

        Account account = authService.getAccount();

        long end = Utils.getDate();

        List<String> labels = new ArrayList<>();
        List<Long> counts = new ArrayList<>();

        int day = 31;
        long currentDate = Utils.getYesterday(day);

        for(int n = 32; n != 0; n--){
            long date = Utils.getYesterday(n);
            long count = accountRepo.getViews(account, date, currentDate);

            String datef = Utils.getGraphDate(n);
            labels.add(datef);
            counts.add(count);

            day--;
            currentDate = Utils.getYesterday(day);
        }


        long weekCount = accountRepo.getViews(account, Utils.getYesterday(7), end);
        long monthCount = accountRepo.getViews(account, Utils.getYesterday(31), end);
        long allTimeCount = accountRepo.getAllViews(account);

        respData.put("labels", labels);
        respData.put("counts", counts);
        respData.put("week", weekCount);
        respData.put("month", monthCount);
        respData.put("all", allTimeCount);

        return respData;
    }


    public String suspendAccount(String id, ModelMap modelMap, RedirectAttributes redirect) {

        if(!authService.isAdministrator()){
            redirect.addFlashAttribute("message", "You don't have permission to do this!");
            return "redirect:/account/profile/" + id;
        }

        Account account = accountRepo.get(Long.parseLong(id));
        account.setDateDisabled(Utils.getDate());
        accountRepo.suspend(account);

        modelMap.addAttribute("message", "Account suspended.");
        return "redirect:/account/edit/" + id;
    }


    public String renewAccount(String id, ModelMap modelMap, RedirectAttributes redirect) {
        if(!authService.isAdministrator()){
            redirect.addFlashAttribute("message", "You don't have permission to do this!");
            return "redirect:/account/profile/" + id;
        }

        Account account = accountRepo.get(Long.parseLong(id));
        accountRepo.renew(account);

        modelMap.addAttribute("message", "Account renewed.");
        return "redirect:/account/edit/" + id;
    }
}
