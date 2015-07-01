package pl.java.scalatech.ws;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

import pl.java.scalatech.ws.config.CxfConfig;

@SpringBootApplication
@Import(CxfConfig.class)
public class SecurityWsApplication {

    public static void main(String[] args) {
        SpringApplication.run(SecurityWsApplication.class, args);
    }
}
