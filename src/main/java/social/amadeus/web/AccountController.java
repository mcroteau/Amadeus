package social.amadeus.web;

import com.google.gson.Gson;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import javax.servlet.http.HttpServletRequest;

import org.springframework.ui.ModelMap;
import social.amadeus.model.*;
import social.amadeus.service.*;


@Controller
public class AccountController {

	private static final Logger log = Logger.getLogger(AccountController.class);

	Gson gson = new Gson();

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
	

	@PostMapping(value="/register")
	protected String register(@ModelAttribute("account") Account account,
							  @RequestParam(value="g-recaptcha-response", required = true ) String reCaptchaResponse,
							  @RequestParam(value="uri", required = false ) String uri,
							  HttpServletRequest request,
							  RedirectAttributes redirect){
    	return accountService.register(uri, reCaptchaResponse, account, request, redirect);
	}



	@GetMapping(value="/profile/{id}", produces="application/json")
	public @ResponseBody String profile(ModelMap modelMap,
						  				@PathVariable String id){
   		return gson.toJson(accountService.profile(id, modelMap));
    }


	@GetMapping(value="/account/reset")
	public String reset(){
		return "account/reset";
	}


	@PostMapping(value="/account/send_reset")
	public String sendReset(RedirectAttributes redirect,
				    		HttpServletRequest request,
				    		@RequestParam(value="username", required = true ) String username){
		return accountService.sendReset(username, redirect, request);
	}


	@GetMapping(value="/account/confirm_reset")
	public String resetView(ModelMap modelMap,
							RedirectAttributes redirect,
							@RequestParam(value="username", required = true ) String username,
							@RequestParam(value="uuid", required = true ) String uuid){
		return accountService.resetView(uuid, username, modelMap, redirect);
	}



	@RequestMapping(value="/account/reset/{id}", method=RequestMethod.POST)
	public String resetPassword(@ModelAttribute("account") Account account,
								ModelMap modelMap,
								RedirectAttributes redirect){
    	return accountService.resetPassword(account, modelMap, redirect);
	}


	@RequestMapping(value="/profile/heart/{id}", method=RequestMethod.POST,  produces="application/json")
	public @ResponseBody String heart(@PathVariable String id){
		return gson.toJson(accountService.heart(id));
	}



	@RequestMapping(value="/account/block/{id}", method=RequestMethod.POST,  produces="application/json")
	public @ResponseBody String blockUser(@PathVariable String id) {
		return gson.toJson(accountService.blockUser(id));
	}


	@RequestMapping(value="/profile/data", method=RequestMethod.GET, produces="application/json")
	public @ResponseBody String profileData(HttpServletRequest request){
    	return gson.toJson(accountService.profileData(request));
	}


	@RequestMapping(value="/profile/data/views", method=RequestMethod.GET, produces="application/json")
	public @ResponseBody String viewsData(){
		return gson.toJson(accountService.viewsData());
	}


	@RequestMapping(value="/account/suspend/{id}", method=RequestMethod.POST)
	public String suspendAccount(ModelMap modelMap,
					   			 RedirectAttributes redirect,
					   			 @PathVariable String id){
		return accountService.suspendAccount(id, modelMap, redirect);
    }


	@RequestMapping(value="/account/renew/{id}", method=RequestMethod.POST)
	public String renew(ModelMap modelMap,
						RedirectAttributes redirect,
						@PathVariable String id){
    	return accountService.renewAccount(id, modelMap, redirect);
	}
}