package social.amadeus.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import social.amadeus.common.Constants;
import social.amadeus.common.Utils;
import social.amadeus.model.ActionLike;
import social.amadeus.model.ActionShare;
import social.amadeus.model.Post;
import social.amadeus.model.Resource;
import social.amadeus.repository.AccountRepo;
import social.amadeus.repository.ActionRepo;
import social.amadeus.repository.PostRepo;

import java.util.HashMap;
import java.util.Map;

public class ActionService {

    @Autowired
    private ActionRepo actionRepo;

    @Autowired
    private PostRepo postRepo;

    @Autowired
    private AccountRepo accountRepo;

    @Autowired
    private AuthService authService;

    public String getAction(String uri, ModelMap modelMap) {
        if(!authService.isAuthenticated())
            return "redirect:/home?uri=" + uri;

        modelMap.addAttribute("uri", uri);
        return "action/hello";
    }

    public String likeWebsite(String uri, ModelMap modelMap) {

        if(!authService.isAuthenticated())
            return "redirect:/signin?uri=" + uri;

        Resource existingResource = actionRepo.getWebsite(uri);
        if(existingResource == null){
            Resource resource = new Resource();
            resource.setUri(uri);
            resource.setAccountId(authService.getAccount().getId());
            resource.setDateAdded(Utils.getDate());
            actionRepo.saveWebsite(resource);
        }

        Resource savedResource = actionRepo.getWebsite(uri);
        ActionLike actionLike = new ActionLike();
        actionLike.setResourceId(savedResource.getId());
        actionLike.setAccountId(authService.getAccount().getId());
        actionLike.setDateLiked(Utils.getDate());

        if(!actionRepo.isLiked(actionLike))
            actionRepo.likeWebsite(actionLike);

        modelMap.addAttribute("message", "Successfully liked!");
        return "action/success";
    }

    public String shareWebsite(String uri, String comment, ModelMap modelMap) {

        if(!authService.isAuthenticated())
            return "redirect:/signin?uri=" + uri;

        Resource resource = actionRepo.getWebsite(uri);
        if(resource == null){
            Resource r = new Resource();
            r.setUri(uri);
            r.setAccountId(authService.getAccount().getId());
            r.setDateAdded(Utils.getDate());
            actionRepo.saveWebsite(r);
        }

        Resource savedResource = actionRepo.getWebsite(uri);

        Post post = new Post();
        post.setAccountId(authService.getAccount().getId());

        if(comment.contains("<style")){
            comment.replace("<style", "<h1>We caught a hacker</h1>");
        }

        if(comment.contains("<script")){
            comment.replace("<script", "<h1>We caught a hacker</h1>");
        }

        post.setContent("<p class=\"post-comment\" style=\"white-space: pre-line\">" + uri + "<br/>" + comment + "</p>");
        post.setDatePosted(Utils.getDate());
        Post savedPost = postRepo.save(post);

        accountRepo.savePermission(authService.getAccount().getId(), Constants.POST_MAINTENANCE + savedPost.getId());

        ActionShare actionShare = new ActionShare();
        actionShare.setComment(comment);
        actionShare.setResourceId(savedResource.getId());
        actionShare.setPostId(savedPost.getId());
        actionShare.setAccountId(authService.getAccount().getId());
        actionShare.setDateShared(Utils.getDate());
        actionRepo.shareWebsite(actionShare);

        modelMap.addAttribute("message", "Successfully shared!");

        return "action/success";
    }

    public Map<String, Object> getLikes(String uri) {
        Map<String, Object> respData = new HashMap<>();

        Resource resource = actionRepo.getWebsite(uri);
        long likes = 0;
        if(resource != null)
            likes = actionRepo.getLikesCount(resource.getId());

        respData.put("likes", likes);

        return respData;
    }

    public Map<String, Object> getSharesCount(String uri) {
        Map<String, Object> respData = new HashMap<>();

        Resource resource = actionRepo.getWebsite(uri);
        long shares = 0;
        if(resource != null){
            shares = actionRepo.getSharesCount(resource.getId());
        }

        respData.put("shares", shares);

        return respData;
    }
}
