package social.amadeus.web;

import com.google.gson.Gson;
import org.apache.log4j.Logger;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import javax.servlet.http.HttpServletRequest;
import org.springframework.ui.ModelMap;

import org.springframework.web.multipart.commons.CommonsMultipartFile;

import java.io.IOException;
import java.util.*;

import social.amadeus.common.Constants;
import social.amadeus.common.Utils;
import social.amadeus.repository.*;
import social.amadeus.model.*;
import social.amadeus.service.AuthService;
import social.amadeus.service.NotificationService;
import social.amadeus.service.PostService;
import social.amadeus.service.SyncService;


@Controller
public class PostController {

	private static final Logger log = Logger.getLogger(PostController.class);

	Gson gson = new Gson();

	@Autowired
	private PostRepo postRepo;

	@Autowired
	private AccountRepo accountRepo;

	@Autowired
	private Utils utils;

	@Autowired
	private NotificationRepo notificationRepo;

	@Autowired
	private AuthService authService;

	@Autowired
	private PostService postService;

	@Autowired
	private NotificationService notificationService;



	@GetMapping(value="/post/{id}", produces="application/json")
	public @ResponseBody String postData(@PathVariable String id){
		return gson.toJson(postService.getPost(id));
	}

	@RequestMapping(value="/post/activity", method=RequestMethod.GET, produces="application/json")
	public @ResponseBody String posts(HttpServletRequest req){
		req.getSession().setAttribute(Constants.ACTIVITY_REQUEST_TIME, utils.getCurrentDate());
		return gson.toJson(postService.getActivity());
	}


	@RequestMapping(value="/post/account/{id}", method=RequestMethod.GET, produces="application/json")
	public @ResponseBody String posts(HttpServletRequest request,
									  @PathVariable String id){
		return gson.toJson(postService.getUserActivity(id));
	}


	@RequestMapping(value="/post/save", method=RequestMethod.POST, produces="application/json")
	public @ResponseBody String share(@ModelAttribute("post") Post post,
									  @RequestParam(value="imageFiles", required = false) CommonsMultipartFile[] imageFiles,
									  @RequestParam(value="videoFile", required = false) CommonsMultipartFile videoFile){
        return gson.toJson(postService.savePost(post, imageFiles, videoFile));
	}


	@RequestMapping(value="/post/publish/{id}", method=RequestMethod.POST)
	public @ResponseBody String publish(ModelMap model,
										@PathVariable String id) {
		return gson.toJson(postService.publishPost(id));
	}


	@RequestMapping(value="/post/update/{id}", method=RequestMethod.POST)
	public @ResponseBody String update(@ModelAttribute("post") Post post,
									   @PathVariable String id){
		return gson.toJson(postService.updatePost(id, post));
	}


	@RequestMapping(value="/post/like/{id}", method=RequestMethod.POST,  produces="application/json")
	public @ResponseBody String like(@PathVariable String id){
		return gson.toJson(postService.likePost(id));
	}


	@RequestMapping(value="/post/share/{id}", method=RequestMethod.POST,  produces="application/json")
	public @ResponseBody String sharePost(@PathVariable String id,
										  @RequestBody PostShare postShare){
		return gson.toJson(postService.sharePost(id, postShare));
	}


	@RequestMapping(value="/post/delete/{id}", method=RequestMethod.DELETE,  produces="application/json")
	public @ResponseBody String delete(@PathVariable String id){
		return gson.toJson(postService.deletePost(id));
	}


	@RequestMapping(value="/post/unshare/{id}", method=RequestMethod.DELETE,  produces="application/json")
	public @ResponseBody String unshare(@PathVariable String id){
		return gson.toJson(postService.unsharePost(id));
	}


	@RequestMapping(value="/post/comment/{id}", method=RequestMethod.POST, produces="application/json")
	public @ResponseBody String postComment(@PathVariable String id,
										    @RequestBody PostComment postComment){
		return gson.toJson(postService.savePostComment(id, postComment));
	}


	@RequestMapping(value="/post_share/comment/{id}", method=RequestMethod.POST,  produces="application/json")
	public @ResponseBody String shareComment(@PathVariable String id,
										     @RequestBody PostComment postComment){
		return gson.toJson(postService.savePostShareComment(id, postComment));
	}


	@RequestMapping(value="/post/delete_comment/{id}", method=RequestMethod.DELETE,  produces="application/json")
	public @ResponseBody String deletePostComment(@PathVariable String id) {
		return gson.toJson(postService.deletePostComment(id));
	}


	@RequestMapping(value="/post_share/delete_comment/{id}", method=RequestMethod.DELETE,  produces="application/json")
	public @ResponseBody String deletePostShareComment(@PathVariable String id) {
		return gson.toJson(postService.deletePostShareComment(id));
	}


