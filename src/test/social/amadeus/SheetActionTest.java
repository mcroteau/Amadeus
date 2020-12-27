package social.amadeus;

import org.checkerframework.checker.units.qual.A;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
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
        authService.signin(Constants.ADMIN_USERNAME, Constants.PASSWORD);
        Sheet sheet = new MockSheet(authService.getAccount());
        sheetService.save(sheet);
        assertEquals(1, sheetRepo.getCount());
    }

    @Test
    public void testEdit(){
        TestUtils.mockRequestCycle();
        authService.signin(Constants.ADMIN_USERNAME, Constants.PASSWORD);
        Sheet sheet = sheetService.edit(sheetRepo.getLast().getId());
        sheetRepo.delete(sheetRepo.getLast().getId());
    }

}
