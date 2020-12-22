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
	private StaticService staticService;

	@RequestMapping(value="/uno", method=RequestMethod.GET)
	public String uno(HttpServletRequest request,
					  @RequestParam(value="uri", required=false) String uri){
		return staticService.uno(uri, request);
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
		return "static/get_code";
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

}