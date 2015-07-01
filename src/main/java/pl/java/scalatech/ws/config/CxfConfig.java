package pl.java.scalatech.ws.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import lombok.extern.slf4j.Slf4j;

import org.apache.cxf.binding.soap.saaj.SAAJInInterceptor;
import org.apache.cxf.bus.spring.SpringBus;
import org.apache.cxf.feature.Feature;
import org.apache.cxf.feature.LoggingFeature;
import org.apache.cxf.jaxws.EndpointImpl;
import org.apache.cxf.transport.servlet.CXFServlet;
import org.apache.cxf.ws.security.wss4j.WSS4JInInterceptor;
import org.apache.wss4j.dom.WSConstants;
import org.apache.wss4j.dom.handler.WSHandlerConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.embedded.ServletRegistrationBean;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ImportResource;

import pl.java.scalatech.ws.security.ClientPasswordCallback;
import pl.java.scalatech.ws.security.MyUsernameTokenValidator;
import pl.java.scalatech.ws.security.ServerPasswordCallback;
import pl.java.scalatech.ws.service.HelloWorld;

import com.google.common.collect.Maps;

@Configurable
@Slf4j
@ImportResource({ "classpath:wsClient.xml" })
@ComponentScan(basePackages = "pl.java.scalatech.ws.service")
public class CxfConfig extends SpringBootServletInitializer {
    @Autowired
    SpringBus cxf;

    @Autowired
    @Qualifier("helloWorld")
    private HelloWorld helloWorld;

    @Autowired
    @Qualifier("helloClient")
    private HelloWorld helloClient;

    @PostConstruct
    public void init() {
        log.info("+++ init cxf ....");
    }

    @Bean
    public ServletRegistrationBean soapServletRegistrationBean() {
        log.info("+++  soapServletRegistration");
        ServletRegistrationBean registration = new ServletRegistrationBean(new CXFServlet(), "/services/*");
        registration.setLoadOnStartup(1);
        registration.setName("CXFServlet");
        return registration;
    }

    @Bean(name = "loggingFeature")
    LoggingFeature loggingFeature() {
        LoggingFeature loggingFeature = new LoggingFeature();
        loggingFeature.setPrettyLogging(true);
        return loggingFeature;
    }

    @Bean(name = "cxf", destroyMethod = "shutdown")
    public SpringBus configureCxfBus() {
        final SpringBus bus = new SpringBus();
        List<Feature> features = new ArrayList<>();
        features.add(loggingFeature());
        // bus.setProperty("ws-security.ut.validator", MyUsernameTokenValidator.class.getName());
        bus.setFeatures(features);
        bus.setId("cxf");
        return bus;
    }

    @Bean(name = "helloWorldProviderBean")
    public EndpointImpl helloWorldEndpoint() {
        EndpointImpl endpoint = new EndpointImpl(cxf, helloWorld);
        endpoint.setAddress("/helloworld");
        endpoint.setBus(cxf);
        // endpoint.setInInterceptors(Lists.newArrayList(wSS4JInInterceptor()));
        endpoint.setProperties(jaxwsProperties());

        Map<String, Object> inProps = Maps.newHashMap();

        inProps.put(WSHandlerConstants.ACTION, WSHandlerConstants.USERNAME_TOKEN);
        inProps.put(WSHandlerConstants.USER, "max");
        inProps.put(WSHandlerConstants.PASSWORD_TYPE, WSConstants.PW_TEXT);
        inProps.put(WSHandlerConstants.PW_CALLBACK_CLASS, ClientPasswordCallback.class.getName());
        inProps.put("ws-security.callback-handler", ServerPasswordCallback.class.getName());
        WSS4JInInterceptor wssIn = new WSS4JInInterceptor(inProps);
        endpoint.getInInterceptors().add(wssIn);
        endpoint.getInInterceptors().add(new SAAJInInterceptor());

        endpoint.publish();
        log.info("++++  features  : {}", endpoint.getFeatures());
        endpoint.getInInterceptors().stream().forEach(i -> log.info("in : {}", i));
        endpoint.getOutInterceptors().stream().forEach(i -> log.info("out : {}", i));
        return endpoint;
    }

    private Map<String, Object> jaxwsProperties() {
        Map<String, Object> properties = new HashMap<>();
        properties.put("ws-security.ut.validator", MyUsernameTokenValidator.class.getName());
        properties.put("security.callback-handler", ServerPasswordCallback.class.getName());
        return properties;
    }

}