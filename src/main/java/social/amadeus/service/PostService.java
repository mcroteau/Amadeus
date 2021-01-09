package social.amadeus.service;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.ocpsoft.prettytime.PrettyTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import social.amadeus.common.Constants;
import social.amadeus.common.SessionManager;
import social.amadeus.common.Utils;
import social.amadeus.model.ActivityOutput;
import social.amadeus.model.LikesOutput;
import social.amadeus.repository.AccountRepo;
import social.amadeus.repository.FlyerRepo;
import social.amadeus.repository.NotificationRepo;
import social.amadeus.repository.PostRepo;
import social.amadeus.model.*;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PostService {

    private static final Logger log = Logger.getLogger(PostService.class);

    private static final String YOUTUBE_URL = "https://youtu.be";

    private static final String YOUTUBE_EMBED_URL = "https://youtube.com/embed";

    private static final String YOUTUBE_EMBED = "<iframe style=\"margin-left:-30px;\" width=\"465\" height=\"261\" src=\"{{URL}}\" frameborder=\"0\" allow=\"accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture\" allowfullscreen></iframe>";

    private static final Pattern urlPattern = Pattern.compile(
            "(?:^|[\\W])((ht|f)tp(s?):\\/\\/|www\\.)"
                    + "(([\\w\\-]+\\.){1,}?([\\w\\-.~]+\\/?)*"
                    + "[\\p{Alnum}.,%_=?&#\\-+()\\[\\]\\*$~@!:/{};']*)",
            Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);


    boolean notify = false;

    @Autowired
    private Utils utils;

    @Autowired
    private PostRepo postRepo;

    @Autowired
    private AccountRepo accountRepo;

    @Autowired
    private FlyerRepo flyerRepo;

    @Autowired
    private AuthService authService;

    @Autowired
    private NotificationRepo notificationRepo;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private SyncService syncService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private SessionManager sessionManager;


    public Post getPost(String id){
        Post postPre = postRepo.get(Long.parseLong(id));
        Account authdAccount = authService.getAccount();
        Post post = setPostData(postPre, authdAccount);
        return post;
    }

    public Post savePost(Post post, CommonsMultipartFile[] imageFiles, CommonsMultipartFile videoFile){

        if(!authService.isAuthenticated()){
            post.setStatusMessage(Constants.AUTHENTICATION_REQUIRED);
            return post;
        }

        Account authdAccount = authService.getAccount();

        Map<String, String> imageLookup = new HashMap<>();

        if(imageFiles != null &&
                imageFiles.length > 0) {
            synchronizeImages(imageFiles, imageLookup);
        }

        if(videoFile != null  &&
                !videoFile.isEmpty()) {

            try{
                String videoFileName = utils.generateFileName(videoFile);

                String[] contentTypes = new String[]{"video/mp4"};
                List<String> list = Arrays.asList(contentTypes);

                if(!utils.correctMimeType(list, videoFile)){
                    post.setStatusMessage("Video file must be mp4.");
                    return post;
                }else{
                    syncService.send(videoFileName, videoFile.getInputStream());
                    post.setVideoFileUri(Constants.HTTPS + Constants.DO_ENDPOINT + "/" + videoFileName);
                    post.setVideoFileName(videoFileName);
                }
            }catch(IOException ex){
                ex.printStackTrace();
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


        List<String> youtubes = new ArrayList<>();
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


        if(imageLookup.size() == 0 &&
                (post.getVideoFileUri() == null || post.getVideoFileUri().equals("")) &&
                post.getContent().equals("")){
            post.setStatusMessage("everything is blank");
            return post;
        }

        long date = utils.getCurrentDate();
        post.setDatePosted(date);
        post.setUpdateDate(date);
        post.setAccountId(authdAccount.getId());

        Post savedPost = postRepo.save(post);
        accountRepo.savePermission(authdAccount.getId(), Constants.POST_MAINTENANCE  + savedPost.getId());
        Post populatedPost = setPostData(savedPost, authdAccount);

        List<String> imageUris = savePostImages(populatedPost, imageLookup);
        populatedPost.setImageFileUris(imageUris);

        return savedPost;
    }


    public String publishPost(String id){
        if (!authService.isAuthenticated()) {
            return Constants.AUTHENTICATION_REQUIRED;
        }

        String permission = Constants.POST_MAINTENANCE + id;
        if (!authService.hasPermission(permission)) {
            return Constants.REQUIRES_PERMISSION;
        }
        postRepo.publish(Long.parseLong(id));

        return Constants.SUCCESS;
    }


    public PostImage getPostImage(String fileName, String imageUri, Post populatedPost){
        PostImage postImage = new PostImage();
        postImage.setPostId(populatedPost.getId());
        postImage.setUri(imageUri);
        postImage.setFileName(fileName);
        postImage.setDate(utils.getCurrentDate());
        return postImage;
    }


    public Post updatePost(String id, Post post){
        if (!authService.isAuthenticated()) {
            post.setStatusMessage(Constants.AUTHENTICATION_REQUIRED);
            return post;
        }

        String permission = Constants.POST_MAINTENANCE  + id;
        if(!authService.hasPermission(permission)) {
            post.setStatusMessage(Constants.REQUIRES_PERMISSION);
            return post;
        }

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

        long date = utils.getCurrentDate();
        post.setUpdateDate(date);

        postRepo.update(post);
        return post;
    }


    public String deletePost(String id){

        if(!authService.isAuthenticated()){
            return Constants.AUTHENTICATION_REQUIRED;
        }

        String permission = Constants.POST_MAINTENANCE  + id;
        if(!authService.isAdministrator() &&
                !authService.hasPermission(permission)) {
            return Constants.REQUIRES_PERMISSION;
        }

        Post post = postRepo.get(Long.parseLong(id));
        postRepo.hide(Long.parseLong(id));
        return Constants.SUCCESS;
    }


    public String sharePost(String id, PostShare postShare){

        if(!authService.isAuthenticated()){
            return Constants.AUTHENTICATION_REQUIRED;
        }

        Account authdAccount = authService.getAccount();
        postShare.setAccountId(authdAccount.getId());
        postShare.setPostId(Long.parseLong(id));
        postShare.setDateShared(utils.getCurrentDate());

        postShare.setComment(postShare.getComment());
        if(postShare.getComment().contains("<style")){
            postShare.setComment(postShare.getComment().replace("style", "") + "We caught a hacker!");
        }

        if(postShare.getComment().contains("<script")){
            postShare.setComment(postShare.getComment().replace("script", "") + "We caught a hacker!");
        }

        PostShare savedPostShare = postRepo.sharePost(postShare);

        String permission = Constants.POST_SHARE_MAINTENANCE  + Constants.DELIMITER + savedPostShare.getId();
        accountRepo.savePermission(authdAccount.getId(), permission);

        Post existingPost = postRepo.get(Long.parseLong(id));

        Notification notification = notificationService.createNotification(existingPost.getAccountId(), authdAccount.getId(), Long.parseLong(id), false, true, false);
        notificationRepo.save(notification);

        return Constants.SUCCESS;
    }


    public String unsharePost(String id){

        if(!authService.isAuthenticated()){
            return Constants.AUTHENTICATION_REQUIRED;
        }

        String permission = Constants.POST_SHARE_MAINTENANCE  + Constants.DELIMITER + id;
        if(!authService.hasPermission(permission)) {
            return Constants.REQUIRES_PERMISSION;
        }

        if(postRepo.deletePostShareComments(Long.parseLong(id))){
            if(!postRepo.deletePostShare(Long.parseLong(id)))
                return Constants.X_MESSAGE;
        }
        return Constants.SUCCESS;
    }


    public String hidePost(String id) {

        if(!authService.isAuthenticated()){
            return Constants.AUTHENTICATION_REQUIRED;
        }

        Account authdAccount = authService.getAccount();
        HiddenPost hiddenPost = new HiddenPost();
        hiddenPost.setAccountId(authdAccount.getId());
        hiddenPost.setPostId(Long.parseLong(id));
        hiddenPost.setDateHidden(utils.getCurrentDate());

        postRepo.makeInvisible(hiddenPost);
        postRepo.hide(Long.parseLong(id));

        return Constants.SUCCESS;
    }

    public String flagPost(String id, boolean shared){

        if(!authService.isAuthenticated()){
            return Constants.AUTHENTICATION_REQUIRED;
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

        if(notify) {
            String body = "<h1>Amadeus</h1>"+
                    "<p>" + post.getContent() + "</p>" +
                    "<p><a href=\"astrophysical.love\">Signin</a></p>";
            emailService.send(Constants.ADMIN_USERNAME, "It ain't good...", body);
        }

        return Constants.SUCCESS;
    }


    public String savePostComment(String id, PostComment postComment){

        if(!authService.isAuthenticated()){
            return Constants.AUTHENTICATION_REQUIRED;
        }

        Account authdAccount = authService.getAccount();
        if(postComment.getComment() == null ||
                postComment.getComment().equals("")){
            return Constants.X_MESSAGE;
        }

        postComment.setPostId(Long.parseLong(id));
        postComment.setAccountId(authdAccount.getId());
        postComment.setDateCreated(utils.getCurrentDate());

        if(postComment.getComment().contains("<style")){
            postComment.setComment(postComment.getComment().replace("style", "") + "We caught a hacker!");
        }
        if(postComment.getComment().contains("<script")){
            postComment.setComment(postComment.getComment().replace("script", "") + "We caught a hacker!");
        }

        PostComment savedComment = postRepo.savePostComment(postComment);

        String permission = Constants.POST_COMMENT_MAINTENANCE  + savedComment.getId();
        accountRepo.savePermission(authdAccount.getId(), permission);

        Post post = postRepo.get(Long.parseLong(id));

        Notification notification = notificationService.createNotification(post.getAccountId(), authdAccount.getId(), Long.parseLong(id), false, false, true);
        notificationRepo.save(notification);

        return Constants.SUCCESS;
    }

    public String deletePostComment(String id){
        if(!authService.isAuthenticated()){
            return Constants.AUTHENTICATION_REQUIRED;
        }

        String permission = Constants.POST_COMMENT_MAINTENANCE + id;
        if(!authService.hasPermission(permission)){
            return Constants.REQUIRES_PERMISSION;
        }

        postRepo.deletePostComment(Long.parseLong(id));
        return Constants.SUCCESS;
    }


    public String savePostShareComment(String id, PostShareComment postShareComment){

        if(!authService.isAuthenticated()){
            return Constants.AUTHENTICATION_REQUIRED;
        }

        if(postShareComment.getComment() == null ||
                postShareComment.getComment().equals("")){
            return Constants.X_MESSAGE;
        }

        Account authdAccount = authService.getAccount();
        PostShare postShare = postRepo.getPostShare(Long.parseLong(id));

        postShareComment.setPostShareId(Long.parseLong(id));
        postShareComment.setAccountId(authdAccount.getId());

        if(postShareComment.getComment().contains("<style")){
            postShareComment.setComment(postShareComment.getComment().replace("style", "") + "We caught a hacker!");
        }
        if(postShareComment.getComment().contains("<script")){
            postShareComment.setComment(postShareComment.getComment().replace("script", "") + "We caught a hacker!");
        }

        postShareComment.setDateCreated(utils.getCurrentDate());
        PostShareComment savedComment = postRepo.savePostShareComment(postShareComment);

        accountRepo.savePermission(authdAccount.getId(), Constants.POST_SHARE_COMMENT_MAINTENANCE  + savedComment.getId());

        Notification notification = notificationService.createNotification(postShare.getAccountId(), authdAccount.getId(), postShare.getPostId(), false, false, true);
        notificationRepo.save(notification);

        return Constants.SUCCESS;
    }


    public String deletePostShareComment(String id){

        if(!authService.isAuthenticated()){
            return Constants.AUTHENTICATION_REQUIRED;
        }

        String permission = Constants.POST_SHARE_COMMENT_MAINTENANCE + id;
        if(!authService.hasPermission(permission)){
            return Constants.REQUIRES_PERMISSION;
        }

        postRepo.deletePostShareComment(Long.parseLong(id));
        return Constants.SUCCESS;
    }


    public String deletePostImage(String id, String imageUri){
        if(!authService.isAuthenticated()){
            return Constants.AUTHENTICATION_REQUIRED;
        }
        String permission = Constants.POST_MAINTENANCE  + id;
        if(!authService.hasPermission(permission)) {
            return Constants.REQUIRES_PERMISSION;
        }
        PostImage postImage = postRepo.getImage(Long.parseLong(id), imageUri);
        syncService.delete(postImage.getFileName());
        postRepo.deletePostImage(Long.parseLong(id), imageUri);
        return Constants.SUCCESS;
    }


    public LikesOutput likePost(String id){
        LikesOutput likesOutput = new LikesOutput();
        if(!authService.isAuthenticated()){
            likesOutput.setMessage(Constants.AUTHENTICATION_REQUIRED);
            return likesOutput;
        }

        Account authdAccount = authService.getAccount();

        PostLike postLike = new PostLike();
        postLike.setAccountId(authdAccount.getId());
        postLike.setPostId(Long.parseLong(id));

        boolean existingPostLike = postRepo.liked(postLike);
        Post post = postRepo.get(Long.parseLong(id));

        Notification notification = notificationRepo.getLikeNotification(Long.parseLong(id), post.getAccountId(), authdAccount.getId());

        if(existingPostLike) {
            postRepo.unlike(postLike);

            if(notification != null){
                notificationRepo.delete(notification.getId());
            }
        }else{
            long dateLiked = utils.getCurrentDate();
            postLike.setDateLiked(dateLiked);
            postRepo.like(postLike);

            if(notification == null) {
                Notification notificationPre = notificationService.createNotification(post.getAccountId(), authdAccount.getId(), Long.parseLong(id), true, false, false);
                notificationRepo.save(notificationPre);
            }
        }

        likesOutput.setMessage(Constants.SUCCESS);
        likesOutput.setId(Long.parseLong(id));
        likesOutput.setLikes(postRepo.likes(Long.parseLong(id)));

        return likesOutput;
    }


    public ActivityOutput getUserActivity(String userId){

        ActivityOutput activityOutput = new ActivityOutput();

        if(!authService.isAuthenticated()){
            activityOutput.setMessage(Constants.AUTHENTICATION_REQUIRED);
            activityOutput.setPosts(new ArrayList<>());
            return activityOutput;
        }

        Account authdAccount = authService.getAccount();
        Account profileAccount = accountRepo.get(Long.parseLong(userId));

        List<Post> postsPre = postRepo.getUserPosts(profileAccount.getId());
        List<Post> posts = populatePostData(postsPre, authdAccount);

        List<PostShare> postSharesPre = postRepo.getUserPostShares(profileAccount.getId());
        List<Post> postsShares = getPopulatedSharedPosts(authdAccount, postSharesPre);

        List<Post> activityFeedPre = new ArrayList<>();
        activityFeedPre.addAll(posts);
        activityFeedPre.addAll(postsShares);
        List<Post> activityFeedSorted = getSortedActivities(activityFeedPre);

        activityFeedSorted.stream().forEach(post -> setActive(post));
        activityOutput.setMessage(Constants.SUCCESS);
        activityOutput.setPosts(activityFeedSorted);

        return activityOutput;
    }

    public ActivityOutput getActivity(){

        ActivityOutput activityOutput = new ActivityOutput();

        if(!authService.isAuthenticated()){
            activityOutput.setMessage(Constants.AUTHENTICATION_REQUIRED);
            activityOutput.setPosts(new ArrayList<>());
            activityOutput.setAccounts(new ArrayList<>());
            return activityOutput;
        }

        Account authdAccount = authService.getAccount();

        long start = utils.getPreviousDay(14);
        long end = utils.getCurrentDate();

        List<Post> postsPre = postRepo.getActivity(start, end, authdAccount);
        List<Post> posts = populatePostData(postsPre, authdAccount);

        List<PostShare> postSharesPre = postRepo.getPostShares(start, end, authdAccount.getId());
        List<Post> sharedPosts = getPopulatedSharedPosts(authdAccount, postSharesPre);

        List<Post> activityFeedPre = new ArrayList<>();
        activityFeedPre.addAll(posts);
        activityFeedPre.addAll(sharedPosts);

        List<Post> activityFeed = getFlyerPosts(activityFeedPre);
        List<Post> activityFeedSorted = getSortedActivities(activityFeed);

        activityFeedSorted.stream().forEach(post -> setActive(post));

        List<Account> accounts = getFemalesMales(activityFeed);

        activityOutput.setMessage(Constants.SUCCESS);
        activityOutput.setPosts(activityFeedSorted);
        activityOutput.setAccounts(accounts);

        return activityOutput;
    }

    public String addPostImages(String id, CommonsMultipartFile[] imageFiles){

        if(!authService.isAuthenticated()){
            return Constants.AUTHENTICATION_REQUIRED;
        }

        String permission = Constants.POST_MAINTENANCE  + id;
        if(!authService.hasPermission(permission)) {
            return Constants.REQUIRES_PERMISSION;
        }

        Map<String, String> imageLookup = new HashMap<>();
        if(imageFiles != null &&
                imageFiles.length > 0) {
            synchronizeImages(imageFiles, imageLookup);
        }

        Post post = postRepo.get(Long.parseLong(id));
        savePostImages(post, imageLookup);

        return Constants.SUCCESS;
    }

    public List<String> savePostImages(Post post, Map<String, String> imageLookup){
        List<String> imageUris = new ArrayList<>();
        for (Map.Entry<String,String> image : imageLookup.entrySet()){
            PostImage postImage = getPostImage(image.getKey(), image.getValue(), post);
            postRepo.saveImage(postImage);
            imageUris.add(image.getValue());
        }
        return imageUris;
    }


    public Map<String, String> synchronizeImages(CommonsMultipartFile[] imageFiles, Map<String, String> imageLookup) {

        for (CommonsMultipartFile imageFile : imageFiles){
            try {
                String fileName = utils.generateFileName(imageFile);
                String imageUri = Constants.HTTPS + Constants.DO_ENDPOINT + "/" + fileName;

                syncService.send(fileName, imageFile.getInputStream());
                imageLookup.put(fileName, imageUri);
            }catch(IOException ex){
                ex.printStackTrace();
            }
        }

        return imageLookup;
    }


    private List<Post> getSortedActivities(List<Post> activityFeed){
        Comparator<Post> comparator = new Comparator<Post>() {
            @Override
            public int compare(Post a1, Post a2) {
                Long p1 = new Long(a1.getDatePosted());
                Long p2 = new Long(a2.getDatePosted());
                return p2.compareTo(p1);
            }
        };
        Collections.sort(activityFeed, comparator);
        return activityFeed;
    }

    private Post setActive(Post post){
        if (post.getUsername() != null &&
                sessionManager.sessions.containsKey(post.getUsername())) {
            post.setStatus("online");
        }
        return post;
    }

    private List<Post> populatePostData(List<Post> posts, Account authdAccount){
        posts.stream().forEach(post -> setPostData(post, authdAccount));
        return posts;
    }

    public Post setPostData(Post post, Account authdAccount){
        setTimeAgo(post);
        setLikes(post, authdAccount);
        setShares(post);
        setPostActions(post, authdAccount);
        setPostComments(post, authdAccount);
        setMultimedia(post);
        setAccountData(post);
        return post;
    }

    public Post setPostShareData(Post post, PostShare postShare, Account authdAccount){
        setBaseShared(post, postShare);
        setTimeAgo(post);
        setSharedTimeAgo(post, postShare);
        setLikes(post, authdAccount);
        setShares(post);
        setSharedPostActions(post, postShare, authdAccount);
        setPostShareComments(post, postShare, authdAccount);
        setMultimedia(post);
        setAccountData(post);
        setShareAccountData(post, postShare);
        return post;
    }

    private Post setBaseShared(Post post, PostShare postShare){
        post.setShared(true);
        post.setSharedComment(postShare.getComment());
        post.setPostShareId(postShare.getId());
        post.setDatePosted(postShare.getDateShared());
        return post;
    }


    private List<Post> getPopulatedSharedPosts(Account authdAccount, List<PostShare> postShares){
        List<Post> sharedPosts = new ArrayList<Post>();
        for(PostShare postShare: postShares){
            Post post = postRepo.get(postShare.getPostId());
            setPostShareData(post, postShare, authdAccount);
            sharedPosts.add(post);
        }
        return sharedPosts;
    }


    private Post setLikes(Post post, Account authdAccount){
        long likes = postRepo.likes(post.getId());
        post.setLikes(likes);

        PostLike postLike = new PostLike();
        postLike.setPostId(post.getId());
        postLike.setAccountId(authdAccount.getId());

        if (postRepo.liked(postLike)) post.setLiked(true);

        return post;
    }

    private Post setShares(Post post){
        long shares = postRepo.shares(post.getId());
        post.setShares(shares);
        return post;
    }

    private Post setPostActions(Post post, Account authdAccount){
        if(post.getAccountId() == authdAccount.getId()){
            post.setDeletable(true);
            post.setPostEditable(true);
        }
        return post;
    }

    private Post setSharedPostActions(Post post, PostShare postShare, Account authdAccount){
        if(postShare.getAccountId() == authdAccount.getId()){
            post.setDeletable(true);
        }
        return post;
    }

    private Post setPostComments(Post post, Account authdAccount){

        List<PostComment> postComments = postRepo.getPostComments(post.getId());
        for (PostComment postComment : postComments) {
            if(postComment.getAccountId() == authdAccount.getId()){
                postComment.setCommentDeletable(true);
            }
            postComment.setCommentId(postComment.getId());//used for front end
        }
        post.setComments(postComments);
        if(postComments.size() > 0)post.setCommentsOrShareComments(true);

        return post;
    }

    private Post setPostShareComments(Post post, PostShare postShare, Account authdAccount){
        List<PostShareComment> postShareComments = postRepo.getPostShareComments(postShare.getId());

        for (PostShareComment postShareComment : postShareComments) {
            if(postShareComment.getAccountId() == authdAccount.getId()){
                postShareComment.setCommentDeletable(true);
            }
            postShareComment.setCommentId(postShareComment.getId());
        }

        post.setShareComments(postShareComments);
        if(postShareComments.size() > 0)post.setCommentsOrShareComments(true);
        return post;
    }

    private Post setMultimedia(Post post){
        List<PostImage> postImages = postRepo.getImages(post.getId());
        List<String> imageUris = new ArrayList<String>();

        for(PostImage postImage : postImages){
            imageUris.add(postImage.getUri());
        }
        post.setImageFileUris(imageUris);

        return post;
    }


    private Post setShareAccountData(Post post, PostShare postShare){
        Account account = accountRepo.get(postShare.getAccountId());
        post.setSharedAccountId(account.getId());
        post.setSharedAccount(account.getName());
        post.setSharedImageUri(account.getImageUri());
        return post;
    }

    private Post setAccountData(Post post){
        Account account = accountRepo.get(post.getAccountId());
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

    private Post setSharedTimeAgo(Post post, PostShare postShare){
        try {
            SimpleDateFormat format = new SimpleDateFormat(Constants.DATE_SEARCH_FORMAT);
            Date date = format.parse(Long.toString(postShare.getDateShared()));

            PrettyTime prettyTime = new PrettyTime();
            post.setTimeSharedAgo(prettyTime.format(date));
        }catch(Exception e){
            e.printStackTrace();
        }
        return post;
    }


    private List<Post> getFlyerPosts(List<Post> activityFeed){
        Random rand = new Random();

        int adIdx = rand.nextInt(4);
        if (adIdx == 2) {
            List<Post> flyerPosts = new ArrayList<Post>();
            List<Flyer> activeFlyers = flyerRepo.getActiveFlyers();

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
                flyerRepo.updateViews(views, flyer.getId());
            }
        }
        return activityFeed;
    }


    private List<Account> getFemalesMales(List<Post> activityFeed){
        Map<Long, Account> femalesMalesMap = new HashMap<>();
        for (Post post : activityFeed) {
            Account account = new Account();
            account.setId(post.getAccountId());
            account.setName(post.getName());
            account.setImageUri(post.getImageUri());
            if(post.getUsername() != null &&
                    sessionManager.sessions.containsKey(post.getUsername())) {
                account.setStatus("online");
            }
            if(femalesMalesMap.containsKey(post.getAccountId())) {
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


    public String reviewFlaggedPosts(ModelMap modelMap){
        if(!authService.isAdministrator()){
            return Constants.UNAUTHORIZED_REDIRECT;
        }

        List<Post> posts = postRepo.getFlaggedPosts();
        modelMap.addAttribute("posts", posts);

        return "admin/flagged";
    }

    public String reviewFlaggedPost(String id, ModelMap modelMap){
        if(!authService.isAdministrator()){
            return Constants.UNAUTHORIZED_REDIRECT;
        }

        Post post = postRepo.getFlaggedPost(Long.parseLong(id));
        Account account = accountRepo.get(post.getAccountId());
        Post populatedPost = setPostData(post, account);
        modelMap.addAttribute("post", populatedPost);

        return "admin/review_post";
    }

    public String approveFlaggedPost(String id) {
        if(!authService.isAdministrator()){
            return Constants.UNAUTHORIZED_REDIRECT;
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

    public String revokeFlag(String id) {

        if(!authService.isAdministrator()){
            return Constants.UNAUTHORIZED_REDIRECT;
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
}
