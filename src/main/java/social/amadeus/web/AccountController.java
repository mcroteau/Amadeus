package social.amadeus.web;

import com.google.gson.Gson;
import org.apache.log4j.Logger;
import org.apache.commons.io.IOUtils;
import java.nio.charset.StandardCharsets;
import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import javax.servlet.http.HttpServletRequest;

import org.springframework.ui.ModelMap;
import java.net.URLEncoder;
import java.util.*;

import io.github.mcroteau.Parakeet;
import social.amadeus.common.Constants;
import social.amadeus.common.Utils;
import social.amadeus.repository.*;
import social.amadeus.model.*;
import social.amadeus.service.*;


@Controller
public class AccountController {

	private static final Logger log = Logger.getLogger(AccountController.class);

	Gson gson = new Gson();


	@Autowired
	private Parakeet parakeet;

	@Autowired
	private Utils utils;

	@Autowired
	private AccountRepo accountRepo;

	@Autowired
	private RoleRepo roleRepo;

	@Autowired
	private FriendRepo friendRepo;

	@Autowired
	private MusicRepo musicRepo;

	@Autowired
	private MessageRepo messageRepo;

	@Autowired
	private PostRepo postRepo;

	@Autowired
	private NotificationRepo notificationRepo;

	@Autowired
	private EmailService emailService;

	@Autowired
	private PhoneService phoneService;

	@Autowired
	private ReCaptchaService reCaptchaService;

	@Autowired
	private AuthService authService;

	@Autowired
	private AccountService accountService;


    @GetMapping(value="/account/info", produces="application/json")
    public @ResponseBody String getAccountInfo(){
    	return gson.toJson(accountService.getAccountInfo());
    }


	@GetMapping(value="/admin/account/list")
	public String getAccounts(ModelMap modelMap){
		return accountService.getAccounts(modelMap);
	}


	@GetMapping(value="/account/edit/{id}")
	public String getEditAccount(ModelMap modelMap,
								 @PathVariable String id){
		return accountService.getEditAccount(id, modelMap);
	}


	@GetMapping(value="/account/update/{id}")
	public String updateAccountGet(ModelMap modelMap,
								   @PathVariable String id){
		return accountService.updateAccountGet(id, modelMap);
    }



	@PostMapping(value="/account/update/{id}")
	public String update(ModelMap modelMap,
						 RedirectAttributes redirect,
						 @ModelAttribute("account") Account account,
						 @RequestParam(value="image", required=false) CommonsMultipartFile imageFile){
		return accountService.updateAccount(imageFile, account, modelMap, redirect);
	}


	@GetMapping(value="/account/edit_password/{id}")
	public String editPassword(ModelMap modelMap,
	                     	   @PathVariable String id){
		return accountService.editPassword(id, modelMap);
	}


	@PostMapping(value="/account/update_password/{id}")
	public String updatePassword(ModelMap modelMap,
								 RedirectAttributes redirect,
								 @ModelAttribute("account") Account account){
    	return accountService.updatePassword(account, modelMap, redirect);
	}

	@PostMapping(value="/account/delete/{id}")
	public String disableAccount(ModelMap modelMap,
								 RedirectAttributes redirect,
								 @PathVariable String id) {
		return accountService.disableAccount(id, modelMap, redirect);
	}
	
	@GetMapping(value="/signup")
	public String signup(ModelMap modelMap,
						 @RequestParam(value="uri", required = false ) String uri){
		return accountService.signup(uri, modelMap);
	}
	

