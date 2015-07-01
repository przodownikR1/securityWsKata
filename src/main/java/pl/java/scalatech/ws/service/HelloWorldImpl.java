package pl.java.scalatech.ws.service;

import javax.jws.WebService;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

@WebService(endpointInterface = "pl.java.scalatech.ws.service.HelloWorld")
@Slf4j
@Service("helloWorld")
public class HelloWorldImpl implements HelloWorld {

    @Override
    public String sayHi(String text) {
        log.info("++++++++++++++++++++++++++++++++++  {}", text);
        return "Hello " + text;
    }

}