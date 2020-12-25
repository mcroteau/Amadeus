package social.amadeus.accessor;

import io.github.mcroteau.resources.access.Accessor;
import org.springframework.beans.factory.annotation.Autowired;
import social.amadeus.repository.AccountRepo;
import social.amadeus.model.Account;

import java.util.Set;

public class AmadeusAccessor implements Accessor {

    @Autowired
    private AccountRepo accountRepo;

    public String getPassword(String user){
        String password = accountRepo.getAccountPassword(user);
        return password;
    }

    public Set<String> getRoles(String user){
        Account account = accountRepo.getByUsername(user);
        Set<String> roles = accountRepo.getAccountRoles(account.getId());
        return roles;
    }

    public Set<String> getPermissions(String user){
        Account account = accountRepo.getByUsername(user);
        Set<String> permissions = accountRepo.getAccountPermissions(account.getId());
        return permissions;
    }

}