	@RequestMapping(value="/register", method=RequestMethod.POST)
	protected String register(HttpServletRequest req,
							  @ModelAttribute("account") Account account,
							  @RequestParam(value="g-recaptcha-response", required = true ) String reCaptchaResponse,
							  @RequestParam(value="uri", required = false ) String uri,
							  RedirectAttributes redirect){

		if(!reCaptchaService.validates(reCaptchaResponse)){
			redirect.addFlashAttribute("account", account);
			redirect.addFlashAttribute("error", "Please be a valid human... check the box?");
			return "redirect:/signup?uri=" + uri;
		}

		if(!utils.validEmail(account.getUsername())){
			redirect.addFlashAttribute("account", account);
			redirect.addFlashAttribute("error", "Username must be a valid email.");
			return "redirect:/signup?uri=" + uri;
		}

		Account existingAccount = accountRepo.findByUsername(account.getUsername());
		if(existingAccount != null){
			redirect.addFlashAttribute("account", account);
			redirect.addFlashAttribute("error", "Account exists with same username.");
			return "redirect:/signup?uri=" + uri;
		}

		if(account.getName().equals("")){
			redirect.addFlashAttribute("account", account);
			redirect.addFlashAttribute("error", "Name must not be blank.");
			return "redirect:/signup?uri=" + uri;
		}
		
		if(account.getPassword().equals("")) {
			redirect.addFlashAttribute("account", account);
			redirect.addFlashAttribute("error", "Password cannot be blank");
			return "redirect:/signup?uri=" + uri;
		}

		if(account.getPassword().length() < 7){
			redirect.addFlashAttribute("account", account);
			redirect.addFlashAttribute("error", "Password must be at least 7 characters long.");
			return "redirect:/signup?uri=" + uri;
		}

		String password = account.getPassword();
		String passwordHashed = utils.hash(account.getPassword());

        try{

			account.setPassword(passwordHashed.toString());
			account.setImageUri(Constants.DEFAULT_IMAGE_URI);
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
        	redirect.addFlashAttribute("error", "Will you contact us? Email us with the subject, Please Fix. support@amadeus.social. Our programmers missed something. Gracias");
        	return("redirect:/signup?uri=" + uri);
        }


        if(parakeet.login(account.getUsername(), password)) {

			req.getSession().setAttribute("account", account);
			req.getSession().setAttribute("imageUri", account.getImageUri());

			return "redirect:/?uri=" + uri;

		}else{
			redirect.addFlashAttribute("message", "Thank you for registering. Enjoy");
			return "redirect:/?uri=" + uri;
		}
	}

	

	@RequestMapping(value="/register_mobile", method=RequestMethod.POST, consumes="application/json")
	protected @ResponseBody String registerMobile(
							 @RequestBody Account account,
							  @RequestParam(value="name", required = false ) String name,
							  @RequestParam(value="email", required = false ) String email,
							  @RequestParam(value="password", required = false ) String password,
							   HttpServletRequest request){


		Map<String, String> data = new HashMap<String, String>();
		
		try{

			if(account == null){
				String payload = IOUtils.toString(request.getInputStream(), StandardCharsets.UTF_8);
		    	account = gson.fromJson(payload, Account.class);
			}

		}catch(Exception e){
			data.put("error", "IO error");
			return gson.toJson(data);
		}

		if(!utils.validEmail(account.getUsername())){
			data.put("error", "Username must be a valid email");
		}

		if(account.getUsername().contains(" ")){
			data.put("error", "Username contains spaces, no spaces are allowed");
		}

		if(account.getName().equals("")){
			data.put("error", "Name cannot be blank.");
		}
		
		if(account.getPassword().equals("")) {
			data.put("error", "Password cannot be blank.");
		}

		if(account.getPassword().length() < 7){
			data.put("error", "Passwords must be at least 7 characters long.");
		}

		String passwordHashed = utils.hash(account.getPassword());

        try{

			account.setPassword(passwordHashed.toString());
			account.setImageUri(Constants.DEFAULT_IMAGE_URI);
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
			data.put("error",  "Will you contact us? Email us with the subject, Please Fix. support@amadeus.social. Our programmers missed something. Gracias");
        }

		data.put("success", "true");
		return gson.toJson(data);
	}



