package social.amadeus.web;

import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import javax.servlet.http.HttpServletRequest;
import org.springframework.ui.ModelMap;

import org.springframework.web.multipart.commons.CommonsMultipartFile;

import java.text.SimpleDateFormat;
import org.ocpsoft.prettytime.PrettyTime;

import java.util.*;

import social.amadeus.common.Constants;
import social.amadeus.common.SessionManager;
import social.amadeus.common.Utilities;
import social.amadeus.dao.*;
import social.amadeus.model.*;
import social.amadeus.service.AuthService;
import social.amadeus.service.EmailService;
import social.amadeus.service.PostService;

import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Controller
public class PostController {

	private static final Logger log = Logger.getLogger(PostController.class);

	private static final String YOUTUBE_URL = "https://youtu.be";

	private static final String YOUTUBE_EMBED_URL = "https://youtube.com/embed";

	private static final String YOUTUBE_EMBED = "<iframe style=\"margin-left:-30px;\" width=\"465\" height=\"261\" src=\"{{URL}}\" frameborder=\"0\" allow=\"accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture\" allowfullscreen></iframe>";

	private static final Pattern urlPattern = Pattern.compile(
			"(?:^|[\\W])((ht|f)tp(s?):\\/\\/|www\\.)"
					+ "(([\\w\\-]+\\.){1,}?([\\w\\-.~]+\\/?)*"
					+ "[\\p{Alnum}.,%_=?&#\\-+()\\[\\]\\*$~@!:/{};']*)",
			Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);


	Gson gson = new Gson();

	@Autowired
	private PostDao postDao;

	@Autowired
	private AccountDao accountDao;

	@Autowired
	private FriendDao friendDao;

	@Autowired
	private MusicDao musicDao;

	@Autowired
	private Utilities utilities;

	@Autowired
	private SessionManager sessionManager;

	@Autowired
	private NotificationDao notificationDao;

	@Autowired
	private FlyerDao flyerDao;

	@Autowired
	private EmailService emailService;

	@Autowired
	private AuthService authService;

	@Autowired
	private PostService postService;



	//Germany is trying to get us all into one big group and use corporate espionage, so I don't need help right now.
	//I am Microsoft, I am IBM I am America

	@RequestMapping(value="/post/{id}", method=RequestMethod.GET, produces="application/json")
	public @ResponseBody String postData(HttpServletRequest request,
									  @PathVariable String id){
		Gson gson = new Gson();
		Post post = postDao.get(Long.parseLong(id));
		Account account = accountDao.get(post.getAccountId());
		Account authenticatedAccount = authService.getAccount();
		Post populated = postService.populatePost(post, account, authenticatedAccount);
		return gson.toJson(populated);
	}
	

	@RequestMapping(value="/activity", method=RequestMethod.GET, produces="application/json")
	public @ResponseBody String posts(HttpServletRequest req){

		if(!authService.isAuthenticated()){
			Map<String, Object> responseData = new HashMap<String, Object>();
			responseData.put("error", "authentication required");
			return gson.toJson(responseData);
		}

		long start = utilities.getPreviousDay(14);
		long end = utilities.getCurrentDate();

		List<Post> postsPre = postDao.getActivity(start, end, authService.getAccount().getId());
		List<Post> posts = populatePostData(postsPre);

		List<PostShare> postShares = postDao.getPostShares(start, end, authService.getAccount().getId());
		List<Post> postsShared = getPopulatedSharedPosts(postShares);

		List<Post> activityFeedPre = new ArrayList<>();
		activityFeedPre.addAll(posts);
		activityFeedPre.addAll(postsShared);

		List<Post> activityFeed = getFlyerPosts(activityFeedPre);
		List<Account> femalesMales = getFemalesMales(activityFeed);

		Map<String, Object> data = new HashMap<>();
		data.put("activities", activityFeed);
		data.put("femsfellas", femalesMales);

		return gson.toJson(data);

	}

