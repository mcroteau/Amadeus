package social.amadeus.service;

import org.apache.log4j.Logger;
import org.ocpsoft.prettytime.PrettyTime;
import org.springframework.beans.factory.annotation.Autowired;
import social.amadeus.common.Constants;
import social.amadeus.common.SessionManager;
import social.amadeus.common.Utilities;
import social.amadeus.dao.AccountDao;
import social.amadeus.dao.FlyerDao;
import social.amadeus.dao.PostDao;
import social.amadeus.model.*;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.*;

public class PostService {

    private static final Logger log = Logger.getLogger(PostService.class);


    @Autowired
    private Utilities utilities;

    @Autowired
    private PostDao postDao;

    @Autowired
    private AccountDao accountDao;

    @Autowired
    private FlyerDao flyerDao;

    @Autowired
    private AuthService authService;

    @Autowired
    private SessionManager sessionManager;

    public Map<String, Object> getActivity(Account authenticatedAccount, HttpServletRequest request){

        Map<String, Object> activityData = new HashMap<>();

        try {

            long start = utilities.getPreviousDay(14);
            long end = utilities.getCurrentDate();

            List<Post> posts = postDao.getActivity(start, end, authenticatedAccount.getId());

            for (Post post : posts) {
                Account postedAccount = accountDao.get(post.getAccountId());
                populatePost(post, postedAccount, authenticatedAccount);
            }

            List<PostShare> postShares = postDao.getPostShares(start, end, authenticatedAccount.getId());

            for (PostShare postShare : postShares) {
                Post post = postDao.get(postShare.getPostId());
                post.setPostShareId(postShare.getId());

                Account postedAccount = accountDao.get(post.getAccountId());

                populatePostShare(post, postShare, postedAccount, authenticatedAccount);

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

                if (postShare.getAccountId() == authenticatedAccount.getId()) {
                    post.setDeletable(true);
                }

                if (postShare.getAccountId() == authenticatedAccount.getId()) {
                    post.setPostShareEditable(true);
                }

                posts.add(post);
            }


            Comparator<Post> comparator = new Comparator<Post>() {
                @Override
                public int compare(Post a1, Post a2) {
                    Long p1 = new Long(a1.getDatePosted());
                    Long p2 = new Long(a2.getDatePosted());
                    return p2.compareTo(p1);
                }
            };


            Collections.sort(posts, comparator);
            List<Post> finalFeed = new ArrayList<Post>();

            request.getSession().setAttribute(Constants.ACTIVITY_REQUEST_TIME, utilities.getCurrentDate());

            for (Post post : posts) {
                if (sessionManager.sessions.containsKey(post.getUsername())) {
                    post.setStatus("active");
                } else {
                    post.setStatus("inactive");
                }
                finalFeed.add(post);
            }

            Random rand = new Random();

            int adIdx = rand.nextInt(2);
            if (adIdx == 1) {

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
                    adPost.setShared(false);
                    adPost.setHidden(false);
                    adPost.setFlagged(false);
                    adPost.setPublished(true);

                    if (finalFeed.size() > 0) {
                        int feedIdx = rand.nextInt(finalFeed.size());
                        finalFeed.add(feedIdx, adPost);
                    } else {
                        finalFeed.add(adPost);
                    }
                    long views = flyer.getAdViews() + 1;
                    flyerDao.updateViews(views, flyer.getId());
                }
            }

            Map<Long, Account> femsfellasMap = new HashMap<Long, Account>();
            List<Account> femsfellas = new ArrayList<Account>();
            for (Post post : posts) {
                Account femfella = new Account();
                femfella.setId(post.getAccountId());
                femfella.setName(post.getName());
                femfella.setImageUri(post.getImageUri());
                if (femsfellasMap.containsKey(post.getAccountId())) {
                    int count = femsfellasMap.get(post.getAccountId()).getCount() + 1;
                    femfella.setCount(count);
                    femsfellasMap.put(post.getAccountId(), femfella);
                } else {
                    femfella.setCount(1);
                    femsfellasMap.put(post.getAccountId(), femfella);
                }
            }

            for (Account femfella : femsfellasMap.values()) {
                femsfellas.add(femfella);
            }


            activityData.put("activities", finalFeed);
            activityData.put("femsfellas", femsfellas);

        }catch (Exception e){
            e.printStackTrace();
        }

