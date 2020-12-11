package social.amadeus.web;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mobile.device.Device;
import org.springframework.stereotype.Controller;

import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import social.amadeus.common.Utilities;
import social.amadeus.model.Account;
import social.amadeus.service.AuthService;
import social.amadeus.service.EmailService;
import social.amadeus.service.PhoneService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@Controller
public class AppController {

	private static final Logger log = Logger.getLogger(AppController.class);

	@Autowired
	private Utilities utilities;

	@Autowired
	private PhoneService phoneService;

	@Autowired
	private EmailService emailService;

	@Autowired
	private AuthService authService;


	@RequestMapping(value="/", method=RequestMethod.GET)
	public String portal(Device device,
						 ModelMap model,
						 HttpServletRequest req,
						 HttpServletResponse resp,
						 final RedirectAttributes redirect){

		if(!authService.isAuthenticated()){
			return "redirect:/uno";
		}

		Account account = authService.getAccount();
		if(account.isDisabled()) {
			return "redirect:/account/edit/" + account.getId();
		}

		req.getSession().removeAttribute("message");

//		if(device.isMobile()) {
//			return "redirect:/mobile";
//		}else{
//			return "portal";
//		}

		return "portal";
	}

	@RequestMapping(value="/mobile", method=RequestMethod.GET)
	public String mobile(Device device, HttpServletRequest req){

		if(!authService.isAuthenticated()){
			return "app/uno";
		}

		Account account = authService.getAccount();
		if(account.isDisabled()) {
			return "redirect:/account/edit/" + account.getId();
		}

		return "portal_mobile";

	}


	@RequestMapping(value="/uno", method=RequestMethod.GET)
	public String uno(@RequestParam(value="uri", required = false) String uri,
					  HttpServletRequest req){
		if(uri != null &&
			!uri.equals("")){
			req.setAttribute("uri", uri);
		}
		return "app/uno";
	}


	@RequestMapping(value="/eula", method=RequestMethod.GET)
	public String eula(){
		return "app/eula";
	}

	@RequestMapping(value="/privacy", method=RequestMethod.GET)
	public String privacy(){
		return "app/privacy";
	}


	@RequestMapping(value="/get_code", method=RequestMethod.GET)
	public String getCode(){
		return "app/get_code";
	}


	@RequestMapping(value="/issues/report", method=RequestMethod.GET)
	public String report(){
		phoneService.support("Amadeus:issue");
		return "app/report";
	}


	@RequestMapping(value="/issues/report", method=RequestMethod.POST)
	public String reportIssue(
			@RequestParam(value="email", required = true ) String email,
			@RequestParam(value="issue", required = true ) String issue,
			final RedirectAttributes redirect,
			ModelMap model){


		if (email.equals("")) {
			redirect.addFlashAttribute("error", "Please enter a valid email address");
			return "redirect:/issues/report";
		}

		if (issue.equals("")) {
			redirect.addFlashAttribute("error", "Issue was left black, please tell us what happened.");
			return "redirect:/issues/report";
		}

		StringBuffer sb = new StringBuffer();
		sb.append(email);
		sb.append("<br/>");
		sb.append(issue);
		emailService.send("croteau.mike+amadeus@gmail.com", "Amadeus", sb.toString());

		model.addAttribute("message", "Thank you. Issue has been reported.");
		return "app/success";
	}


	@RequestMapping(value="/invite", method=RequestMethod.GET)
	public String invite(){
		return "app/invite";
	}


	@RequestMapping(value="/invite", method=RequestMethod.POST)
	public String sendInvite(
			@RequestParam(value="emails", required = true ) String emails,
			final RedirectAttributes redirect,
			ModelMap model){

		if(!authService.isAuthenticated()){
			redirect.addFlashAttribute("error", "Please signin to continue...");
			return "redirect:/signin";
		}

		Account account = authService.getAccount();

		if (emails.equals("")) {
			redirect.addFlashAttribute("error", "Please enter valid email addresses");
			return "app/invite";
		}

		String body = "<h1>Amadeus</h1>" +
				"<p>" + account.getName() + " invited you to join Amadeus! " +
				"<a href=\"https://amadeus.social\">https://amadeus.social</a>";

		emailService.send(emails, "You have been invited to join!", body);

		model.addAttribute("message", "Invite(s) have been sent! Thank you!");
		return "app/success";
	}

}