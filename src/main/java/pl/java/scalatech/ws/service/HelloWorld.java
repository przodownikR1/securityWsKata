package pl.java.scalatech.ws.service;

import javax.jws.WebService;

@WebService
public interface HelloWorld extends WS {

    String sayHi(String text);

}