        return activityData;
    }



    public Post populatePost(Post post, Account postAccount, Account authenticatedAccount){
        try {

            SimpleDateFormat format = new SimpleDateFormat(Constants.DATE_SEARCH_FORMAT);
            Date date = format.parse(Long.toString(post.getDatePosted()));

            PrettyTime p = new PrettyTime();
            post.setTimeAgo(p.format(date));

            long likes = postDao.likes(post.getId());
            post.setLikes(likes);

            PostLike postLike = new PostLike();
            postLike.setPostId(post.getId());
            postLike.setAccountId(authenticatedAccount.getId());
            boolean existingPostLike = postDao.liked(postLike);
            if (existingPostLike) {
                post.setLiked(true);
            }

            long shares = postDao.shares(post.getId());
            post.setShares(shares);

            post.setAccountId(postAccount.getId());
            post.setImageUri(postAccount.getImageUri());
            post.setName(postAccount.getName());
            post.setUsername(postAccount.getUsername());

            if(post.getAccountId() == authenticatedAccount.getId()){
                post.setDeletable(true);
                post.setPostEditable(true);
            }else{
                post.setDeletable(false);
            }

            List<PostComment> postComments = postDao.getPostComments(post.getId());

            for (PostComment postComment : postComments) {
                if(postComment.getAccountId() == authenticatedAccount.getId()){
                    postComment.setCommentDeletable(true);
                }
                postComment.setCommentId(postComment.getId());//used for front end
            }

            post.setComments(postComments);

            List<HiddenPost> hiddenPosts = postDao.getHiddenPosts(post.getId(), authenticatedAccount.getId());
            if(hiddenPosts.size() > 0){
                post.setHidden(true);
            }

            if(postComments.size() > 0)post.setCommentsOrShareComments(true);

            retrieveMultimedia(post);

        }catch(Exception e){ }

        return post;
    }


    public Post populatePostShare(Post post, PostShare postShare, Account postAccount, Account authenticatedAccount){
        try {

            //this and previous written in a hurry, needs help.
            SimpleDateFormat format = new SimpleDateFormat(Constants.DATE_SEARCH_FORMAT);
            Date date = format.parse(Long.toString(post.getDatePosted()));

            PrettyTime p = new PrettyTime();
            post.setTimeAgo(p.format(date));

            long likes = postDao.likes(post.getId());
            post.setLikes(likes);

            PostLike postLike = new PostLike();
            postLike.setPostId(post.getId());
            postLike.setAccountId(postAccount.getId());
            boolean existingPostLike = postDao.liked(postLike);
            if (existingPostLike) {
                post.setLiked(true);
            }

            long shares = postDao.shares(post.getId());
            post.setShares(shares);

            post.setAccountId(postAccount.getId());
            post.setImageUri(postAccount.getImageUri());
            post.setName(postAccount.getName());
            post.setUsername(postAccount.getUsername());

            if(post.getAccountId() == authenticatedAccount.getId()){
                post.setDeletable(true);
            }else{
                post.setDeletable(false);
            }

            List<PostShareComment> postShareComments = postDao.getPostShareComments(postShare.getId());

            for (PostShareComment postShareComment : postShareComments) {
                if(postShareComment.getAccountId() == authenticatedAccount.getId()){
                    postShareComment.setCommentDeletable(true);
                }
                postShareComment.setCommentId(postShareComment.getId());
            }

            post.setShareComments(postShareComments);

            List<HiddenPost> hiddenPosts = postDao.getHiddenPosts(post.getId(), authenticatedAccount.getId());
            if(hiddenPosts.size() > 0){
                post.setHidden(true);
            }

            if(postShareComments.size() > 0)post.setCommentsOrShareComments(true);

            retrieveMultimedia(post);

        }catch(Exception e){ }

        return post;
    }


    private Post retrieveMultimedia(Post post){
        List<PostImage> postImages = postDao.getImages(post.getId());
        List<String> imageUris = new ArrayList<String>();

        for(PostImage postImage : postImages){
            imageUris.add(postImage.getUri());
        }
        post.setImageFileUris(imageUris);

        return post;
    }
}
