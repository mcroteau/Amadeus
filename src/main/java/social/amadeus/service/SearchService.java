package social.amadeus.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import social.amadeus.common.Constants;
import social.amadeus.model.*;
import social.amadeus.model.SearchOutput;
import social.amadeus.repository.AccountRepo;
import social.amadeus.repository.FriendRepo;
import social.amadeus.repository.MusicRepo;
import social.amadeus.repository.SheetRepo;

import java.util.List;

@Service
public class SearchService {

    @Autowired
    MusicRepo musicRepo;

    @Autowired
    AccountRepo accountRepo;

    @Autowired
    FriendRepo friendRepo;

    @Autowired
    SheetRepo sheetRepo;

    @Autowired
    private AuthService authService;


    public SearchOutput queryBasic(String q) {

        SearchOutput searchOutput = new SearchOutput();

        if(!authService.isAuthenticated()){
            searchOutput.setStatus(Constants.AUTHENTICATION_REQUIRED);
            return searchOutput;
        }

        Account account = authService.getAccount();

        if(q != null){

            List<Account> accounts = accountRepo.search(q, 0);

            for(Account a : accounts){
                a.setIsFriend(friendRepo.isFriend(account.getId(), a.getId()));
                a.setInvited(friendRepo.isInvited(account.getId(), a.getId()));

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

            List<Sheet> sheets = sheetRepo.query(q);

            searchOutput.setAccounts(accounts);
            searchOutput.setSheets(sheets);
            searchOutput.setStatus(Constants.SUCCESS);

        }

        return searchOutput;
    }
}
