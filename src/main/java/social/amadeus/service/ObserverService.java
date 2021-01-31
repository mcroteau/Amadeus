package social.amadeus.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import social.amadeus.common.Constants;
import social.amadeus.common.Utils;
import social.amadeus.model.Account;
import social.amadeus.model.Observed;
import social.amadeus.repository.ObserverRepo;

import java.util.ArrayList;
import java.util.List;

@Service
public class ObserverService {

    @Autowired
    ObserverRepo observerRepo;

    @Autowired
    AuthService authService;


    public String observe(Long id) {
        if(!authService.isAuthenticated()){
            return Constants.AUTHENTICATION_REQUIRED;
        }

        Account authdAccount = authService.getAccount();

        Observed observed = new Observed();
        observed.setObservedId(id);
        observed.setObserverId(authdAccount.getId());
        observed.setDateCreated(Utils.getDate());

        if(!observerRepo.observe(observed)){
            return "fix";
        }
        return "success";
    }

    public String unobserve(Long id) {
        if(!authService.isAuthenticated()){
            return Constants.AUTHENTICATION_REQUIRED;
        }

        Account authdAccount = authService.getAccount();
        Observed observed = new Observed();
        observed.setObservedId(id);
        observed.setObserverId(authdAccount.getId());

        if(!observerRepo.unobserve(observed)){
            return "fix";
        }
        return "success";
    }

    public List<Observed> getObservers(Long id) {
        if(!authService.isAuthenticated()){
            return new ArrayList<>();
        }
        List<Observed> observed = observerRepo.getObserving(id);
        return observed;
    }
}
