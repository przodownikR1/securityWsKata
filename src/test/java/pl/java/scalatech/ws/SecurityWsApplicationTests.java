package pl.java.scalatech.ws;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import pl.java.scalatech.ws.SecurityWsApplication;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = SecurityWsApplication.class)
@WebAppConfiguration
public class SecurityWsApplicationTests {

	@Test
	public void contextLoads() {
	}

}
