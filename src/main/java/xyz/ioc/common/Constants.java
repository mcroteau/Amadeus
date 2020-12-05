package xyz.ioc.common;

public class Constants {

	public static int TRUE = 1;
	public static int FALSE = 0;

	public static final String AMADEUS_GROUP = "Amadeus";
	public static final String PUBLISHING_JOB_NAME = "AmadeusPublishing";
	public static final String PUBLISHING_JOB_TRIGGER = "AmadeusPublishingTrigger";

	public static final String PASSWORD = "password";
	public static final String ADMIN_USERNAME = "croteau.mike@gmail.com";

	public static final String GUEST_USERNAME = "mjackson.guest@outlook.com";
	public static final String GUEST_PASSWORD = "guest123";

	public static final String ROLE_ADMIN   = "ROLE_ADMIN";
	public static final String ROLE_ACCOUNT = "ROLE_ACCOUNT";

	public static final String IMAGE_DIRECTORY  = "media/images/";
	public static final String MUSIC_DIRECTORY  = "media/music/";
	public static final String VIDEO_DIRECTORY  = "media/video/";

    public static final String PROFILE_IMAGE_DIRECTORY = "media/profiles/";

    public static final String FRESCO            = "media/profiles/sebastien.jpg";
	public static final String DEFAULT_IMAGE_URI = "media/profiles/amadeus.png";

	public static final String ACCOUNT_MAINTENANCE = "permission:accounts:";
	public static final String POST_MAINTENANCE    = "permission:posts:";
	public static final String COMMENT_MAINTENANCE = "permission:comments:";

	public static final int RESULTS_PER_PAGE       = 10;
	public static final int RESULTS_PER_PAGE_MUSIC = 7;
	public static final int MAX_RECORDS_MUSIC      = 7;
	public static final int MOCK_ACCOUNTS          = 7;
	
    public static final String DATE_SEARCH_FORMAT  = "yyyyMMddHHmmssSSS";
	public static final String DATE_GRAPH_FORMAT  = "dd MMM";

    public static final String[] PRELOADED_PLAYLIST = {
			"media/samples/WhereEverIMayRoam.mp3" };
    
}