	@RequestMapping(value="/profile/{id}", method=RequestMethod.GET, produces="application/json")
	public @ResponseBody String profile(ModelMap model,
						  final RedirectAttributes redirect,
						  @PathVariable String id){

		Map<String, Object> data = new HashMap<String, Object>();

		if(!authService.isAuthenticated()){
			data.put("error", "Authentication required");
			return gson.toJson(data);
		}

		Account account = accountRepo.get(Long.parseLong(id));
		Account authenticatedAccount = authService.getAccount();

		if(account.getId() == authenticatedAccount.getId()){
			account.setOwnersAccount(true);
		}

		boolean ifFriend = friendRepo.isFriend(authenticatedAccount.getId(), account.getId());
		if(ifFriend){
			account.setIsFriend(true);
		}

		AccountBlock accountBlock = new AccountBlock();
		accountBlock.setPersonId(authenticatedAccount.getId());
		accountBlock.setBlockerId(account.getId());

		boolean blocked = accountRepo.blocked(accountBlock);
		account.setBlocked(blocked);

		List<Friend> friends = friendRepo.getFriends(Long.parseLong(id));
        for(Friend friend : friends){
            if(messageRepo.hasMessages(friend.getFriendId(), authService.getAccount().getId()))
                friend.setHasMessages(true);
        }

		long likes = accountRepo.likes(account.getId());
        if(likes > 0){
        	account.setLiked(true);
		}
		account.setLikes(likes);

        data.put("profile", account);
        data.put("friends", friends);

        if(authenticatedAccount.getId() != Long.parseLong(id)) {
			ProfileView view = new ProfileView.Builder()
					.profile(account.getId())
					.viewer(authenticatedAccount.getId())
					.date(utils.getCurrentDate())
					.build();

			accountRepo.incrementViews(view);
		}

		return gson.toJson(data);
	}

	@RequestMapping(value="/account/reset", method=RequestMethod.GET)
	public String reset(){
		return "account/reset";
	}

