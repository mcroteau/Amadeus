package social.amadeus;

import org.checkerframework.checker.units.qual.A;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;
import social.amadeus.common.Constants;
import social.amadeus.mocks.MockSheet;
import social.amadeus.model.Sheet;
import social.amadeus.repository.SheetRepo;
import social.amadeus.service.AuthService;
import social.amadeus.service.SheetService;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:o-combined-test.xml")
public class SheetActionTest {

    @Autowired
    SheetRepo sheetRepo;

    @Autowired
    AuthService authService;

    @Autowired
    SheetService sheetService;

    @Test
    public void testBasicSave(){
        TestUtils.mockRequestCycle();
        authService.signin(Constants.GUEST_USERNAME, Constants.GUEST_PASSWORD);
        Sheet sheet = new MockSheet(authService.getAccount());
        sheetService.save(sheet, null, new RedirectAttributesModelMap());
        assertEquals(1, sheetRepo.getCount());
    }

    @Test
    public void testDelete(){
        TestUtils.mockRequestCycle();
        authService.signin(Constants.GUEST_USERNAME, Constants.GUEST_PASSWORD);
        sheetService.delete(sheetRepo.getLast().getId(), new RedirectAttributesModelMap());
        assertEquals(0, sheetRepo.getCount());
    }

}
