package social.amadeus;

import org.apache.log4j.Logger;
import org.apache.tools.ant.taskdefs.condition.Http;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;
import social.amadeus.common.Constants;
import social.amadeus.mocks.MockAccount;
import social.amadeus.model.Account;
import social.amadeus.repository.AccountRepo;
import social.amadeus.service.AccountService;
import social.amadeus.service.AuthService;
import social.amadeus.service.SearchService;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:o-combined-test.xml")
public class AccountActionTest {

    private static final Logger log = Logger.getLogger(AccountActionTest.class);

    @Autowired
    AccountRepo accountRepo;

    @Autowired
    AccountService accountService;

    @Autowired
    SearchService searchService;

    @Autowired
    AuthService authService;


    @Test
    public void testBasicQuery(){
        TestUtils.mockRequestCycle();
        authService.signin(Constants.ADMIN_USERNAME, Constants.PASSWORD);
        Map<String, Object> results = searchService.queryBasic("Sebastien");
        List<Account> accounts = (List) results.get("accounts");
        assertEquals(1, accounts.size());
    }

    @Test
    public void testBasicSignup(){
        TestUtils.mockRequestCycle();
        RedirectAttributes redirect = new RedirectAttributesModelMap();
        Account mockAccount = new MockAccount(1);
        accountService.register("", "", mockAccount, new MockHttpServletRequest(), redirect);
        assertEquals(3, accountRepo.getCount());
    }

    @Test
    public void testMarisaExotic(){
        TestUtils.mockRequestCycle();
        Account mockAccount = new MockAccount(2);
        accountService.register("", "", mockAccount, new MockHttpServletRequest(), new RedirectAttributesModelMap());
        TestUtils.mockRequestCycle();
        authService.signin(Constants.ADMIN_USERNAME, Constants.PASSWORD);
        Account account = accountRepo.getByUsername(mockAccount.getUsername());
        Map<String, Object> results = searchService.queryBasic("marisa");
        List<Account> accounts = (List) results.get("accounts");
        assertEquals(3, accounts.size());
    }
}
