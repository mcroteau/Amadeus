package social.amadeus;

import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import xyz.strongperched.resources.filters.ParakeetFilter;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class TestUtils {

    public static boolean mockRequestCycle(){
        try {
            ParakeetFilter filter = new ParakeetFilter();
            HttpServletRequest req = new MockHttpServletRequest();
            HttpServletResponse resp = new MockHttpServletResponse();

            FilterChain filterChain = Mockito.mock(FilterChain.class);
            FilterConfig config = Mockito.mock(FilterConfig.class);

            filter.init(config);
            filter.doFilter(req, resp, filterChain);
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return true;
    }


}
