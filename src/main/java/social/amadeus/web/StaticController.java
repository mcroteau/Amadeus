package social.amadeus.web;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import social.amadeus.service.StaticService;

import javax.servlet.http.HttpServletRequest;


@Controller
public class StaticController {

	private static final Logger log = Logger.getLogger(StaticController.class);

	@Autowired
	StaticService staticService;

	@RequestMapping(value="/home", method=RequestMethod.GET)
	public String home(HttpServletRequest request,
					  @RequestParam(value="uri", required=false) String uri){
		return staticService.home(uri, request);
	}

	@RequestMapping(value="/signin", method=RequestMethod.GET)
	public String signin(){
		return staticService.signin();
	}

	@RequestMapping(value="/eula", method=RequestMethod.GET)
	public String eula(){
		return "static/eula";
	}

	@RequestMapping(value="/privacy", method=RequestMethod.GET)
	public String privacy(){
		return "static/privacy";
	}

	@RequestMapping(value="/get_code", method=RequestMethod.GET)
	public String getCode(){
		return "action/get_code";
	}


	@RequestMapping(value="/issues/report", method=RequestMethod.GET)
	public String beginReport(){
		return staticService.beginReport();
	}


	@RequestMapping(value="/issues/report", method=RequestMethod.POST)
	public String reportIssue(ModelMap modelMap,
							  RedirectAttributes redirect,
							  @RequestParam(value="email", required = true ) String email,
							  @RequestParam(value="issue", required = true ) String issue){
		return staticService.reportIssue(email, issue, modelMap, redirect);
	}


	@RequestMapping(value="/invite", method=RequestMethod.GET)
	public String invite(){ return "static/invite"; }


	@RequestMapping(value="/invite", method=RequestMethod.POST)
	public String sendInvite(ModelMap modelMap,
							 RedirectAttributes redirect,
							 @RequestParam(value="emails", required = true ) String emails){
		return staticService.sendInvites(emails, modelMap, redirect);
	}

	@RequestMapping(value="/unauthorized", method=RequestMethod.GET)
	public String unauthorized(ModelMap model, @RequestParam(value="uri", required=false ) String uri){
		return "static/unauthorized";
	}

}