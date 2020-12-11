package social.amadeus.web;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Autowired;

import io.github.mcroteau.Parakeet;
import social.amadeus.common.Constants;
import social.amadeus.model.Account;
import social.amadeus.dao.AccountDao;


@Controller
public class BaseController {

	private static final Logger log = Logger.getLogger(BaseController.class);

	@Autowired
	private Parakeet parakeet;

	@Autowired
	public AccountDao accountDao;


	public boolean administratord(){
		return parakeet.hasRole(Constants.ROLE_ADMIN);
	}	

	public boolean authenticatedd(){
		return parakeet.isAuthenticated();
	}

	public boolean hasPermissiond(String str){
		return parakeet.hasPermission(str);
	}

	public Account getAuthenticatedAccountd(){
		String user = parakeet.getUser();
		Account account = accountDao.findByUsername(user);
		return account;
	}
	
}