	private List<Account> getFemalesMales(List<Post> activityFeed){
		Map<Long, Account> femalesMalesMap = new HashMap<>();
		for (Post post : activityFeed) {
			Account account = new Account();
			account.setId(post.getAccountId());
			account.setName(post.getName());
			account.setImageUri(post.getImageUri());
			if (femalesMalesMap.containsKey(post.getAccountId())) {
				int count = femalesMalesMap.get(post.getAccountId()).getCount() + 1;
				account.setCount(count);
				femalesMalesMap.put(post.getAccountId(), account);
			} else {
				account.setCount(1);
				femalesMalesMap.put(post.getAccountId(), account);
			}
		}

		List<Account> femalesMales = new ArrayList<Account>();
		for (Account femfella : femalesMalesMap.values()) {
			femalesMales.add(femfella);
		}
		return femalesMales;
	}

	private List<Post> getFlyerPosts(List<Post> activityFeed){
		Random rand = new Random();

		int adIdx = rand.nextInt(4);
		if (adIdx == 2) {
			List<Post> flyerPosts = new ArrayList<Post>();
			List<Flyer> activeFlyers = flyerDao.getActiveFlyers();

			int flyerIdx = 0;
			if (activeFlyers.size() > 0) {

				flyerIdx = rand.nextInt(activeFlyers.size());

				Flyer flyer = activeFlyers.get(flyerIdx);

				Post adPost = new Post();

				List<String> imageUris = new ArrayList<>();
				imageUris.add(flyer.getImageUri());

				adPost.setImageFileUris(imageUris);
				adPost.setAdvertisementUri(flyer.getPageUri());
				adPost.setAdvertisement(true);
				adPost.setPublished(true);

				if (activityFeed.size() > 0) {
					int feedIdx = rand.nextInt(activityFeed.size());
					activityFeed.add(feedIdx, adPost);
				} else {
					activityFeed.add(adPost);
				}
				long views = flyer.getAdViews() + 1;
				flyerDao.updateViews(views, flyer.getId());
			}
		}
		return activityFeed;
	}


	private List<Post> populatePostData(List<Post> posts){
		posts.stream().forEach(post -> setPostData(post));
		return posts;
	}

	private Post setPostData(Post post){
		setTimeAgo(post);
		setLikes(post);
		setShares(post);
		setPostActions(post);
		setPostComments(post);
		setMultimedia(post);
		setAccountData(post);
		log.info("image uri " + post.getImageUri());
		return post;
	}

	private Post setPostShareData(Post post, PostShare postShare){
		setTimeAgo(post);
		setLikes(post);
		setShares(post);
		setSharedPostActions(post);
		setPostShareComments(post, postShare);
		setMultimedia(post);
		setAccountData(post);
		return post;
	}


	private List<Post> getPopulatedSharedPosts(List<PostShare> postShares){
		List<Post> sharedPosts = new ArrayList<Post>();
		for(PostShare postShare: postShares){
			Post post = postDao.get(postShare.getPostId());
			setPostShareData(post, postShare);
			sharedPosts.add(post);
		}
		return sharedPosts;
	}


	private Post setLikes(Post post){
		long likes = postDao.likes(post.getId());
		post.setLikes(likes);

		PostLike postLike = new PostLike();
		postLike.setPostId(post.getId());
		postLike.setAccountId(authService.getAccount().getId());

		if (postDao.liked(postLike)) post.setLiked(true);

		return post;
	}

	private Post setShares(Post post){
		long shares = postDao.shares(post.getId());
		post.setShares(shares);
		return post;
	}

	private Post setPostActions(Post post){
		if(post.getAccountId() == authService.getAccount().getId()){
			post.setDeletable(true);
			post.setPostEditable(true);
		}
		return post;
	}

	private Post setSharedPostActions(Post post){
		if(post.getAccountId() == authService.getAccount().getId()){
			post.setDeletable(true);
		}
		return post;
	}

	private Post setPostComments(Post post){

		List<PostComment> postComments = postDao.getPostComments(post.getId());
		for (PostComment postComment : postComments) {
			if(postComment.getAccountId() == authService.getAccount().getId()){
				postComment.setCommentDeletable(true);
			}
			postComment.setCommentId(postComment.getId());//used for front end
		}
		post.setComments(postComments);
		if(postComments.size() > 0)post.setCommentsOrShareComments(true);

		return post;
	}

