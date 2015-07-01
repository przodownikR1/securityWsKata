package pl.java.scalatech.ws;

import lombok.extern.slf4j.Slf4j;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.endpoint.Endpoint;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import pl.java.scalatech.ws.service.HelloWorld;
import pl.java.scalatech.ws.service.HelloWorldImpl;

/*
 * @RunWith(SpringJUnit4ClassRunner.class)
 * @ContextConfiguration(classes = CxfConfig.class)
 */
@Slf4j
public class WsTest {
    private HelloWorld clientWs;
    private static final String ADDRESS = "http://localhost:8883/services/helloworld";

    @BeforeClass
    public static void startService() {
        HelloWorldImpl implementor = new HelloWorldImpl();
        javax.xml.ws.Endpoint.publish(ADDRESS, implementor);

    }

    @Before
    public void init() {
        log.info("+++                                  init ....");
        JaxWsProxyFactoryBean factoryBean = new JaxWsProxyFactoryBean();
        factoryBean.setAddress(ADDRESS);
        factoryBean.setServiceClass(HelloWorld.class);
        Object obj = factoryBean.create();
        Client client = ClientProxy.getClient(obj);
        Endpoint endpoint = client.getEndpoint();
        LoggingOutInterceptor outLog = new LoggingOutInterceptor();
        outLog.setPrettyLogging(true);
        LoggingInInterceptor inLog = new LoggingInInterceptor();
        inLog.setPrettyLogging(true);
        endpoint.getOutInterceptors().add(outLog);
        endpoint.getInInterceptors().add(inLog);
        clientWs = (HelloWorld) obj;
    }

    @Test
    public void shouldWsClientWork() {
        Assert.assertEquals(clientWs.sayHi("przodownik"), "Hello przodownik");
    }
}