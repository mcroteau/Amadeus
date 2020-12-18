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

import java.util.*;

import social.amadeus.common.Constants;
import social.amadeus.common.Utils;
import social.amadeus.repository.*;
import social.amadeus.model.*;
import social.amadeus.service.AuthService;
import social.amadeus.service.NotificationService;
import social.amadeus.service.PostService;


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
		Post post = postService.getPost(id);
		return gson.toJson(post);
	}
	

	@RequestMapping(value="/post/activity", method=RequestMethod.GET, produces="application/json")
	public @ResponseBody String posts(HttpServletRequest req){

		if(!authService.isAuthenticated()){
			Map<String, Object> responseData = new HashMap<String, Object>();
			responseData.put("error", "authentication required");
			return gson.toJson(responseData);
		}

		Account authdAccount = authService.getAccount();
		req.getSession().setAttribute(Constants.ACTIVITY_REQUEST_TIME, utils.getCurrentDate());
		Map<String, Object> activityData = postService.getActivity(authdAccount);

		return gson.toJson(activityData);
	}



	@RequestMapping(value="/post/account/{id}", method=RequestMethod.GET, produces="application/json")
	public @ResponseBody String posts(HttpServletRequest request,
									  @PathVariable String id){


		if(!authService.isAuthenticated()){
			Map<String, Object> responseData = new HashMap<String, Object>();
			responseData.put("error", "authentication required");
			return gson.toJson(responseData);
		}

		Account authdAccount = authService.getAccount();
		Account profileAccount = accountRepo.get(Long.parseLong(id));
		List<Post> userActivity = postService.getUserActivity(profileAccount, authdAccount);

		return gson.toJson(userActivity);
	}



	@RequestMapping(value="/post/save", method=RequestMethod.POST, produces="application/json")
	public @ResponseBody String share(@ModelAttribute("post") Post post,
									  @RequestParam(value="imageFiles", required = false) CommonsMultipartFile[] imageFiles,
									  @RequestParam(value="videoFile", required = false) CommonsMultipartFile videoFile){

		if(!authService.isAuthenticated()){
			post.setFailMessage("authentication required");
		}

		Post savedPost = postService.savePost(post, imageFiles, videoFile);
        return gson.toJson(savedPost);
	}


	@RequestMapping(value="/post/like/{id}", method=RequestMethod.POST,  produces="application/json")
	public @ResponseBody String like(@PathVariable String id){

		if(!authService.isAuthenticated()){
			Map<String, Object> respData = new HashMap<>();
			respData.put("error", "authentication required");
			return gson.toJson(respData);
		}

		Map<String, Object> respData = postService.likePost(id);
		return gson.toJson(respData);
	}




	@RequestMapping(value="/post/share/{id}", method=RequestMethod.POST,  produces="application/json")
	public @ResponseBody String sharePost(HttpServletRequest request,
									 		final RedirectAttributes redirect,
									 		@PathVariable String id,
										  	@RequestBody PostShare postShareComment){


		Gson gson = new Gson();
		Map<String, Object> response = new HashMap<String, Object>();

		if(!authService.isAuthenticated()){
			response.put("error", "authentication required");
			String responseData = gson.toJson(response);
			return responseData;
		}

		Account account = authService.getAccount();
		long dateShared = utils.getCurrentDate();

		PostShare postShare = new PostShare();
		postShare.setAccountId(account.getId());
		postShare.setPostId(Long.parseLong(id));
		postShare.setComment(postShareComment.getComment());
		if(postShare.getComment().contains("<style")){
			postShare.setComment(postShare.getComment().replace("style", "") + "We caught a hacker!");
		}

		if(postShare.getComment().contains("<script")){
			postShare.setComment(postShare.getComment().replace("script", "") + "We caught a hacker!");
		}

		postShare.setDateShared(dateShared);

		PostShare savedPostShare = postRepo.sharePost(postShare);

		response.put("success", savedPostShare);

		String permission = Constants.POST_MAINTENANCE  + postShare.getAccountId() + ":" + savedPostShare.getId();
		accountRepo.savePermission(account.getId(), permission);

		Post existingPost = postRepo.get(Long.parseLong(id));

		Notification notification = notificationService.createNotification(existingPost.getAccountId(), account.getId(), Long.parseLong(id), false, true, false);
		notificationRepo.save(notification);

		return gson.toJson(response);

	}


	@RequestMapping(value="/post/delete/{id}", method=RequestMethod.DELETE,  produces="application/json")
	public @ResponseBody String remove(ModelMap model,
									 HttpServletRequest request,
									 final RedirectAttributes redirect,
									 @PathVariable String id){

		Map<String, Object> respData = new HashMap<String, Object>();

		if(!authService.isAuthenticated()){
			respData.put("error", "authentication required");
			return gson.toJson(respData);
		}

		boolean deleted = postService.deletePost(id);
		respData.put("success", deleted);
		return gson.toJson(respData);
	}


	@RequestMapping(value="/post/unshare/{id}", method=RequestMethod.DELETE,  produces="application/json")
	public @ResponseBody String unshare(ModelMap model,
									   HttpServletRequest request,
									   final RedirectAttributes redirect,
									   @PathVariable String id){

		Map<String, Object> data = new HashMap<String, Object>();
		Gson gson = new Gson();

		if(!authService.isAuthenticated()){
			data.put("error", "authentication required");
			return gson.toJson(data);
		}

		Account authenticatedAccount = authService.getAccount();
		String permission = Constants.POST_MAINTENANCE  + authenticatedAccount.getId() + ":" + id;
		if(authService.hasPermission(permission)){
			if(postRepo.deletePostShareComments(Long.parseLong(id))){
				if(!postRepo.deletePostShare(Long.parseLong(id))){
					data.put("error", "something went wrong...");
					return gson.toJson(data);
				}
			}
			data.put("success", true);
		}else{
			data.put("error", "permission required");
		}


		String json = gson.toJson(data);
		return json;
	}


	@RequestMapping(value="/post/comment/{id}", method=RequestMethod.POST, produces="application/json")
	public @ResponseBody String postComment(@PathVariable String id,
										    @RequestBody PostComment comment){


		Map<String, Object> data = new HashMap<String, Object>();
		Gson gson = new Gson();

		if(!authService.isAuthenticated()){
			data.put("error", "authentication required");
			return gson.toJson(data);
		}

		Account account = authService.getAccount();
		long date = utils.getCurrentDate();

		if(comment.getComment().equals("")){
			data.put("error", "comment is blank");
			return gson.toJson(data);
		}

		PostComment postComment = new PostComment();
		postComment.setPostId(Long.parseLong(id));
		postComment.setAccountId(account.getId());
		postComment.setAccountName(account.getName());
		postComment.setAccountImageUri(account.getImageUri());
		postComment.setComment(comment.getComment());
		if(postComment.getComment().contains("<style")){
			postComment.setComment(postComment.getComment().replace("style", "") + "We caught a hacker!");
		}
		if(postComment.getComment().contains("<script")){
			postComment.setComment(postComment.getComment().replace("script", "") + "We caught a hacker!");
		}

		postComment.setDateCreated(date);
		PostComment savedComment = postRepo.savePostComment(postComment);


		accountRepo.savePermission(account.getId(), Constants.COMMENT_MAINTENANCE  + savedComment.getId());

		Post post = postRepo.get(Long.parseLong(id));

		Notification notification = notificationService.createNotification(post.getAccountId(), account.getId(), Long.parseLong(id), false, false, true);
		notificationRepo.save(notification);

		return gson.toJson(data);

	}


	@RequestMapping(value="/post_share/comment/{id}", method=RequestMethod.POST,  produces="application/json")
	public @ResponseBody String shareComment(@PathVariable String id,
										     @RequestBody PostComment comment){


		Map<String, Object> data = new HashMap<String, Object>();
		Gson gson = new Gson();

		if(!authService.isAuthenticated()){
			data.put("error", "authentication required");
			return gson.toJson(data);
		}

		Account account = authService.getAccount();
		long date = utils.getCurrentDate();

		if(comment.getComment().equals("")){
			data.put("error", "comment is blank");
			return gson.toJson(data);
		}

		PostShareComment postShareComment = new PostShareComment();
		postShareComment.setPostShareId(Long.parseLong(id));
		postShareComment.setAccountId(account.getId());
		postShareComment.setAccountName(account.getName());
		postShareComment.setAccountImageUri(account.getImageUri());
		postShareComment.setComment(comment.getComment());
		if(postShareComment.getComment().contains("<style")){
			postShareComment.setComment(postShareComment.getComment().replace("style", "") + "We caught a hacker!");
		}
		if(postShareComment.getComment().contains("<script")){
			postShareComment.setComment(postShareComment.getComment().replace("script", "") + "We caught a hacker!");
		}

		postShareComment.setDateCreated(date);
		PostShareComment savedComment = postRepo.savePostShareComment(postShareComment);

		accountRepo.savePermission(account.getId(), Constants.COMMENT_MAINTENANCE  + savedComment.getId());
		PostShare postShare = postRepo.getPostShare(Long.parseLong(id));

		Notification notification = notificationService.createNotification(postShare.getAccountId(), account.getId(), Long.parseLong(id), false, false, true);
		notificationRepo.save(notification);

		return gson.toJson(data);

	}


	@RequestMapping(value="/post/delete_comment/{id}", method=RequestMethod.DELETE,  produces="application/json")
	public @ResponseBody String deletePostComment(@PathVariable String id) {
		Map<String, Object> data = new HashMap<String, Object>();
		Gson gson = new Gson();

		if(!authService.isAuthenticated()){
			data.put("error", "authentication required");
			return gson.toJson(data);
		}

		Long commentId = Long.parseLong(id);
		String permission = Constants.COMMENT_MAINTENANCE + id;
		if(authService.hasPermission(permission)){
			postRepo.deletePostComment(commentId);
			data.put("success", true);
		}
		else{
			data.put("error", true);
		}

		return gson.toJson(data);

	}



	@RequestMapping(value="/post_share/delete_comment/{id}", method=RequestMethod.DELETE,  produces="application/json")
	public @ResponseBody String deletePostShareComment(@PathVariable String id) {
		Map<String, Object> data = new HashMap<String, Object>();
		Gson gson = new Gson();

		if(!authService.isAuthenticated()){
			data.put("error", "authentication required");
			return gson.toJson(data);
		}

		Long commentId = Long.parseLong(id);
		String permission = Constants.COMMENT_MAINTENANCE + id;
		if(authService.hasPermission(permission)){
			postRepo.deletePostShareComment(commentId);
			data.put("success", true);
		}
		else{
			data.put("error", true);
		}

		return gson.toJson(data);

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
											  HttpServletRequest request,
											  final RedirectAttributes redirect,
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
											  HttpServletRequest request,
											  final RedirectAttributes redirect,
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
	public @ResponseBody String share(ModelMap model,
									  HttpServletRequest request,
									  final RedirectAttributes redirect,
									  @RequestParam(value="imageFiles", required = false) CommonsMultipartFile[] uploadedImageFiles,
									  @PathVariable String id) {


		Map<String, Object> response = new HashMap<String, Object>();
		Gson gson = new Gson();

		if(!authService.isAuthenticated()){
			response.put("error", "authentication required");
			String responseData = gson.toJson(response);
			return responseData;
		}

		String permission = Constants.POST_MAINTENANCE  + id;
		if(authService.hasPermission(permission)) {

			List<String> imageUris = new ArrayList<String>();

			if(uploadedImageFiles != null &&
					uploadedImageFiles.length > 0) {

				for (CommonsMultipartFile imageFile : uploadedImageFiles){
					String imageUri = utils.write(imageFile, Constants.IMAGE_DIRECTORY);

					if(imageUri.equals("")){
						utils.deleteUploadedFile(imageUri);
					}
					else{
						imageUris.add(imageUri);
					}
				}
			}

			Post post = postRepo.get(Long.parseLong(id));

			for(String imageUri: imageUris){
				PostImage postImage = new PostImage();
				postImage.setPostId(post.getId());
				postImage.setUri(imageUri);
				postImage.setDate(utils.getCurrentDate());
				postRepo.saveImage(postImage);
			}

			response.put("success", true);

		}else{
			response.put("error", "user does not have required permissions");
		}

		return gson.toJson(response);
	}



	@RequestMapping(value="/post/image/delete/{id}", method=RequestMethod.POST, produces="application/json")
	public @ResponseBody String deleteImage(@PathVariable String id,
											@RequestParam(value="imageUri", required = true) String imageUri){

		Map<String, Object> respData = new HashMap<String, Object>();
		if(!authService.isAuthenticated()){
			respData.put("fail", "authentication required");
			return gson.toJson(respData);
		}

		boolean deleted = postService.deletePostImage(id, imageUri);
		respData.put("success", deleted);

		return gson.toJson(respData);
	}


	@RequestMapping(value="/post/publish/{id}", method=RequestMethod.POST)
	public @ResponseBody String publish(ModelMap model,
									   @PathVariable String id) {

		Gson gson = new Gson();
		Map<String, Object> resp = new HashMap<String, Object>();

		if (!authService.isAuthenticated()) {
			resp.put("error", "authentication required");
			return gson.toJson(resp);
		}

		String permission = Constants.POST_MAINTENANCE + id;
		if (authService.hasPermission(permission)) {
			postRepo.publish(Long.parseLong(id));
			resp.put("success", true);
		}else{
			resp.put("error", "permission required");
		}
		return gson.toJson(resp);
	}


	@RequestMapping(value="/post/update/{id}", method=RequestMethod.POST)
	public @ResponseBody String update(ModelMap model,
						 @ModelAttribute("post") Post post,
						 @PathVariable String id){

		Map<String, Object> respData = new HashMap<String, Object>();

		if(!authService.isAuthenticated()){
			respData.put("fail", "authentication required");
			return gson.toJson(respData);
		}

		Post updatedPost = postService.updatePost(id, post);
		return gson.toJson(updatedPost);
	}


}