	private Post setPostShareComments(Post post, PostShare postShare){
		List<PostShareComment> postShareComments = postDao.getPostShareComments(postShare.getId());

		for (PostShareComment postShareComment : postShareComments) {
			if(postShareComment.getAccountId() == authService.getAccount().getId()){
				postShareComment.setCommentDeletable(true);
			}
			postShareComment.setCommentId(postShareComment.getId());
		}

		post.setShareComments(postShareComments);
		if(postShareComments.size() > 0)post.setCommentsOrShareComments(true);
		return post;
	}

	private Post setMultimedia(Post post){
		List<PostImage> postImages = postDao.getImages(post.getId());
		List<String> imageUris = new ArrayList<String>();

		for(PostImage postImage : postImages){
			imageUris.add(postImage.getUri());
		}
		post.setImageFileUris(imageUris);

		return post;
	}

	private Post setAccountData(Post post){
		Account account = accountDao.get(post.getAccountId());
		post.setAccountId(account.getId());
		post.setImageUri(account.getImageUri());
		post.setName(account.getName());
		post.setUsername(account.getUsername());
		return post;
	}

	private Post setTimeAgo(Post post){
		try {
			SimpleDateFormat format = new SimpleDateFormat(Constants.DATE_SEARCH_FORMAT);
			Date date = format.parse(Long.toString(post.getDatePosted()));

			PrettyTime prettyTime = new PrettyTime();
			post.setTimeAgo(prettyTime.format(date));
		}catch(Exception e){
			e.printStackTrace();
		}
		return post;
	}



	@RequestMapping(value="/posts/latest", method=RequestMethod.GET, produces="application/json")
	public @ResponseBody String latest(HttpServletRequest request){

		Gson gson = new Gson();
		Map<String, Object> responseData = new HashMap<String, Object>();

		if(!authService.isAuthenticated()){
			responseData.put("error", "authentication required");
			return gson.toJson(responseData);
		}

		try{

			List<Post> feed = new ArrayList<Post>();

			Account authenticatedAccount = authService.getAccount();

			if(request.getSession().getAttribute(Constants.ACTIVITY_REQUEST_TIME) != null) {
				long start = (Long) request.getSession().getAttribute(Constants.ACTIVITY_REQUEST_TIME);
				long end = utilities.getCurrentDate();

				feed = postDao.getActivity(start, end, authenticatedAccount.getId());
				for (Post post : feed) {
					Account postedAccount = accountDao.get(post.getAccountId());
					postService.populatePost(post, postedAccount, authenticatedAccount);
				}

				List<PostShare> postShares = postDao.getPostShares(start, end, authenticatedAccount.getId());

				for (PostShare postShare : postShares) {
					Post post = postDao.get(postShare.getPostId());

					post.setShared(true);
					post.setSharedComment(postShare.getComment());

					Account acc = accountDao.get(postShare.getAccountId());
					post.setSharedAccount(acc.getName());
					post.setSharedImageUri(acc.getImageUri());

					postService.populatePost(post, acc, authenticatedAccount);

					SimpleDateFormat format = new SimpleDateFormat(Constants.DATE_SEARCH_FORMAT);
					Date date = format.parse(Long.toString(postShare.getDateShared()));

					PrettyTime p = new PrettyTime();
					post.setTimeSharedAgo(p.format(date));
					post.setDatePosted(postShare.getDateShared());

					feed.add(post);
				}

				Comparator<Post> comparator = new Comparator<Post>() {
					@Override
					public int compare(Post a1, Post a2) {
						Long p1 = new Long(a1.getDatePosted());
						Long p2 = new Long(a2.getDatePosted());
						return p2.compareTo(p1);
					}
				};


				Collections.sort(feed, comparator);

			}

			return gson.toJson(feed);

		}catch(ParseException e){
			e.printStackTrace();
			return gson.toJson(responseData);
		}
	}


