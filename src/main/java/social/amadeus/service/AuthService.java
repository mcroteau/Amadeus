package social.amadeus.service;

import io.github.mcroteau.Parakeet;
import org.springframework.beans.factory.annotation.Autowired;
import social.amadeus.common.Constants;
import social.amadeus.dao.AccountDao;
import social.amadeus.model.Account;

public class AuthService {

    @Autowired
    private Parakeet parakeet;

    @Autowired
    private AccountDao accountDao;

    public boolean isAuthenticated(){
        return parakeet.isAuthenticated();
    }

    public boolean isAdministrator(){
        return parakeet.hasRole(Constants.ROLE_ADMIN);
    }

    public boolean hasPermission(String permission){
        return parakeet.hasPermission(permission);
    }

    public boolean hasRole(String role){
        return parakeet.hasRole(role);
    }

    public Account getAccount(){
        String username = parakeet.getUser();
        Account account = accountDao.findByUsername(username);
        return account;
    }

}