	@RequestMapping(value="/account/send_reset", method=RequestMethod.POST)
	public String sendReset(HttpServletRequest request,
							final RedirectAttributes redirect,
				    			@RequestParam(value="username", required = true ) String username){


		try {
			Account account = accountRepo.findByUsername(username);

			if (account == null) {
				redirect.addFlashAttribute("error", "Unable to find account.");
				return ("redirect:/account/reset");
			}

			String resetUuid = utils.generateRandomString(13);
			account.setUuid(resetUuid);
			accountRepo.updateUuid(account);

			StringBuffer url = request.getRequestURL();

			String[] split = url.toString().split("/b/");
			String httpSection = split[0];

			String resetUrl = httpSection + "/b/account/confirm_reset?";

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


	@RequestMapping(value="/account/confirm_reset", method=RequestMethod.GET)
	public String reset(ModelMap model,
						final RedirectAttributes redirect,
						@RequestParam(value="username", required = true ) String username,
						@RequestParam(value="uuid", required = true ) String uuid){

		Account account = accountRepo.findByUsernameAndUuid(username, uuid);

		if (account == null) {
			redirect.addFlashAttribute("error", "Unable to find account.");
			return ("redirect:/account/reset");
		}
		model.addAttribute("account", account);

		return "account/confirm";
	}



	@RequestMapping(value="/account/reset/{id}", method=RequestMethod.POST)
	public String resetPassword(@ModelAttribute("account") Account account,
								 ModelMap model,
								 HttpServletRequest request,
								 final RedirectAttributes redirect){

		if(account.getPassword().length() < 7){
			redirect.addFlashAttribute("account", account);
			redirect.addFlashAttribute("error", "Passwords must be at least 7 characters long.");
			return "redirect:/account/confirm?username=" + account.getUsername() + "&uuid=" + account.getUuid();
		}

		if(!account.getPassword().equals("")){
			String password = utils.hash(account.getPassword());
			account.setPassword(password);
			accountRepo.updatePassword(account);
		}

		redirect.addFlashAttribute("message", "Password successfully updated");
		return "account/success";

	}


	@RequestMapping(value="/account/guest", method=RequestMethod.GET)
	public String guest(HttpServletRequest request){

		try {

			if(parakeet.login(Constants.GUEST_USERNAME, Constants.GUEST_PASSWORD)) {
				Account sessionAccount = authService.getAccount();
//				parakeet.store("account", sessionAccount);
//				parakeet.store("imageUri", sessionAccount.getImageUri());

				request.getSession(false).setAttribute("account", sessionAccount);
				request.getSession(false).setAttribute("imageUri", sessionAccount.getImageUri());
			}
			//phoneService.support("Zq:" + request.getRemoteHost());

		}catch(Exception e){ }

		return "redirect:/";
	}


	private void preloadConnections(Account authenticatedAccount){
		Account adminAccount = accountRepo.findByUsername(Constants.ADMIN_USERNAME);
		friendRepo.saveConnection(adminAccount.getId(), authenticatedAccount.getId(), utils.getCurrentDate());
	}


	@RequestMapping(value="/profile/like/{id}", method=RequestMethod.POST,  produces="application/json")
	public @ResponseBody String like(ModelMap model,
									 HttpServletRequest request,
									 final RedirectAttributes redirect,
									 @PathVariable String id){

		Gson gson = new Gson();
		Map<String, Object> responseData = new HashMap<String, Object>();

		if(!authService.isAuthenticated()){
			responseData.put("error", "authentication required");
			return gson.toJson(responseData);
		}

		Account account = authService.getAccount();

		ProfileLike profileLike = new ProfileLike();
		profileLike.setLikerId(account.getId());
		profileLike.setProfileId(Long.parseLong(id));
		profileLike.setDateLiked(utils.getCurrentDate());

		boolean result = false;

		boolean existingProfileLike = accountRepo.liked(profileLike);

		if(existingProfileLike) {
			responseData.put("action", "removed");
			result = accountRepo.unlike(profileLike);
		}
		else{
			result = accountRepo.like(profileLike);
			responseData.put("action", "added");
		}

		long likes = accountRepo.likes(Long.parseLong(id));


		responseData.put("success", result);
		responseData.put("likes", likes);
		responseData.put("id", Long.parseLong(id));

		return gson.toJson(responseData);
	}



	@RequestMapping(value="/account/block/{id}", method=RequestMethod.POST,  produces="application/json")
	public @ResponseBody String blockUser(ModelMap model,
									 HttpServletRequest request,
									 final RedirectAttributes redirect,
									 @PathVariable String id) {
		Gson gson = new Gson();
		Map<String, Object> resp = new HashMap<String, Object>();

		if(!authService.isAuthenticated()){
			resp.put("error", "authentication required");
			return gson.toJson(resp);
		}

		Account account = authService.getAccount();

		if(account.getId() == Long.parseLong(id)){
			resp.put("error", "cannot block yourself b");
			return gson.toJson(resp);
		}


		AccountBlock blok = new AccountBlock.Builder()
				.forPerson(Long.parseLong(id))
				.byBlocker(account.getId())
				.atDateBlocked(utils.getCurrentDate())
				.build();

		if(accountRepo.blocked(blok)){
			resp.put("success", "unblocked");
			accountRepo.unblock(blok);
		}else{
			resp.put("success", "blocked");
			accountRepo.block(blok);
		}

		return gson.toJson(resp);
	}


	@RequestMapping(value="/profile/data", method=RequestMethod.GET, produces="application/json")
	public @ResponseBody String data(ModelMap model,
									 HttpServletRequest request,
									 final RedirectAttributes redirect){

    	Map<String, Object> data = new HashMap<String, Object>();

		if(!authService.isAuthenticated()){
			data.put("error", "authentication required");
			return gson.toJson(data);
		}

		Account account = authService.getAccount();

		//remove
//		List<Post> latestPostsTiny = getLatestPostsSkinny(account, request);

		long newestCount = 0;
		if (request.getSession().getAttribute(Constants.ACTIVITY_REQUEST_TIME) != null) {
			long start = (Long) request.getSession().getAttribute(Constants.ACTIVITY_REQUEST_TIME);
			long end = utils.getCurrentDate();
			newestCount = postRepo.getNewestCount(start, end, account.getId());
		}

		List<Notification> notifications = getNotifications(account);
		long messagesCount = messageRepo.countByAccount(account);
		long notificationsCount = notificationRepo.countByAccount(account);
		long invitationsCount = friendRepo.countInvitesByAccount(account);

		List<FriendInvite> invites = friendRepo.invites(account.getId());
		notificationsCount = notificationsCount + invites.size();

		data.put("newestCount", newestCount);
		data.put("messagesCount", messagesCount);
		data.put("notifications", notifications);
		data.put("notificationsCount", notificationsCount);
		data.put("invitationsCount", invitationsCount);

    	return gson.toJson(data);
	}

	private List<Notification> getNotifications(Account account){
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

		List<FriendInvite> invites = friendRepo.invites(account.getId());
		for(FriendInvite invite : invites){
			Notification notification = new Notification();
			notification.setInvite(true);
			Account invitee = accountRepo.get(invite.getInviteeId());
			notification.setName(invitee.getName());
			notification.setDateCreated(invite.getDateCreated());
			notifications.add(notification);
		}

		Collections.sort(notifications, comparator);

		return notifications;
	}


	@RequestMapping(value="/profile/data/views", method=RequestMethod.GET, produces="application/json")
	public @ResponseBody String views(ModelMap model,
										final RedirectAttributes redirect){
		Map<String, Object> viewsData = new HashMap<String, Object>();

		if(!authService.isAuthenticated()){
			viewsData.put("error", "Authentication required");
			return gson.toJson(viewsData);
		}

		Account account = authService.getAccount();

		long end = utils.getCurrentDate();

		List<String> labels = new ArrayList<String>();
		List<Long> counts = new ArrayList<Long>();
		;
		int day = 31;
		long currentDate = utils.getPreviousDay(day);

		for(int n = 32; n != 0; n--){
			long date = utils.getPreviousDay(n);
			long count = accountRepo.getViews(account, date, currentDate);

			String datef = utils.getGraphDate(n);
			labels.add(datef);
			counts.add(count);

			day--;
			currentDate = utils.getPreviousDay(day);
		}


		long weekCount = accountRepo.getViews(account, utils.getPreviousDay(7), end);
		long monthCount = accountRepo.getViews(account, utils.getPreviousDay(31), end);
		long allTimeCount = accountRepo.getAllViews(account);

		viewsData.put("labels", labels);
		viewsData.put("counts", counts);
		viewsData.put("week", weekCount);
		viewsData.put("month", monthCount);
		viewsData.put("all", allTimeCount);

		return gson.toJson(viewsData);
	}


	@RequestMapping(value="/account/suspend/{id}", method=RequestMethod.POST)
	public String suspend(ModelMap model,
					   final RedirectAttributes redirect,
					   @PathVariable String id){
		if(!authService.isAdministrator()){
			redirect.addFlashAttribute("message", "You don't have permission to do this!");
			return "redirect:/account/profile/" + id;
		}

		Account account = accountRepo.get(Long.parseLong(id));
		account.setDateDisabled(utils.getCurrentDate());
		accountRepo.suspend(account);

		model.addAttribute("message", "Account suspended.");
		return "redirect:/account/edit/" + id;
    }


	@RequestMapping(value="/account/renew/{id}", method=RequestMethod.POST)
	public String renew(ModelMap model,
						  final RedirectAttributes redirect,
						  @PathVariable String id){

		if(!authService.isAdministrator()){
			redirect.addFlashAttribute("message", "You don't have permission to do this!");
			return "redirect:/account/profile/" + id;
		}

		Account account = accountRepo.get(Long.parseLong(id));
		accountRepo.renew(account);

		model.addAttribute("message", "Account renewed.");
		return "redirect:/account/edit/" + id;
	}
}