	@RequestMapping(value="/posts/{id}", method=RequestMethod.GET, produces="application/json")
	public @ResponseBody String posts(HttpServletRequest request,
									  @PathVariable String id){

		Gson gson = new Gson();
		Map<String, Object> responseData = new HashMap<String, Object>();

		if(!authService.isAuthenticated()){
			responseData.put("error", "authentication required");
			return gson.toJson(responseData);
		}

		try{

			Account account = authService.getAccount();

			List<Post> posts = postDao.getUserPosts(Long.parseLong(id));
			List<Post> populatedPosts = new ArrayList<Post>();

			for(Post post : posts){
				Account postedAccount = accountDao.get(post.getAccountId());
				Post populated = postService.populatePost(post, postedAccount, account);
				populatedPosts.add(populated);
			}

			List<PostShare> postShares = postDao.fetchUserPostShares(Long.parseLong(id));

			for(PostShare postShare: postShares){
				Post post = postDao.get(postShare.getPostId());
				post.setPostShareId(postShare.getId());

				Account postedAccount = accountDao.get(post.getAccountId());

				postService.populatePostShare(post, postShare, postedAccount, account);

				post.setShared(true);

				post.setSharedComment(postShare.getComment());

				Account acc = accountDao.get(postShare.getAccountId());
				post.setSharedAccountId(postShare.getAccountId());
				post.setSharedAccount(acc.getName());
				post.setSharedImageUri(acc.getImageUri());

				SimpleDateFormat format = new SimpleDateFormat(Constants.DATE_SEARCH_FORMAT);
				Date date = format.parse(Long.toString(postShare.getDateShared()));

				PrettyTime p = new PrettyTime();
				post.setTimeSharedAgo(p.format(date));
				post.setDatePosted(postShare.getDateShared());

				if(postShare.getAccountId() == account.getId()){
					post.setDeletable(true);
				}
				populatedPosts.add(post);
			}


			Comparator<Post> comparator = new Comparator<Post>() {
				@Override
				public int compare(Post a1, Post a2) {
					Long p1 = new Long(a1.getDatePosted());
					Long p2 = new Long(a2.getDatePosted());
					return p2.compareTo(p1);
				}
			};


			Collections.sort(populatedPosts, comparator);
			List<Post> userPosts = new ArrayList<Post>();

			request.getSession().setAttribute(Constants.ACTIVITY_REQUEST_TIME, utilities.getCurrentDate());

			for(Post post : populatedPosts){
				if(sessionManager.sessions.containsKey(post.getUsername())){
					post.setStatus("active");
				}else{
					post.setStatus("logged-out");
				}
				if(!post.isHidden() || post.isShared()){
					userPosts.add(post);
				}
			}

			return gson.toJson(userPosts);

		}catch(ParseException e){
			e.printStackTrace();
			return gson.toJson(responseData);
		}

	}



