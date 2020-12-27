package social.amadeus;

import org.checkerframework.checker.units.qual.A;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import social.amadeus.common.Constants;
import social.amadeus.mocks.MockSheet;
import social.amadeus.model.Sheet;
import social.amadeus.repository.SheetRepo;
import social.amadeus.service.AuthService;
import social.amadeus.service.SheetService;

import static org.junit.Assert.assertEquals;

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
        Sheet sheet = new MockSheet();
        sheetService.save(sheet);
        assertEquals(1, sheetRepo.getCount());
    }
}
