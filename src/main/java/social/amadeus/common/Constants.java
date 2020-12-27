package social.amadeus.common;

public class Constants {

	public static final String X_MESSAGE = "fail";
	public static final String SUCCESS = "complete";
	public static final String AUTHENTICATION_REQUIRED = "authentication required";
	public static final String REQUIRES_PERMISSION = "permission required";
	public static final String UNAUTHORIZED_REDIRECT = "redirect:/unauthorized";

	public static final String AMADEUS_GROUP = "Amadeus";
	public static final String PUBLISHING_JOB = "AmadeusPublishingJob";
	public static final String PUBLISHING_JOB_TRIGGER = "AmadeusPublishingTrigger";

	public static final String POSTS_DAO_KEY = "PostsDao";
	public static final int PUBLISH_JOB_DURATION = 60;
	public static final int PUBLISH_DURATION_DIFFERENCE  = 1000 * 60 * 3;

	public static final String AD_JOB = "AmadeusAdJob";
	public static final String AD_JOB_TRIGGER = "AmadeusAdTrigger";

	public static final String FLYER_DAO_KEY = "FlyerDao";
	public static final int AD_JOB_DURATION = 1000 * 60;
	public static final int AD_DURATION_DIFFERENCE = 1000 * 60 * 60 * 24 * 7;


	public static final String ACTIVITY_REQUEST_TIME = "activity-request-time";

	public static final String PASSWORD = "password";
	public static final String ADMIN_USERNAME = "croteau.mike@gmail.com";

	public static final String GUEST_USERNAME = "mjackson.guest@outlook.com";
	public static final String GUEST_PASSWORD = "guest123";

	public static final String ROLE_ADMIN   = "ROLE_ADMIN";
	public static final String ROLE_ACCOUNT = "ROLE_ACCOUNT";

	public static final String IMAGE_DIRECTORY  = "media/images/";
	public static final String VIDEO_DIRECTORY  = "media/video/";

    public static final String PROFILE_IMAGE_DIRECTORY = "media/profiles/";

    public static final String FRESCO                  = "2Wxt8sKms.jpg";
	public static final String DEFAULT_IMAGE_URI       = "tHMLqV9TU.png";
	public static final String DEFAULT_FLYER_IMAGE_URI = "k0nIVNObX.png";

	public static final String DELIMITER = ":";

	public static final String ACCOUNT_MAINTENANCE            = "permission:accounts:";
	public static final String POST_MAINTENANCE               = "permission:posts:";
	public static final String POST_SHARE_MAINTENANCE         = "permission:posts_shared:";
	public static final String POST_COMMENT_MAINTENANCE            = "permission:comments:";
	public static final String POST_SHARE_COMMENT_MAINTENANCE = "permission:post_share:comments:";
	public static final String FLYER_MAINTENANCE              = "permission:flyer:";

	public static final int RESULTS_PER_PAGE_MUSIC = 7;
	public static final int MOCK_ACCOUNTS          = 7;
	
    public static final String DATE_SEARCH_FORMAT  = "yyyyMMddHHmmssSSS";
	public static final String DATE_GRAPH_FORMAT  = "dd MMM";

	public static final String TEST = "Mock";

	public static final String HTTPS = "https://";
	public static final String DO_REGION = "sfo2";
	public static final String DO_ENDPOINT = "rad.sfo2.digitaloceanspaces.com";
}