package social.amadeus.web;

import com.google.gson.Gson;
import org.apache.log4j.Logger;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import javax.servlet.http.HttpServletRequest;
import org.springframework.ui.ModelMap;

import org.springframework.web.multipart.commons.CommonsMultipartFile;

import social.amadeus.service.PostService;
import social.amadeus.common.Utils;
import social.amadeus.common.Constants;
import social.amadeus.model.*;


@Controller
public class PostController {

	private static final Logger log = Logger.getLogger(PostController.class);

	Gson gson = new Gson();

	@Autowired
	PostService postService;


	@GetMapping(value="/post/{id}", produces="application/json")
	public @ResponseBody String postData(@PathVariable String id){
		return gson.toJson(postService.getPost(id));
	}


	@RequestMapping(value="/post/activity", method=RequestMethod.GET, produces="application/json")
	public @ResponseBody String posts(HttpServletRequest req){
		req.getSession().setAttribute(Constants.ACTIVITY_REQUEST_TIME, Utils.getDate());
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
										     @RequestBody PostShareComment postShareComment){
		return gson.toJson(postService.savePostShareComment(id, postShareComment));
	}


	@RequestMapping(value="/post/comment/delete/{id}", method=RequestMethod.DELETE,  produces="application/json")
	public @ResponseBody String deletePostComment(@PathVariable String id) {
		return gson.toJson(postService.deletePostComment(id));
	}


	@RequestMapping(value="/post_share/comment/delete/{id}", method=RequestMethod.DELETE,  produces="application/json")
	public @ResponseBody String deletePostShareComment(@PathVariable String id) {
		return gson.toJson(postService.deletePostShareComment(id));
	}


	@RequestMapping(value="/post/hide/{id}", method=RequestMethod.POST,  produces="application/json")
	public @ResponseBody String hidePost(@PathVariable String id){
		return gson.toJson(postService.hidePost(id));
	}


	@RequestMapping(value="/post/flag/{id}/{shared}", method=RequestMethod.POST,  produces="application/json")
	public @ResponseBody String flagPost(@PathVariable String id,
									     @PathVariable Boolean shared){
		return gson.toJson(postService.flagPost(id, shared));
	}


	@RequestMapping(value="/post/flagged", method=RequestMethod.GET)
	public String reviewFlaggedPosts(ModelMap modelMap) {
		return postService.reviewFlaggedPosts(modelMap);
	}


	@RequestMapping(value="/post/review/{id}", method=RequestMethod.GET)
	public String postReview(ModelMap modelMap,
							 @PathVariable String id){
		return postService.reviewFlaggedPost(id, modelMap);
	}

	@RequestMapping(value="/post/flag/approve/{id}", method=RequestMethod.POST,  produces="application/json")
	public String approveFlaggedPost(@PathVariable String id){
		return postService.approveFlaggedPost(id);
	}


	@RequestMapping(value="/post/flag/revoke/{id}", method=RequestMethod.POST,  produces="application/json")
	public String revokePostFlag(@PathVariable String id){
		return postService.revokeFlag(id);
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