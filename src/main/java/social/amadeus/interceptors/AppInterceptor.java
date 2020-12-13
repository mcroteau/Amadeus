package social.amadeus.interceptors;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import social.amadeus.common.SessionManager;
import social.amadeus.repository.AccountRepo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

import io.github.mcroteau.Parakeet;
import social.amadeus.model.Account;


public class AppInterceptor implements HandlerInterceptor {

    private static final Logger log = Logger.getLogger(AppInterceptor.class);

    @Autowired
    private AccountRepo accountRepo;

    @Autowired
    private SessionManager sessionManager;

    @Autowired
    private Parakeet parakeet;


    @Override
    public boolean preHandle(HttpServletRequest req,
                             HttpServletResponse resp, Object handler) throws Exception {
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest req,
                           HttpServletResponse resp, Object handler,
                           ModelAndView modelAndView) throws Exception {

        if(parakeet.isAuthenticated()){
            String username = parakeet.getUser();
            sessionManager.sessions.put(username, System.currentTimeMillis());
        }

        for (Map.Entry<String, Long> entry : sessionManager.sessions.entrySet()) {
            long currentTime = System.currentTimeMillis();
            long sessionTime = entry.getValue();
            long diff = currentTime - sessionTime;
            if ( diff >= 24000) {
                sessionManager.sessions.remove(entry.getKey());
            }
        }
    }


    @Override
    public void afterCompletion(HttpServletRequest req,
                                HttpServletResponse resp, Object handler, Exception ex)
            throws Exception {

        if(parakeet.isAuthenticated()){
            String user = parakeet.getUser();
            Account sessionAaccount = accountRepo.findByUsername(user);
            req.getSession().setAttribute("account", sessionAaccount);
            req.getSession().setAttribute("imageUri", sessionAaccount.getImageUri());
        }
    }

}
