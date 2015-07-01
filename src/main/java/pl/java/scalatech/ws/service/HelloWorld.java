package pl.java.scalatech.ws.service;

import javax.jws.WebService;

@WebService
public interface HelloWorld {

	String sayHi(String text);

}