	@RequestMapping(value="/post/share", method=RequestMethod.POST, produces="application/json")
	public @ResponseBody String share(HttpServletRequest request,
									  @ModelAttribute("post") Post post,
									  @RequestParam(value="imageFiles", required = false) CommonsMultipartFile[] uploadedImageFiles,
									  @RequestParam(value="videoFile", required = false) CommonsMultipartFile uploadedVideoFile){


		Map<String, Object> data = new HashMap<String, Object>();
		Gson gson = new Gson();

		if(!authService.isAuthenticated()){
			data.put("error", "authentication required");
			return gson.toJson(data);
		}

		Account account = authService.getAccount();
		post.setAccountId(account.getId());

		List<String> imageUris = new ArrayList<String>();

		if(uploadedImageFiles != null &&
				uploadedImageFiles.length > 0) {

			for (CommonsMultipartFile imageFile : uploadedImageFiles){
				String imageUri = utilities.write(imageFile, Constants.IMAGE_DIRECTORY);

				if(imageUri.equals("")){
					utilities.deleteUploadedFile(imageUri);
				}
				else{
					imageUris.add(imageUri);
				}
			}
		}

		if(uploadedVideoFile != null  &&
				!uploadedVideoFile.isEmpty()) {

			String videoFileUri = utilities.writeVideo(uploadedVideoFile, Constants.VIDEO_DIRECTORY);

			if(videoFileUri.equals("")){
				utilities.deleteUploadedFile(videoFileUri);
				data.put("error", "video upload issue, check format");
				return gson.toJson(data);
			}else{
				post.setVideoFileUri(videoFileUri);
			}
		}

		if(post.getContent().contains("<style")){
			post.setContent(post.getContent().replace("style", "") + "<h1>We caught a hacker</h1>");
		}

		if(post.getContent().contains("<script")){
			post.setContent(post.getContent().replace("script", "") + "<h1>We caught a hacker</h1>");
		}


		if(post.getContent().contains("<iframe width=\"560\"")){
			post.setContent(post.getContent().replace("<iframe width=\"560\"" , "<iframe style=\"margin-top:-15px; margin-left:-30px;\" width=\"490\""));
		}


		List<String> youtubes = new ArrayList<String>();
		if(post.getContent().contains(YOUTUBE_URL) &&
				!post.getContent().contains("<iframe")) {

			Matcher matcher = urlPattern.matcher(post.getContent());
			while (matcher.find()) {
				int urlStart = matcher.start(1);
				int urlEnd = matcher.end();
				String url = post.getContent().substring(urlStart, urlEnd);
				if(url.contains(YOUTUBE_URL)){
					youtubes.add(url);
				}
			}
		}

		if(!youtubes.isEmpty()){
			for(int n = 0; n < 1; n++){
				String bad = youtubes.get(n);
				String good = bad.replace(YOUTUBE_URL, YOUTUBE_EMBED_URL);
				String better = StringUtils.stripEnd(good, ",");
				String best = StringUtils.stripEnd(better, ".");
				String refactor = StringUtils.stripEnd(best, "!");
				String embed = YOUTUBE_EMBED.replace("{{URL}}", refactor);
				post.setContent(post.getContent().replace(bad, embed));
			}
		}

		long date = utilities.getCurrentDate();
		post.setDatePosted(date);
		post.setUpdateDate(date);

		if(imageUris.size() == 0 && 
				(post.getVideoFileUri() == null || post.getVideoFileUri().equals("")) && 
				post.getContent().equals("")){
			data.put("error", true);
			return gson.toJson(data);
		}

		Post savedPost = postDao.save(post);
		accountDao.savePermission(account.getId(), Constants.POST_MAINTENANCE  + savedPost.getId());
//		postService.populatePost(savedPost, account, account);
		Post populatedPost = setPostData(savedPost);
//		postDao.update(populatedPost);



		for(String imageUri: imageUris){
			PostImage postImage = new PostImage();
			postImage.setPostId(populatedPost.getId());
			postImage.setUri(imageUri);
			postImage.setDate(date);
			postDao.saveImage(postImage);
		}
		populatedPost.setImageFileUris(imageUris);

        return gson.toJson(savedPost);
	}


	@RequestMapping(value="/image/delete/{id}", method=RequestMethod.POST, produces="application/json")
	public @ResponseBody String deleteImage(@PathVariable String id,
									@RequestParam(value="imageUri", required = true) String imageUri){

		Map<String, Object> response = new HashMap<String, Object>();
		Gson gson = new Gson();

		if(!authService.isAuthenticated()){
			response.put("error", "authentication required");
			String responseData = gson.toJson(response);
			return responseData;
		}

		String permission = Constants.POST_MAINTENANCE  + id;
		if(authService.hasPermission(permission)) {
			postDao.deletePostImage(Long.parseLong(id), imageUri);
			response.put("success", true);
		}else{
			response.put("error", "user does not have required permissions");
		}

		return gson.toJson(response);
	}


