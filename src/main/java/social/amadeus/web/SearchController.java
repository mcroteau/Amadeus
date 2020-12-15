package social.amadeus.web;

import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import social.amadeus.model.Account;
import social.amadeus.model.AccountBlock;
import social.amadeus.model.MusicFile;
import social.amadeus.repository.AccountRepo;
import social.amadeus.repository.FriendRepo;
import social.amadeus.repository.MusicRepo;
import social.amadeus.service.AuthService;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class SearchController {

    @Autowired
    private MusicRepo musicRepo;

    @Autowired
    private AccountRepo accountRepo;

    @Autowired
    private FriendRepo friendRepo;

    @Autowired
    private AuthService authService;

    @RequestMapping(value="/search", method= RequestMethod.GET, produces="application/json")
    public @ResponseBody
    String search(ModelMap model,
                  HttpServletRequest request,
                  final RedirectAttributes redirect,
                  @RequestParam(value="q", required = false ) String q){

        Map<String, Object> data = new HashMap<String, Object>();
        Gson gson = new Gson();

        if(!authService.isAuthenticated()){
            data.put("error", "Authentication required");
            return gson.toJson(data);
        }

        Account account = authService.getAccount();

        if(q != null){

            List<Account> accounts = accountRepo.search(q, 0);

            for(Account a : accounts){
                a.setIsFriend(friendRepo.isFriend(account.getId(), a.getId()));
                a.setInvited(friendRepo.invited(account.getId(), a.getId()));

                AccountBlock accountBlock = new AccountBlock();
                accountBlock.setPersonId(account.getId());
                accountBlock.setBlockerId(a.getId());

                boolean blocked = accountRepo.blocked(accountBlock);
                a.setBlocked(blocked);
            }
            Map<String, Object> d = new HashMap<String, Object>();

            for(Account a : accounts){
                if(a.getId() == account.getId()){
                    a.setOwnersAccount(true);
                }
            }

            List<MusicFile> music = musicRepo.search(q);
            for(MusicFile musicFile : music){
                if(musicFile.getAccountId() == account.getId()){
                    musicFile.setEditable(true);
                }
            }

            d.put("accounts", accounts);
            d.put("music", music);

            return gson.toJson(d);

        } else {
            return gson.toJson(data);
        }
    }

}
