package social.amadeus.service;

import com.google.gson.Gson;
import opennlp.tools.parser.Cons;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import social.amadeus.common.Constants;
import social.amadeus.model.Account;
import social.amadeus.model.AccountBlock;
import social.amadeus.model.MusicFile;
import social.amadeus.repository.AccountRepo;
import social.amadeus.repository.FriendRepo;
import social.amadeus.repository.MusicRepo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SearchService {

    @Autowired
    private MusicRepo musicRepo;

    @Autowired
    private AccountRepo accountRepo;

    @Autowired
    private FriendRepo friendRepo;

    @Autowired
    private AuthService authService;


    public Object queryBasic(String q) {

        Map<String, Object> respData = new HashMap<String, Object>();

        if(!authService.isAuthenticated()){
            respData.put("error", Constants.AUTHENTICATION_REQUIRED);
            return respData;
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

            respData.put("accounts", accounts);
            respData.put("music", music);

        }

        return respData;
    }
}
