package social.amadeus;

import org.apache.log4j.Logger;

import java.util.List;
import java.util.Random;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.core.env.Environment;
import social.amadeus.repository.*;
import social.amadeus.jobs.AdJob;
import social.amadeus.jobs.PublishJob;
import social.amadeus.model.*;
import social.amadeus.common.Constants;
import social.amadeus.common.Utils;

import javax.annotation.PostConstruct;

public class AppStartup {

	private static final Logger log = Logger.getLogger(AppStartup.class);

	@Autowired
	public RoleRepo roleRepo;

	@Autowired
	public AccountRepo accountRepo;

	@Autowired
	public PostRepo postRepo;
	
	@Autowired
	public FriendRepo friendRepo;

	@Autowired
	public MessageRepo messageRepo;

	@Autowired
	public FlyerRepo flyerRepo;

	@Autowired
	public Utils utils;

	@Autowired
	private Environment env;


	@PostConstruct
	public void init() {
		createApplicationRoles();
		createApplicationAdministrator();
		createApplicationGuest();
		connectEm();

		generateAppData();

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
		Role adminRole = roleRepo.find(Constants.ROLE_ADMIN);
		Role accountRole = roleRepo.find(Constants.ROLE_ACCOUNT);

		if(adminRole == null){
			adminRole = new Role();
			adminRole.setName(Constants.ROLE_ADMIN);
			roleRepo.save(adminRole);
		}

		if(accountRole == null){
			accountRole = new Role();
			accountRole.setName(Constants.ROLE_ACCOUNT);
			roleRepo.save(accountRole);
		}

		log.info("Roles : " + roleRepo.count());
	}

	
	private void createApplicationAdministrator(){
		
		try{
			Account existing = accountRepo.findByUsername(Constants.ADMIN_USERNAME);
			String password = io.github.mcroteau.resources.Constants.hash(Constants.PASSWORD);

			if(existing == null){
				Account admin = new Account();
				admin.setName("Sebastien");
				admin.setUsername(Constants.ADMIN_USERNAME);
				admin.setPassword(password);
				admin.setImageUri(Constants.FRESCO);
				accountRepo.saveAdministrator(admin);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		log.info("Accounts : " + accountRepo.count());
	}



	private void createApplicationGuest(){
		Account existing = accountRepo.findByUsername(Constants.GUEST_USERNAME);
		String password = utils.hash(Constants.GUEST_PASSWORD);

		if(existing == null){
			Account account = new Account();
			account.setName("Kelly");
			account.setUsername(Constants.GUEST_USERNAME);
			account.setPassword(password);
			account.setImageUri(Constants.DEFAULT_IMAGE_URI);
			accountRepo.save(account);
		}
		log.info("Accounts : " + accountRepo.count());
	}


	private void startupBackgroundJobs() {
		try {
			JobDetail publishJob = JobBuilder.newJob(PublishJob.class)
				.withIdentity(Constants.PUBLISHING_JOB, Constants.AMADEUS_GROUP).build();

			publishJob.getJobDataMap().put(Constants.POSTS_DAO_KEY, postRepo);

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

			adJob.getJobDataMap().put(Constants.FLYER_DAO_KEY, flyerRepo);

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
		long count = accountRepo.count();
		if(count == 2){
			for(int m = 0; m < Constants.MOCK_ACCOUNTS; m++){
				Account account = new Account();
				String name = "Prometheus " + utils.generateRandomString(9);
				account.setName(name);
				account.setUsername("croteau.mike+"+ m + "@gmail.com");
				account.setLocation(utils.generateRandomString(7));
				account.setAge("33");
				String password = utils.hash(Constants.PASSWORD);
				account.setPassword(password);
				account.setImageUri(Constants.DEFAULT_IMAGE_URI);
				Account savedAccount = accountRepo.save(account);
				accountRepo.savePermission(savedAccount.getId(), Constants.ACCOUNT_MAINTENANCE + savedAccount.getId());
			}
		}

		log.info("Accounts : " + accountRepo.count());
	}




	private void generateMockFriendInvites(){
		try{

			List<Account> accounts = accountRepo.findAll();

			int possibilities = accounts.size() * accounts.size();
			long currentDate = utils.getCurrentDate();

			for (int n = 0; n < possibilities; n++) {
				for (Account account : accounts) {
					List<Account> possibleInvites = accountRepo.findAll();
					Account invited = possibleInvites.get(n);
					friendRepo.invite(account.getId(), invited.getId(), currentDate);
				}
			}

		}catch(Exception e){
			log.info("duplicate invite");
		}

		log.info("Friend Invites : " + friendRepo.countInvites());
	}

	private void generateMockConnections() {
		try {
			long count = friendRepo.count();

			if (count == 0) {
				List<Account> accounts = accountRepo.findAll();

				int possibilities = accounts.size() * accounts.size();
				long currentDate = utils.getCurrentDate();

				for (int n = 0; n < possibilities; n++) {

					for (Account account : accounts) {

						List<Account> possibleFriends = accountRepo.findAll();
						Random r1 = new Random();

						if (n < possibleFriends.size()) {

							Random doOrDontRandom = new Random();
							int r2 = doOrDontRandom.nextInt(21);

							if (r2 % 13 == 0 && r2 != 0) {
								Account friend = possibleFriends.get(n);

								if (account.getId() != friend.getId()) {
									System.out.print(".");
									friendRepo.saveConnection(account.getId(), friend.getId(), currentDate);
								}
							}
						}
					}
				}
			}
		}catch(Exception e){
			log.error("duplicate friend connection");
		}

		log.info("Connections : " + friendRepo.count()/2);
	}

	
	private void generateMockPosts(){
		
		if(postRepo.getCount() == 0) {
			List<Account> accounts = accountRepo.findAll();
			
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

					long datePosted = utils.getCurrentDate();
					
					post.setDatePosted(datePosted);
					Post savedPost = postRepo.save(post);
					accountRepo.savePermission(account.getId(), Constants.POST_MAINTENANCE + savedPost.getId());

				}
			}
		}
		
		log.info("Posts : " + postRepo.getCount());
	}


	private void generateMockMessages(){
		try{
			List<Account> accounts = accountRepo.findAll();
			int possibilities = accounts.size() * accounts.size();

			for (int n = 0; n < possibilities; n++) {
				for (Account account : accounts) {
					List<Account> possibleRecipients = accountRepo.findAll();
					Account recipient = possibleRecipients.get(n);
					if(recipient.getId() != account.getId()) {
						Message message = new Message();
						message.setSenderId(account.getId());
						message.setRecipientId(recipient.getId());
						message.setContent(utils.generateRandomString(15));
						message.setDateSent(utils.getCurrentDate());
						message.setViewed(false);
						messageRepo.send(message);
					}
				}
			}
		}catch(Exception e){
			// log.error("message error");
		}

		log.info("Messages : " + messageRepo.count());
	}

	private void generateMockViewData() {

		if(accountRepo.getAllViewsAll() == 0) {
			List<Account> accounts = accountRepo.findAll();
			for (Account account : accounts) {
				for (int n = 0; n < 61; n++) {

					long time = utils.getPreviousDay(n);
					int views = utils.generateRandomNumber(170);

					for (int m = 0; m < views; m++) {
						ProfileView view = new ProfileView.Builder()
								.profile(account.getId())
								.viewer(account.getId())
								.date(time)
								.build();
						accountRepo.incrementViews(view);
					}
				}
			}
		}

		log.info("Profile Views : " + accountRepo.getAllViewsAll());
	}

	private void generateAds(){
		long count = flyerRepo.getCount();
		if (count == 0) {
			Account account = accountRepo.findByUsername(Constants.ADMIN_USERNAME);
			Flyer flyer = new Flyer();
			flyer.setImageUri(Constants.DEFAULT_FLYER_IMAGE_URI);
			flyer.setActive(true);
			flyer.setPageUri("www.microsoft.org");
			flyer.setStartDate(utils.getCurrentDate());
			flyer.setAccountId(account.getId());
			Flyer savedFlyer = flyerRepo.save(flyer);
			log.info("saved flyer " + savedFlyer.getId());
			accountRepo.savePermission(account.getId(), Constants.FLYER_MAINTENANCE  + savedFlyer.getId());
		}
		log.info("Ads : " + flyerRepo.getCount());
	}

	private void connectEm(){
		Account admin = accountRepo.findByUsername(Constants.ADMIN_USERNAME);
		Account guest = accountRepo.findByUsername(Constants.GUEST_USERNAME);
		if(!friendRepo.isFriend(admin.getId(), guest.getId())) {
			friendRepo.saveConnection(admin.getId(), guest.getId(), utils.getCurrentDate());
		}
	}
}