	@RequestMapping(value="/post/like/{id}", method=RequestMethod.POST,  produces="application/json")
	public @ResponseBody String like(ModelMap model,
									  HttpServletRequest request,
									  final RedirectAttributes redirect,
									 @PathVariable String id){

		Gson gson = new Gson();
		Map<String, Object> response = new HashMap<String, Object>();

		if(!authService.isAuthenticated()){
			response.put("error", "authentication required");
			String responseData = gson.toJson(response);
			return responseData;
		}
		Account account = authService.getAccount();


		PostLike postLike = new PostLike();
		postLike.setAccountId(account.getId());
		postLike.setPostId(Long.parseLong(id));

		boolean existingPostLike = postDao.liked(postLike);


		boolean success = false;
		
		Map<String, Object> data = new HashMap<String, Object>();
		Post post = postDao.get(Long.parseLong(id));
		Notification notification = notificationDao.getLikeNotification(Long.parseLong(id), post.getAccountId(), account.getId());
		if(existingPostLike) {
			data.put("action", "removed");
			success = postDao.unlike(postLike);
			if(notification != null){
				notificationDao.delete(notification.getId());
			}
		}
		else{
			long dateLiked = utilities.getCurrentDate();
			postLike.setDateLiked(dateLiked);

			success = postDao.like(postLike);
			data.put("action", "added");

			// log.info("notification: " + notification);
			if(notification == null) {
				createNotification(post.getAccountId(), account.getId(), Long.parseLong(id), true, false, false);
			}
		}

		long likes = postDao.likes(Long.parseLong(id));

		data.put("success", success);
		data.put("likes", likes);
		data.put("id", id);

		String json = gson.toJson(data);
		return json;
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
		long dateShared = utilities.getCurrentDate();

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

		PostShare savedPostShare = postDao.share(postShare);

		response.put("success", savedPostShare);

		String permission = Constants.POST_MAINTENANCE  + postShare.getAccountId() + ":" + savedPostShare.getId();
		accountDao.savePermission(account.getId(), permission);

		Post existingPost = postDao.get(Long.parseLong(id));

		createNotification(existingPost.getAccountId(), account.getId(), Long.parseLong(id), false, true, false);

		return gson.toJson(response);

	}


	@RequestMapping(value="/post/remove/{id}", method=RequestMethod.DELETE,  produces="application/json")
	public @ResponseBody String remove(ModelMap model,
									 HttpServletRequest request,
									 final RedirectAttributes redirect,
									 @PathVariable String id){

		Map<String, Object> data = new HashMap<String, Object>();
		Gson gson = new Gson();

		if(!authService.isAuthenticated()){
			data.put("error", "authentication required");
			return gson.toJson(data);
		}

		String permission = Constants.POST_MAINTENANCE  + id;
		if(authService.hasPermission(permission)) {

			Post post = postDao.get(Long.parseLong(id));
			postDao.hide(Long.parseLong(id));
			data.put("post", post);

		}else{
			data.put("error", "user doesn't have permission");
		}

		return gson.toJson(data);
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
			if(postDao.deletePostShareComments(Long.parseLong(id))){
				if(!postDao.deletePostShare(Long.parseLong(id))){
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
		long date = utilities.getCurrentDate();

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
		PostComment savedComment = postDao.savePostComment(postComment);


		accountDao.savePermission(account.getId(), Constants.COMMENT_MAINTENANCE  + savedComment.getId());

		Post post = postDao.get(Long.parseLong(id));

		createNotification(post.getAccountId(), account.getId(), Long.parseLong(id), false, false, true);

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
		long date = utilities.getCurrentDate();

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
		PostShareComment savedComment = postDao.savePostShareComment(postShareComment);

		accountDao.savePermission(account.getId(), Constants.COMMENT_MAINTENANCE  + savedComment.getId());
		PostShare postShare = postDao.getPostShare(Long.parseLong(id));

		createNotification(postShare.getAccountId(), account.getId(), Long.parseLong(id), false, false, true);

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
			postDao.deletePostComment(commentId);
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
			postDao.deletePostShareComment(commentId);
			data.put("success", true);
		}
		else{
			data.put("error", true);
		}

		return gson.toJson(data);

	}

	private void createNotification(long postAccountId, long authenticatedAccountId, long postId, boolean liked, boolean shared, boolean commented){
		Notification notification = new Notification();
		notification.setDateCreated(utilities.getCurrentDate());

		notification.setPostAccountId(postAccountId);
		notification.setAuthenticatedAccountId(authenticatedAccountId);
		notification.setPostId(postId);
		notification.setLiked(liked);
		notification.setShared(shared);
		notification.setCommented(commented);

		notificationDao.save(notification);
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
		hiddenPost.setDateHidden(utilities.getCurrentDate());

		postDao.makeInvisible(hiddenPost);

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

		Post post = postDao.get(Long.parseLong(id));
		post.setFlagged(true);

		PostFlag postFlag = new PostFlag();
		postFlag.setPostId(post.getId());
		postFlag.setAccountId(authService.getAccount().getId());
		postFlag.setDateFlagged(utilities.getCurrentDate());
		postFlag.setShared(shared);

		postDao.flagPost(postFlag);
		postDao.updateFlagged(post);

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

		List<Post> posts = postDao.getFlaggedPosts();
		// log.info("flagged : " + posts.size());

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

		Post post = postDao.getFlaggedPost(Long.parseLong(id));
		Account account = accountDao.get(post.getAccountId());
		Post populatedPost = postService.populatePost(post, account, authService.getAccount());
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

		Post post = postDao.get(Long.parseLong(id));
		Account account = accountDao.get(post.getAccountId());

		account.setDisabled(true);
		accountDao.updateDisabled(account);

		postDao.delete(post.getId());
		postDao.removePostShares(post.getId());
		postDao.removePostFlags(post.getId());

		List<PostImage> postImages = postDao.getImages(post.getId());
		for(PostImage postImage : postImages){
			postDao.deletePostImage(postImage.getId());
			utilities.deleteUploadedFile(postImage.getUri());
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

		Post post = postDao.get(Long.parseLong(id));
		Account account = accountDao.get(post.getAccountId());

		account.setDisabled(false);
		accountDao.updateDisabled(account);

		post.setFlagged(false);
		postDao.updateFlagged(post);
		postDao.removePostFlags(post.getId());

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
					String imageUri = utilities.write(imageFile, Constants.IMAGE_DIRECTORY);

					if(imageUri.equals("")){
						utilities.deleteUploadedFile(imageUri);
					}
					else{
						imageUris.add(imageUri);
					}
				}
			}

			Post post = postDao.get(Long.parseLong(id));

			for(String imageUri: imageUris){
				PostImage postImage = new PostImage();
				postImage.setPostId(post.getId());
				postImage.setUri(imageUri);
				postImage.setDate(utilities.getCurrentDate());
				postDao.saveImage(postImage);
			}

			response.put("success", true);

		}else{
			response.put("error", "user does not have required permissions");
		}

