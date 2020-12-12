package social.amadeus;

import org.apache.log4j.Logger;

import java.util.List;
import java.util.Random;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.core.env.Environment;
import social.amadeus.dao.*;
import social.amadeus.jobs.AdJob;
import social.amadeus.jobs.PublishJob;
import social.amadeus.model.*;
import social.amadeus.common.Constants;
import social.amadeus.common.Utilities;

import javax.annotation.PostConstruct;

public class AppRunner {

	private static final Logger log = Logger.getLogger(AppRunner.class);

	@Autowired
	public RoleDao roleDao;

	@Autowired
	public AccountDao accountDao;

	@Autowired
	public PostDao postDao;
	
	@Autowired
	public FriendDao friendDao;

	@Autowired
	public MessageDao messageDao;

	@Autowired
	public FlyerDao flyerDao;

	@Autowired
	public Utilities utilities;

	@Autowired
	private Environment env;


	@PostConstruct
	public void init() {
		createApplicationRoles();
		createApplicationAdministrator();
		createApplicationGuest();
		connectEm();

		if(!isTestEnvironment()) {
			startupBackgroundJobs();
		}
	}

	private boolean isTestEnvironment(){
		String[] profiles = env.getActiveProfiles();
		for(String profile: profiles){
			if(profile.equals("test")){
				return true;
			}
		}
		return false;
	}


