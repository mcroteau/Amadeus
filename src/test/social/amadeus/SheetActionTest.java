package social.amadeus;

import org.apache.log4j.Logger;
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

    private static final Logger log = Logger.getLogger(SheetActionTest.class);

    @Autowired
    SheetRepo sheetRepo;

    @Autowired
    AuthService authService;

    @Autowired
    SheetService sheetService;

    @Test
    public void testBasicSave(){
        log.info("1");
        TestUtils.mockRequestCycle();
        authService.signin(Constants.MARISA_USERNAME, Constants.MARISA_PASSWORD);
        String result = sheetService.save(new MockSheet(), null, new RedirectAttributesModelMap());
        log.info("result: " + result);
        assertEquals(1, sheetRepo.getCount());
    }

    @Test
    public void testDelete(){
        log.info("2");
        TestUtils.mockRequestCycle();
        authService.signin(Constants.MARISA_USERNAME, Constants.MARISA_PASSWORD);
        log.info(sheetRepo.getCount());
        sheetService.delete(sheetRepo.getLast().getId(), new RedirectAttributesModelMap());
        assertEquals(0, sheetRepo.getCount());
    }

    @Test
    public void testUpdate(){
        log.info("3");
        TestUtils.mockRequestCycle();
        authService.signin(Constants.MARISA_USERNAME, Constants.MARISA_PASSWORD);
        sheetService.save(new MockSheet(), null, new RedirectAttributesModelMap());
        Sheet sheet = sheetRepo.getLast();
        sheet.setDescription("How now brown cow.");
        sheetService.update(sheet, null, new RedirectAttributesModelMap());
        Sheet updatedSheet = sheetRepo.getLast();
        assertEquals("How now brown cow.", updatedSheet.getDescription());
    }

    @Test
    public void testGetList(){
        TestUtils.mockRequestCycle();
        authService.signin(Constants.ADMIN_USERNAME, Constants.PASSWORD);
        assertEquals(1, sheetRepo.getSheets().size());
    }

    @Test
    public void testGetUserList(){
        TestUtils.mockRequestCycle();
        authService.signin(Constants.MARISA_USERNAME, Constants.MARISA_PASSWORD);
        assertEquals(1, sheetRepo.getSheets(authService.getAccount().getId()).size());
    }

}
