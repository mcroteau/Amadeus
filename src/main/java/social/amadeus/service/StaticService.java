package social.amadeus.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import social.amadeus.model.Account;

import javax.servlet.http.HttpServletRequest;

@Service
public class StaticService {

    @Autowired
    private PhoneService phoneService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private AuthService authService;

    public String uno(String uri, HttpServletRequest req) {
        if(uri != null &&
                !uri.equals("")){
            req.setAttribute("uri", uri);
        }
        return "static/uno";
    }

    public String beginReport() {
        phoneService.support("Amadeus:issue");
        return "static/report";
    }

    public String reportIssue(String email, String issue, ModelMap modelMap, RedirectAttributes redirect) {

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

        modelMap.addAttribute("message", "Thank you. Issue has been reported.");
        return "static/success";
    }

    public String sendInvites(String emails, ModelMap modelMap, RedirectAttributes redirect) {

        if(!authService.isAuthenticated()){
            redirect.addFlashAttribute("error", "Please signin to continue...");
            return "redirect:/signin";
        }

        Account account = authService.getAccount();

        if (emails.equals("")) {
            redirect.addFlashAttribute("error", "Please enter valid email addresses");
            return "static/invite";
        }

        String body = "<h1>Amadeus</h1>" +
                "<p>" + account.getName() + " invited you to join Amadeus! " +
                "<a href=\"https://amadeus.social\">https://amadeus.social</a>";

        emailService.send(emails, "You have been invited to join!", body);

        modelMap.addAttribute("message", "Invite(s) have been sent! Thank you!");
        return "static/success";

    }
}
