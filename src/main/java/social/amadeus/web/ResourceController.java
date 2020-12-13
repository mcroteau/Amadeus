package social.amadeus.web;

import com.google.gson.Gson;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import social.amadeus.common.Constants;
import social.amadeus.common.Utilities;
import social.amadeus.repository.*;
import social.amadeus.model.*;
import social.amadeus.service.AuthService;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class ResourceController {

    private static final Logger log = Logger.getLogger(ResourceController.class);

    @Autowired
    private Utilities utilities;

    @Autowired
    private AccountRepo accountRepo;

    @Autowired
    private MusicRepo musicRepo;

    @Autowired
    private FriendRepo friendRepo;

    @Autowired
    private PostRepo postRepo;

    @Autowired
    private ResourceRepo resourceRepo;

    @Autowired
    private AuthService authService;

    @RequestMapping(value="/resource", method=RequestMethod.GET)
    public String resource(ModelMap model,
                                         HttpServletRequest request,
                                         final RedirectAttributes redirect,
                                         @RequestParam(value="uri", required = false ) String uri){

        if(!authService.isAuthenticated())
            return "redirect:/uno?uri=" + uri;

        model.addAttribute("uri", uri);
        return "resource/resource";
    }


    @CrossOrigin(origins="*")
    @RequestMapping(value="/resource/like", method=RequestMethod.POST)
    public String save(ModelMap model, HttpServletRequest request,
                                         final RedirectAttributes redirect,
                                         @RequestParam(value="uri", required = true ) String uri){

        if(!authService.isAuthenticated())
            return "redirect:/signin?uri=" + uri;

        Resource existingResource = resourceRepo.get(uri);
        if(existingResource == null){
            Resource resource = new Resource();
            resource.setUri(uri);
            resource.setAccountId(authService.getAccount().getId());
            resource.setDateAdded(utilities.getCurrentDate());
            resourceRepo.save(resource);
        }

        Resource savedResource = resourceRepo.get(uri);
        ResourceLike resourceLike = new ResourceLike();
        resourceLike.setResourceId(savedResource.getId());
        resourceLike.setAccountId(authService.getAccount().getId());
        resourceLike.setDateLiked(utilities.getCurrentDate());

        if(!resourceRepo.liked(resourceLike))
            resourceRepo.like(resourceLike);

        model.addAttribute("message", "Successfully liked!");
        return "resource/resource_success";
    }


    @CrossOrigin(origins="*")
    @RequestMapping(value="/resource/share", method=RequestMethod.POST)
    public String save(ModelMap model, HttpServletRequest request,
                                     final RedirectAttributes redirect,
                                     @RequestParam(value="uri", required = true ) String uri,
                                     @RequestParam(value="comment", required = true ) String comment){

        if(!authService.isAuthenticated())
            return "redirect:/signin?uri=" + uri;

        Resource resource = resourceRepo.get(uri);
        if(resource == null){
            Resource r = new Resource();
            r.setUri(uri);
            r.setAccountId(authService.getAccount().getId());
            r.setDateAdded(utilities.getCurrentDate());
            resourceRepo.save(r);
        }

        Resource savedResource = resourceRepo.get(uri);

        Post post = new Post();
        post.setAccountId(authService.getAccount().getId());
        post.setContent("<p class=\"post-comment\" style=\"white-space: pre-line\">" + uri + "<br/>" + comment + "</p>");
        post.setDatePosted(utilities.getCurrentDate());
        Post savedPost = postRepo.save(post);

        accountRepo.savePermission(authService.getAccount().getId(), Constants.POST_MAINTENANCE + savedPost.getId());

        ResourceShare resourceShare = new ResourceShare();
        resourceShare.setComment(comment);
        resourceShare.setResourceId(savedResource.getId());
        resourceShare.setPostId(savedPost.getId());
        resourceShare.setAccountId(authService.getAccount().getId());
        resourceShare.setDateShared(utilities.getCurrentDate());
        resourceRepo.share(resourceShare);

        model.addAttribute("message", "Successfully shared!");

        return "resource/resource_success";
    }

    @CrossOrigin(origins="*")
    @RequestMapping(value="/resource/likes", method=RequestMethod.GET, produces="application/json")
    public @ResponseBody String data(ModelMap model,
                                       HttpServletRequest request,
                                       final RedirectAttributes redirect,
                                       @RequestParam(value="uri", required = true ) String uri){

        Resource resource = resourceRepo.get(uri);
        long likes = 0;
        if(resource != null){
            likes = resourceRepo.likesCount(resource.getId());
        }

        Map<String, Object> data = new HashMap<String, Object>();
        Gson gson = new Gson();

        data.put("likes", likes);

        return gson.toJson(data);
    }


    @CrossOrigin(origins="*")
    @RequestMapping(value="/resource/shares", method=RequestMethod.GET, produces="application/json")
    public @ResponseBody String shares(ModelMap model,
                                      HttpServletRequest request,
                                      final RedirectAttributes redirect,
                                      @RequestParam(value="uri", required = false ) String uri){

        Resource resource = resourceRepo.get(uri);
        long shares = 0;
        if(resource != null){
            shares = resourceRepo.sharesCount(resource.getId());
        }

        Map<String, Object> data = new HashMap<String, Object>();
        Gson gson = new Gson();

        data.put("shares", shares);

        return gson.toJson(data);
    }


    @RequestMapping(value="/search", method=RequestMethod.GET, produces="application/json")
    public @ResponseBody String search(ModelMap model,
                  HttpServletRequest request,
                  final RedirectAttributes redirect,
                  @RequestParam(value="q", required = false ) String q){

        Map<String, Object> data = new HashMap<String, Object>();
        Gson gson = new Gson();

        if(!authService.isAuthenticated()){
            data.put("error", "Authentication required");
            return gson.toJson(data);
        }

        Account account = authService.getAccount();

        if(q != null){

            List<Account>  accounts = accountRepo.search(q, 0);

            for(Account a : accounts){
                a.setIsFriend(friendRepo.isFriend(account.getId(), a.getId()));
                a.setInvited(friendRepo.invited(account.getId(), a.getId()));

                AccountBlock accountBlock = new AccountBlock();
                accountBlock.setPersonId(account.getId());
                accountBlock.setBlockerId(a.getId());

                boolean blocked = accountRepo.blocked(accountBlock);
                a.setBlocked(blocked);
            }
            Map<String, Object> d = new HashMap<String, Object>();

            for(Account a : accounts){
                if(a.getId() == account.getId()){
                    a.setOwnersAccount(true);
                }
            }

            List<MusicFile> music = musicRepo.search(q);
            for(MusicFile musicFile : music){
                if(musicFile.getAccountId() == account.getId()){
                    musicFile.setEditable(true);
                }
            }

            d.put("accounts", accounts);
            d.put("music", music);

            return gson.toJson(d);

        } else {
            return gson.toJson(data);
        }
    }

}
