package social.amadeus.service;

import com.amazonaws.services.s3.model.PutObjectResult;
import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.ocpsoft.prettytime.PrettyTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import social.amadeus.common.Constants;
import social.amadeus.common.SessionManager;
import social.amadeus.common.Utils;
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
    private SessionManager sessionManager;


    public Post getPost(String id){
        Post postPre = postRepo.get(Long.parseLong(id));
        Account authdAccount = authService.getAccount();
        Post post = setPostData(postPre, authdAccount);
        return post;
    }

    public Post savePost(Post post, CommonsMultipartFile[] imageFiles, CommonsMultipartFile videoFile){

        if(!authService.isAuthenticated()){
            post.setFailMessage("authentication required");
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
                    post.setFailMessage("Video file must be mp4.");
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
            post.setFailMessage("everything is blank");
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

        return Constants.SUCCESS_MESSAGE;
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
            post.setFailMessage(Constants.AUTHENTICATION_REQUIRED);
            return post;
        }

        String permission = Constants.POST_MAINTENANCE  + id;
        if(!authService.hasPermission(permission)) {
            post.setFailMessage(Constants.REQUIRES_PERMISSION);
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
        if(!authService.hasPermission(permission)) {
            return Constants.REQUIRES_PERMISSION;
        }

        Post post = postRepo.get(Long.parseLong(id));
        postRepo.hide(Long.parseLong(id));
        return Constants.SUCCESS_MESSAGE;
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
        return Constants.SUCCESS_MESSAGE;
    }


    public Map<String, Object> likePost(String id){
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

        long likes = postRepo.likes(Long.parseLong(id));
        Map<String, Object> respData = new HashMap<>();
        respData.put("likes", likes);
        respData.put("id", id);
        return respData;
    }


    public List<Post> getUserActivity(Account profileAccount, Account authdAccount){
        List<Post> postsPre = postRepo.getUserPosts(profileAccount.getId());
        List<Post> posts = populatePostData(postsPre, authdAccount);

        List<PostShare> postSharesPre = postRepo.getUserPostShares(profileAccount.getId());
        List<Post> postsShares = getPopulatedSharedPosts(authdAccount, postSharesPre);

        List<Post> activityFeedPre = new ArrayList<>();
        activityFeedPre.addAll(posts);
        activityFeedPre.addAll(postsShares);
        List<Post> activityFeedSorted = getSortedActivities(activityFeedPre);

        activityFeedSorted.stream().forEach(post -> setActive(post));

        return activityFeedSorted;
    }

    public Map<String, Object> getActivity(Account authdAccount){

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

        List<Account> femalesMales = getFemalesMales(activityFeed);

        Map<String, Object> data = new HashMap<>();
        data.put("activities", activityFeedSorted);
        data.put("femsfellas", femalesMales);

        return data;
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

        return Constants.SUCCESS_MESSAGE;
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
        if (sessionManager.sessions.containsKey(post.getUsername())) {
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
            if (sessionManager.sessions.containsKey(post.getUsername())) {
                account.setStatus("online");
            }
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





//    public Map<String, Object> getActivityD(Account authenticatedAccount, HttpServletRequest request){
//
//        Map<String, Object> activityData = new HashMap<>();
//
//        try {
//
//            long start = utilities.getPreviousDay(14);
//            long end = utilities.getCurrentDate();
//
//            List<Post> posts = postDao.getActivity(start, end, authenticatedAccount.getId());
//
//            for (Post post : posts) {
//                Account postedAccount = accountDao.get(post.getAccountId());
//                populatePost(post, postedAccount, authenticatedAccount);
//            }
//
//            List<PostShare> postShares = postDao.getPostShares(start, end, authenticatedAccount.getId());
//
//            for (PostShare postShare : postShares) {
//                Post post = postDao.get(postShare.getPostId());
//                post.setPostShareId(postShare.getId());
//
//                Account postedAccount = accountDao.get(post.getAccountId());
//
//                populatePostShare(post, postShare, postedAccount, authenticatedAccount);
//
//                post.setShared(true);
//                post.setSharedComment(postShare.getComment());
//
//                Account acc = accountDao.get(postShare.getAccountId());
//                post.setSharedAccountId(postShare.getAccountId());
//                post.setSharedAccount(acc.getName());
//                post.setSharedImageUri(acc.getImageUri());
//
//                SimpleDateFormat format = new SimpleDateFormat(Constants.DATE_SEARCH_FORMAT);
//                Date date = format.parse(Long.toString(postShare.getDateShared()));
//
//                PrettyTime p = new PrettyTime();
//                post.setTimeSharedAgo(p.format(date));
//                post.setDatePosted(postShare.getDateShared());
//
//                if (postShare.getAccountId() == authenticatedAccount.getId()) {
//                    post.setDeletable(true);
//                }
//
//                if (postShare.getAccountId() == authenticatedAccount.getId()) {
//                    post.setPostShareEditable(true);
//                }
//
//                posts.add(post);
//            }
//
//
//            Comparator<Post> comparator = new Comparator<Post>() {
//                @Override
//                public int compare(Post a1, Post a2) {
//                    Long p1 = new Long(a1.getDatePosted());
//                    Long p2 = new Long(a2.getDatePosted());
//                    return p2.compareTo(p1);
//                }
//            };
//
//
//            Collections.sort(posts, comparator);
//            List<Post> finalFeed = new ArrayList<Post>();
//
//            request.getSession().setAttribute(Constants.ACTIVITY_REQUEST_TIME, utilities.getCurrentDate());
//
//            for (Post post : posts) {
//                if (sessionManager.sessions.containsKey(post.getUsername())) {
//                    post.setStatus("active");
//                } else {
//                    post.setStatus("inactive");
//                }
//                finalFeed.add(post);
//            }
//
//            Random rand = new Random();
//
//            int adIdx = rand.nextInt(2);
//            if (adIdx == 1) {
//
//                List<Flyer> activeFlyers = flyerDao.getActiveFlyers();
//
//                int flyerIdx = 0;
//                if (activeFlyers.size() > 0) {
//
//                    flyerIdx = rand.nextInt(activeFlyers.size());
//
//                    Flyer flyer = activeFlyers.get(flyerIdx);
//
//                    Post adPost = new Post();
//
//                    List<String> imageUris = new ArrayList<>();
//                    imageUris.add(flyer.getImageUri());
//
//                    adPost.setImageFileUris(imageUris);
//                    adPost.setAdvertisementUri(flyer.getPageUri());
//                    adPost.setAdvertisement(true);
//                    adPost.setShared(false);
//                    adPost.setHidden(false);
//                    adPost.setFlagged(false);
//                    adPost.setPublished(true);
//
//                    if (finalFeed.size() > 0) {
//                        int feedIdx = rand.nextInt(finalFeed.size());
//                        finalFeed.add(feedIdx, adPost);
//                    } else {
//                        finalFeed.add(adPost);
//                    }
//                    long views = flyer.getAdViews() + 1;
//                    flyerDao.updateViews(views, flyer.getId());
//                }
//            }
//
//            Map<Long, Account> femsfellasMap = new HashMap<Long, Account>();
//            List<Account> femsfellas = new ArrayList<Account>();
//            for (Post post : posts) {
//                Account femfella = new Account();
//                femfella.setId(post.getAccountId());
//                femfella.setName(post.getName());
//                femfella.setImageUri(post.getImageUri());
//                if (femsfellasMap.containsKey(post.getAccountId())) {
//                    int count = femsfellasMap.get(post.getAccountId()).getCount() + 1;
//                    femfella.setCount(count);
//                    femsfellasMap.put(post.getAccountId(), femfella);
//                } else {
//                    femfella.setCount(1);
//                    femsfellasMap.put(post.getAccountId(), femfella);
//                }
//            }
//
//            for (Account femfella : femsfellasMap.values()) {
//                femsfellas.add(femfella);
//            }
//
//
//            activityData.put("activities", finalFeed);
//            activityData.put("femsfellas", femsfellas);
//
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//
//        return activityData;
//    }
//
//
//
//    public Post populatePost(Post post, Account postAccount, Account authenticatedAccount){
//        try {
//
//            SimpleDateFormat format = new SimpleDateFormat(Constants.DATE_SEARCH_FORMAT);
//            Date date = format.parse(Long.toString(post.getDatePosted()));
//
//            PrettyTime p = new PrettyTime();
//            post.setTimeAgo(p.format(date));
//
//            long likes = postDao.likes(post.getId());
//            post.setLikes(likes);
//
//            PostLike postLike = new PostLike();
//            postLike.setPostId(post.getId());
//            postLike.setAccountId(authenticatedAccount.getId());
//            boolean existingPostLike = postDao.liked(postLike);
//            if (existingPostLike) {
//                post.setLiked(true);
//            }
//
//            long shares = postDao.shares(post.getId());
//            post.setShares(shares);
//
//            post.setAccountId(postAccount.getId());
////            post.setImageUri(postAccount.getImageUri());
////            post.setName(postAccount.getName());
////            post.setUsername(postAccount.getUsername());
//
//            if(post.getAccountId() == authenticatedAccount.getId()){
//                post.setDeletable(true);
//                post.setPostEditable(true);
//            }else{
//                post.setDeletable(false);
//            }
//
//            List<PostComment> postComments = postDao.getPostComments(post.getId());
//
//            for (PostComment postComment : postComments) {
//                if(postComment.getAccountId() == authenticatedAccount.getId()){
//                    postComment.setCommentDeletable(true);
//                }
//                postComment.setCommentId(postComment.getId());//used for front end
//            }
//
//            post.setComments(postComments);
//
////            List<HiddenPost> hiddenPosts = postDao.getHiddenPosts(post.getId(), authenticatedAccount.getId());
////            if(hiddenPosts.size() > 0){
////                post.setHidden(true);
////            }
//
//            if(postComments.size() > 0)post.setCommentsOrShareComments(true);
//
//            retrieveMultimedia(post);
//
//        }catch(Exception e){ }
//
//        return post;
//    }
//
//
//    public Post populatePostShare(Post post, PostShare postShare, Account postAccount, Account authenticatedAccount){
//        try {
//
//            //this and previous written in a hurry, needs help.
//            SimpleDateFormat format = new SimpleDateFormat(Constants.DATE_SEARCH_FORMAT);
//            Date date = format.parse(Long.toString(post.getDatePosted()));
//
//            PrettyTime p = new PrettyTime();
//            post.setTimeAgo(p.format(date));
//
//            long likes = postDao.likes(post.getId());
//            post.setLikes(likes);
//
//            PostLike postLike = new PostLike();
//            postLike.setPostId(post.getId());
//            postLike.setAccountId(postAccount.getId());
//            boolean existingPostLike = postDao.liked(postLike);
//            if (existingPostLike) {
//                post.setLiked(true);
//            }
//
//            long shares = postDao.shares(post.getId());
//            post.setShares(shares);
//
//            post.setAccountId(postAccount.getId());
//            post.setImageUri(postAccount.getImageUri());
//            post.setName(postAccount.getName());
//            post.setUsername(postAccount.getUsername());
//
//            if(post.getAccountId() == authenticatedAccount.getId()){
//                post.setDeletable(true);
//            }else{
//                post.setDeletable(false);
//            }
//
//            List<PostShareComment> postShareComments = postDao.getPostShareComments(postShare.getId());
//
//            for (PostShareComment postShareComment : postShareComments) {
//                if(postShareComment.getAccountId() == authenticatedAccount.getId()){
//                    postShareComment.setCommentDeletable(true);
//                }
//                postShareComment.setCommentId(postShareComment.getId());
//            }
//
//            post.setShareComments(postShareComments);
//
//            List<HiddenPost> hiddenPosts = postDao.getHiddenPosts(post.getId(), authenticatedAccount.getId());
//            if(hiddenPosts.size() > 0){
//                post.setHidden(true);
//            }
//
//            if(postShareComments.size() > 0)post.setCommentsOrShareComments(true);
//
//            retrieveMultimedia(post);
//
//        }catch(Exception e){ }
//
//        return post;
//    }
//
//
//    private Post retrieveMultimedia(Post post){
//        List<PostImage> postImages = postDao.getImages(post.getId());
//        List<String> imageUris = new ArrayList<String>();
//
//        for(PostImage postImage : postImages){
//            imageUris.add(postImage.getUri());
//        }
//        post.setImageFileUris(imageUris);
//
//        return post;
//    }
}