	@RequestMapping(value="/post/hide/{id}", method=RequestMethod.POST,  produces="application/json")
	public @ResponseBody String hidePost(ModelMap model,
										 HttpServletRequest request,
										 final RedirectAttributes redirect,
										 @PathVariable String id){

		Map<String, Object> resp = new HashMap<String, Object>();
		Gson gson = new Gson();

		if(!authService.isAuthenticated()){
			resp.put("error", "authentication required");
			return gson.toJson(resp);
		}

		Account account = authService.getAccount();
		HiddenPost hiddenPost = new HiddenPost();
		hiddenPost.setAccountId(account.getId());
		hiddenPost.setPostId(Long.parseLong(id));
		hiddenPost.setDateHidden(utils.getCurrentDate());

		postRepo.makeInvisible(hiddenPost);
		postRepo.hide(Long.parseLong(id));

		resp.put("success", true);
		return gson.toJson(resp);
	}


	@RequestMapping(value="/post/flag/{id}/{shared}", method=RequestMethod.POST,  produces="application/json")
	public @ResponseBody String flagPost(ModelMap model,
									 HttpServletRequest request,
									 final RedirectAttributes redirect,
									 @PathVariable String id,
									 @PathVariable Boolean shared){

		Map<String, Object> response = new HashMap<String, Object>();
		Gson gson = new Gson();

		if(!authService.isAuthenticated()){
			response.put("error", "authentication required");
			return gson.toJson(response);
		}

		Post post = postRepo.get(Long.parseLong(id));
		post.setFlagged(true);

		PostFlag postFlag = new PostFlag();
		postFlag.setPostId(post.getId());
		postFlag.setAccountId(authService.getAccount().getId());
		postFlag.setDateFlagged(utils.getCurrentDate());
		postFlag.setShared(shared);

		postRepo.flagPost(postFlag);
		postRepo.updateFlagged(post);

		String body = "<h1>Amadeus</h1>"+
				"<p>" + post.getContent() + "</p>" +
				"<p><a href=\"amadeus.social\">Login</a></p>";
		//emailService.send(Constants.ADMIN_USERNAME, "It ain't good.", body);

		response.put("success", true);
		return gson.toJson(response);
	}


	@RequestMapping(value="/posts/flagged", method=RequestMethod.GET)
	public String postsFlagged(ModelMap model,
							   HttpServletRequest request) {

		if(!authService.isAdministrator()){
			return "redirect:/unauthorized";
		}

		List<Post> posts = postRepo.getFlaggedPosts();
		model.addAttribute("posts", posts);

		return "admin/flagged";
	}


	@RequestMapping(value="/post/review/{id}", method=RequestMethod.GET)
	public String postReview(ModelMap model,
							 @PathVariable String id){
		if(!authService.isAdministrator()){
			return "redirect:/unauthorized";
		}

		Post post = postRepo.getFlaggedPost(Long.parseLong(id));
		Account account = accountRepo.get(post.getAccountId());
		Post populatedPost = postService.setPostData(post, account);
		model.addAttribute("post", populatedPost);
		return "admin/review_post";
	}

	@RequestMapping(value="/post/flag/approve/{id}", method=RequestMethod.POST,  produces="application/json")
	public String approvePostFlag(ModelMap model,
								  @PathVariable String id){
		if(!authService.isAdministrator()){
			return "redirect:/unauthorized";
		}

		Post post = postRepo.get(Long.parseLong(id));
		Account account = accountRepo.get(post.getAccountId());

		account.setDisabled(true);
		accountRepo.updateDisabled(account);

		postRepo.delete(post.getId());
		postRepo.removePostShares(post.getId());
		postRepo.removePostFlags(post.getId());

		List<PostImage> postImages = postRepo.getImages(post.getId());
		for(PostImage postImage : postImages){
			postRepo.deletePostImage(postImage.getId());
			utils.deleteUploadedFile(postImage.getUri());
		}

		return "redirect:/posts/flagged";
	}


	@RequestMapping(value="/post/flag/revoke/{id}", method=RequestMethod.POST,  produces="application/json")
	public String revokePostFlag(ModelMap model,
								  HttpServletRequest request,
								  final RedirectAttributes redirect,
								  @PathVariable String id){

		if(!authService.isAdministrator()){
			return "redirect:/unauthorized";
		}

		Post post = postRepo.get(Long.parseLong(id));
		Account account = accountRepo.get(post.getAccountId());

		account.setDisabled(false);
		accountRepo.updateDisabled(account);

		post.setFlagged(false);
		postRepo.updateFlagged(post);
		postRepo.removePostFlags(post.getId());

		return "redirect:/posts/flagged";
	}


	@RequestMapping(value="/post/image/add/{id}", method=RequestMethod.POST, produces="application/json")
	public @ResponseBody String addImage(@PathVariable String id,
									     @RequestParam(value="imageFiles", required=false) CommonsMultipartFile[] imageFiles) {
		return gson.toJson(postService.addPostImages(id, imageFiles));
	}



	@RequestMapping(value="/post/image/delete/{id}", method=RequestMethod.POST, produces="application/json")
	public @ResponseBody String deleteImage(@PathVariable String id,
											@RequestParam(value="imageUri", required = true) String imageUri){
		return gson.toJson(postService.deletePostImage(id, imageUri));
	}


}