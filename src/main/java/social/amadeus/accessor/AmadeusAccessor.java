package social.amadeus.accessor;

import io.github.mcroteau.resources.access.Accessor;
import org.springframework.beans.factory.annotation.Autowired;
import social.amadeus.dao.AccountDao;
import social.amadeus.model.Account;

import java.util.Set;

public class AmadeusAccessor implements Accessor {

    @Autowired
    private AccountDao accountDao;

    public String getPassword(String user){
        String password = accountDao.getAccountPassword(user);
        return password;
    }

    public Set<String> getRoles(String user){
        Account account = accountDao.findByUsername(user);
        Set<String> roles = accountDao.getAccountRoles(account.getId());
        return roles;
    }

    public Set<String> getPermissions(String user){
        Account account = accountDao.findByUsername(user);
        Set<String> permissions = accountDao.getAccountPermissions(account.getId());
        return permissions;
    }

}