		return gson.toJson(response);
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
			postDao.publish(Long.parseLong(id));
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

		Gson gson = new Gson();
		Map<String, Object> resp = new HashMap<String, Object>();

		if(!authService.isAuthenticated()){
			resp.put("error", "authentication required");
			return gson.toJson(resp);
		}

		String permission = Constants.POST_MAINTENANCE  + id;
		if(authService.hasPermission(permission)) {

			if(post.getContent().contains("<style")){
				post.setContent(post.getContent().replace("style", "") + "<h1>We caught a hacker</h1>");
			}

			if(post.getContent().contains("<script")){
				post.setContent(post.getContent().replace("script", "") + "<h1>We caught a hacker</h1>");
			}

			List<String> youtubes = new ArrayList<String>();
			if(post.getContent().contains(YOUTUBE_URL) &&
					!post.getContent().contains("<iframe")) {

				Matcher matcher = urlPattern.matcher(post.getContent());
				while (matcher.find()) {
					int urlStart = matcher.start(1);
					int urlEnd = matcher.end();
					String url = post.getContent().substring(urlStart, urlEnd);
					if(url.contains(YOUTUBE_URL)){
						youtubes.add(url);
					}
				}
			}

			if(!youtubes.isEmpty()){
				int max = youtubes.size() <= 4 ? youtubes.size() : 4;
				for(int n = 0; n < 1; n++){
					String bad = youtubes.get(n);
					String good = bad.replace(YOUTUBE_URL, YOUTUBE_EMBED_URL);
					String better = StringUtils.stripEnd(good, ",");
					String best = StringUtils.stripEnd(better, ".");
					String refactor = StringUtils.stripEnd(best, "!");
					String embed = YOUTUBE_EMBED.replace("{{URL}}", refactor);
					post.setContent(post.getContent().replace(bad, embed));
				}
			}

			long date = utilities.getCurrentDate();
			post.setUpdateDate(date);

			postDao.update(post);
			resp.put("post", post);
		}else{
			resp.put("error", true);
		}
		return gson.toJson(resp);
	}


}