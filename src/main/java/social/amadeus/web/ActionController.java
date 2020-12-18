package social.amadeus.web;

import com.google.gson.Gson;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import social.amadeus.common.Constants;
import social.amadeus.common.Utils;
import social.amadeus.repository.*;
import social.amadeus.model.*;
import social.amadeus.service.AuthService;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Controller
public class ActionController {

    private static final Logger log = Logger.getLogger(ActionController.class);

    @Autowired
    private Utils utils;

    @Autowired
    private AccountRepo accountRepo;

    @Autowired
    private MusicRepo musicRepo;

    @Autowired
    private FriendRepo friendRepo;

    @Autowired
    private PostRepo postRepo;

    @Autowired
    private ActionRepo actionRepo;

    @Autowired
    private AuthService authService;

    @RequestMapping(value="/action", method=RequestMethod.GET)
    public String action(ModelMap model,
                                         HttpServletRequest request,
                                         final RedirectAttributes redirect,
                                         @RequestParam(value="uri", required = false ) String uri){

        if(!authService.isAuthenticated())
            return "redirect:/uno?uri=" + uri;

        model.addAttribute("uri", uri);
        return "action/hello";
    }


    @CrossOrigin(origins="*")//Thank you!
    @RequestMapping(value="/action/like", method=RequestMethod.POST)
    public String like(ModelMap model, HttpServletRequest request,
                                         final RedirectAttributes redirect,
                                         @RequestParam(value="uri", required = true ) String uri){

        if(!authService.isAuthenticated())
            return "redirect:/signin?uri=" + uri;

        Resource existingResource = actionRepo.get(uri);
        if(existingResource == null){
            Resource resource = new Resource();
            resource.setUri(uri);
            resource.setAccountId(authService.getAccount().getId());
            resource.setDateAdded(utils.getCurrentDate());
            actionRepo.save(resource);
        }

        Resource savedResource = actionRepo.get(uri);
        ActionLike actionLike = new ActionLike();
        actionLike.setResourceId(savedResource.getId());
        actionLike.setAccountId(authService.getAccount().getId());
        actionLike.setDateLiked(utils.getCurrentDate());

        if(!actionRepo.liked(actionLike))
            actionRepo.like(actionLike);

        model.addAttribute("message", "Successfully liked!");
        return "action/success";
    }


    @CrossOrigin(origins="*")
    @RequestMapping(value="/action/share", method=RequestMethod.POST)
    public String share(ModelMap model, HttpServletRequest request,
                                     final RedirectAttributes redirect,
                                     @RequestParam(value="uri", required = true ) String uri,
                                     @RequestParam(value="comment", required = true ) String comment){

        if(!authService.isAuthenticated())
            return "redirect:/signin?uri=" + uri;

        Resource resource = actionRepo.get(uri);
        if(resource == null){
            Resource r = new Resource();
            r.setUri(uri);
            r.setAccountId(authService.getAccount().getId());
            r.setDateAdded(utils.getCurrentDate());
            actionRepo.save(r);
        }

        Resource savedResource = actionRepo.get(uri);

        Post post = new Post();
        post.setAccountId(authService.getAccount().getId());
        post.setContent("<p class=\"post-comment\" style=\"white-space: pre-line\">" + uri + "<br/>" + comment + "</p>");
        post.setDatePosted(utils.getCurrentDate());
        Post savedPost = postRepo.save(post);

        accountRepo.savePermission(authService.getAccount().getId(), Constants.POST_MAINTENANCE + savedPost.getId());

        ActionShare actionShare = new ActionShare();
        actionShare.setComment(comment);
        actionShare.setResourceId(savedResource.getId());
        actionShare.setPostId(savedPost.getId());
        actionShare.setAccountId(authService.getAccount().getId());
        actionShare.setDateShared(utils.getCurrentDate());
        actionRepo.share(actionShare);

        model.addAttribute("message", "Successfully shared!");

        return "action/success";
    }

    @CrossOrigin(origins="*")
    @RequestMapping(value="/action/likes", method=RequestMethod.GET, produces="application/json")
    public @ResponseBody String data(ModelMap model,
                                       HttpServletRequest request,
                                       final RedirectAttributes redirect,
                                       @RequestParam(value="uri", required = true ) String uri){

        Resource resource = actionRepo.get(uri);
        long likes = 0;
        if(resource != null){
            likes = actionRepo.likesCount(resource.getId());
        }

        Map<String, Object> data = new HashMap<String, Object>();
        Gson gson = new Gson();

        data.put("likes", likes);

        return gson.toJson(data);
    }


    @CrossOrigin(origins="*")
    @RequestMapping(value="/action/shares", method=RequestMethod.GET, produces="application/json")
    public @ResponseBody String shares(ModelMap model,
                                      HttpServletRequest request,
                                      final RedirectAttributes redirect,
                                      @RequestParam(value="uri", required = false ) String uri){

        Resource resource = actionRepo.get(uri);
        long shares = 0;
        if(resource != null){
            shares = actionRepo.sharesCount(resource.getId());
        }

        Map<String, Object> data = new HashMap<String, Object>();
        Gson gson = new Gson();

        data.put("shares", shares);

        return gson.toJson(data);
    }

}