	private void createApplicationRoles(){
		Role adminRole = roleDao.find(Constants.ROLE_ADMIN);
		Role accountRole = roleDao.find(Constants.ROLE_ACCOUNT);

		if(adminRole == null){
			adminRole = new Role();
			adminRole.setName(Constants.ROLE_ADMIN);
			roleDao.save(adminRole);
		}

		if(accountRole == null){
			accountRole = new Role();
			accountRole.setName(Constants.ROLE_ACCOUNT);
			roleDao.save(accountRole);
		}

		log.info("Roles : " + roleDao.count());
	}

	
	private void createApplicationAdministrator(){
		
		try{
			Account existing = accountDao.findByUsername(Constants.ADMIN_USERNAME);
			String password = io.github.mcroteau.resources.Constants.hash(Constants.PASSWORD);

			if(existing == null){
				Account admin = new Account();
				admin.setName("Sebastien");
				admin.setUsername(Constants.ADMIN_USERNAME);
				admin.setPassword(password);
				admin.setImageUri(Constants.FRESCO);
				accountDao.saveAdministrator(admin);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		log.info("Accounts : " + accountDao.count());
	}



	private void createApplicationGuest(){
		Account existing = accountDao.findByUsername(Constants.GUEST_USERNAME);
		String password = utilities.hash(Constants.GUEST_PASSWORD);

		if(existing == null){
			Account account = new Account();
			account.setName("Kelly");
			account.setUsername(Constants.GUEST_USERNAME);
			account.setPassword(password);
			account.setImageUri(Constants.DEFAULT_IMAGE_URI);
			accountDao.save(account);
		}
		log.info("Accounts : " + accountDao.count());
	}


	private void startupBackgroundJobs() {
		try {
			JobDetail publishJob = JobBuilder.newJob(PublishJob.class)
				.withIdentity(Constants.PUBLISHING_JOB, Constants.AMADEUS_GROUP).build();

			publishJob.getJobDataMap().put(Constants.POSTS_DAO_KEY, postDao);

			Trigger publishTrigger = TriggerBuilder
					.newTrigger()
					.withIdentity(Constants.PUBLISHING_JOB_TRIGGER, Constants.AMADEUS_GROUP)
					.withSchedule(
							SimpleScheduleBuilder.simpleSchedule()
									.withIntervalInSeconds(Constants.PUBLISH_JOB_DURATION).repeatForever())
					.build();

			Scheduler publishScheduler = new StdSchedulerFactory().getScheduler();

			JobKey pubKey = new JobKey(Constants.PUBLISHING_JOB, Constants.AMADEUS_GROUP);
			if(!publishScheduler.checkExists(pubKey)) {
				publishScheduler.start();
				publishScheduler.scheduleJob(publishJob, publishTrigger);
				log.info("publish job repeated " + Constants.PUBLISH_JOB_DURATION + " seconds");
			}





			JobDetail adJob = JobBuilder.newJob(AdJob.class)
				.withIdentity(Constants.AD_JOB, Constants.AMADEUS_GROUP).build();

			adJob.getJobDataMap().put(Constants.FLYER_DAO_KEY, flyerDao);

			Trigger adTrigger = TriggerBuilder
					.newTrigger()
					.withIdentity(Constants.AD_JOB_TRIGGER, Constants.AMADEUS_GROUP)
					.withSchedule(
							SimpleScheduleBuilder.simpleSchedule()
									.withIntervalInSeconds(Constants.AD_JOB_DURATION).repeatForever())
					.build();

			Scheduler adScheduler = new StdSchedulerFactory().getScheduler();
			JobKey adKey = new JobKey(Constants.AD_JOB, Constants.AMADEUS_GROUP);
			if(!adScheduler.checkExists(adKey)) {
				adScheduler.start();
				adScheduler.scheduleJob(adJob, adTrigger);
				log.info("ad job repeated " + Constants.AD_JOB_DURATION + " seconds");
			}

		}catch(Exception e){
			log.info("issue initializing job" + e.getMessage());
		}
	}


	private void generateAppData(){
		generateMockAccounts();
		generateMockFriendInvites();
		generateMockConnections();
		generateMockPosts();
		generateMockMessages();
	}


	private void generateMockAccounts(){
		long count = accountDao.count();
		if(count == 2){
			for(int m = 0; m < Constants.MOCK_ACCOUNTS; m++){
				Account account = new Account();
				String name = "Prometheus " + utilities.generateRandomString(9);
				account.setName(name);
				account.setUsername("croteau.mike+"+ m + "@gmail.com");
				account.setLocation(utilities.generateRandomString(7));
				account.setAge("33");
				String password = utilities.hash(Constants.PASSWORD);
				account.setPassword(password);
				account.setImageUri(Constants.DEFAULT_IMAGE_URI);
				Account savedAccount = accountDao.save(account);
				accountDao.savePermission(savedAccount.getId(), Constants.ACCOUNT_MAINTENANCE + savedAccount.getId());
			}
		}

		log.info("Accounts : " + accountDao.count());
	}




	private void generateMockFriendInvites(){
		try{

			List<Account> accounts = accountDao.findAll();

			int possibilities = accounts.size() * accounts.size();
			long currentDate = utilities.getCurrentDate();

			for (int n = 0; n < possibilities; n++) {
				for (Account account : accounts) {
					List<Account> possibleInvites = accountDao.findAll();
					Account invited = possibleInvites.get(n);
					friendDao.invite(account.getId(), invited.getId(), currentDate);
				}
			}

		}catch(Exception e){
			log.info("duplicate invite");
		}

		log.info("Friend Invites : " + friendDao.countInvites());
	}

	private void generateMockConnections() {
		try {
			long count = friendDao.count();

			if (count == 0) {
				List<Account> accounts = accountDao.findAll();

				int possibilities = accounts.size() * accounts.size();
				long currentDate = utilities.getCurrentDate();

				for (int n = 0; n < possibilities; n++) {

					for (Account account : accounts) {

						List<Account> possibleFriends = accountDao.findAll();
						Random r1 = new Random();

						if (n < possibleFriends.size()) {

							Random doOrDontRandom = new Random();
							int r2 = doOrDontRandom.nextInt(21);

							if (r2 % 13 == 0 && r2 != 0) {
								Account friend = possibleFriends.get(n);

								if (account.getId() != friend.getId()) {
									System.out.print(".");
									friendDao.saveConnection(account.getId(), friend.getId(), currentDate);
								}
							}
						}
					}
				}
			}
		}catch(Exception e){
			log.error("duplicate friend connection");
		}

		log.info("Connections : " + friendDao.count()/2);
	}

	
	private void generateMockPosts(){
		
		if(postDao.getCount() == 0) {
			List<Account> accounts = accountDao.findAll();
			
			for(Account account : accounts){
				Random r1 = new Random();
				int count = r1.nextInt(21);
				
				for(int m = 0; m < count; m++){

					Post post = new Post();
					post.setAccountId(account.getId());

					StringBuilder sb = new StringBuilder();
					for(int n = 0; n < 7; n++){
						sb.append("The Lazy Fox jumped over the yellow dog. ");
					}
					post.setContent(sb.toString());

					long datePosted = utilities.getCurrentDate();
					
					post.setDatePosted(datePosted);
					Post savedPost = postDao.save(post);
					accountDao.savePermission(account.getId(), Constants.POST_MAINTENANCE + savedPost.getId());

				}
			}
		}
		
		log.info("Posts : " + postDao.getCount());
	}


	private void generateMockMessages(){
		try{

			List<Account> accounts = accountDao.findAll();
			int possibilities = accounts.size() * accounts.size();

			for (int n = 0; n < possibilities; n++) {
				for (Account account : accounts) {
					List<Account> possibleRecipients = accountDao.findAll();
					Account recipient = possibleRecipients.get(n);
					if(recipient.getId() != account.getId()) {
						Message message = new Message();
						message.setSenderId(account.getId());
						message.setRecipientId(recipient.getId());
						message.setContent(utilities.generateRandomString(15));
						message.setDateSent(utilities.getCurrentDate());
						message.setViewed(false);
						messageDao.send(message);
					}
				}
			}

		}catch(Exception e){
			// log.error("message error");
		}

		log.info("Messages : " + messageDao.count());
	}

	private void generateMockViewData() {

		if(accountDao.getAllViewsAll() == 0) {

			List<Account> accounts = accountDao.findAll();
			for (Account account : accounts) {
				for (int n = 0; n < 61; n++) {

					long time = utilities.getPreviousDay(n);
					int views = utilities.generateRandomNumber(170);

					for (int m = 0; m < views; m++) {
						ProfileView view = new ProfileView.Builder()
								.profile(account.getId())
								.viewer(account.getId())
								.date(time)
								.build();
						accountDao.incrementViews(view);
					}
				}
			}
		}

		log.info("Profile Views : " + accountDao.getAllViewsAll());
	}

	private void generateAds(){
		long count = flyerDao.getCount();
		if (count == 0) {
			Account account = accountDao.findByUsername(Constants.ADMIN_USERNAME);
			Flyer flyer = new Flyer();
			flyer.setImageUri(Constants.DEFAULT_FLYER_IMAGE_URI);
			flyer.setActive(true);
			flyer.setPageUri("www.microsoft.org");
			flyer.setStartDate(utilities.getCurrentDate());
			flyer.setAccountId(account.getId());
			Flyer savedFlyer = flyerDao.save(flyer);
			log.info("saved flyer " + savedFlyer.getId());
			accountDao.savePermission(account.getId(), Constants.FLYER_MAINTENANCE  + savedFlyer.getId());
		}
		log.info("Ads : " + flyerDao.getCount());
	}

	private void connectEm(){
		Account admin = accountDao.findByUsername(Constants.ADMIN_USERNAME);
		Account guest = accountDao.findByUsername(Constants.GUEST_USERNAME);
		friendDao.saveConnection(admin.getId(), guest.getId(), utilities.getCurrentDate());
	}
}