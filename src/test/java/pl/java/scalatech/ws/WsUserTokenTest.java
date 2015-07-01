package pl.java.scalatech.ws;

import java.net.SocketTimeoutException;
import java.util.Map;

import javax.xml.ws.WebServiceException;

import lombok.extern.slf4j.Slf4j;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.endpoint.Endpoint;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.jaxws.JaxWsServerFactoryBean;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;
import org.apache.cxf.ws.security.wss4j.WSS4JInInterceptor;
import org.apache.cxf.ws.security.wss4j.WSS4JOutInterceptor;
import org.apache.wss4j.dom.WSConstants;
import org.apache.wss4j.dom.handler.WSHandlerConstants;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import pl.java.scalatech.ws.security.ClientPasswordCallback;
import pl.java.scalatech.ws.security.ServerPasswordCallback;
import pl.java.scalatech.ws.service.HelloWorld;
import pl.java.scalatech.ws.service.HelloWorldImpl;

import com.google.common.collect.Maps;

@Slf4j
public class WsUserTokenTest {
    private static final String ADDRESS = "http://localhost:8888/services/helloworld";

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        JaxWsServerFactoryBean factoryBean = new JaxWsServerFactoryBean();
        Map<String, Object> props = Maps.newHashMap();
        props.put(WSHandlerConstants.ACTION, WSHandlerConstants.USERNAME_TOKEN);
        props.put(WSHandlerConstants.PASSWORD_TYPE, WSConstants.PW_TEXT);
        props.put(WSHandlerConstants.PW_CALLBACK_CLASS, ServerPasswordCallback.class.getName());
        WSS4JInInterceptor wss4JInInterceptor = new WSS4JInInterceptor(props);
        factoryBean.getInInterceptors().add(wss4JInInterceptor);
        LoggingInInterceptor inLog = new LoggingInInterceptor();
        inLog.setPrettyLogging(true);
        factoryBean.getInInterceptors().add(inLog);
        factoryBean.setServiceClass(HelloWorldImpl.class);
        factoryBean.setAddress(ADDRESS);
        factoryBean.create();
    }

    @Test
    public void testList() {
        JaxWsProxyFactoryBean factoryBean = new JaxWsProxyFactoryBean();
        factoryBean.setAddress(ADDRESS);
        factoryBean.setServiceClass(HelloWorld.class);
        Object obj = factoryBean.create();
        Client client = ClientProxy.getClient(obj);
        Endpoint endpoint = client.getEndpoint();
        Map<String, Object> props = Maps.newHashMap();
        props.put(WSHandlerConstants.ACTION, WSHandlerConstants.USERNAME_TOKEN);
        props.put(WSHandlerConstants.PASSWORD_TYPE, WSConstants.PW_TEXT);
        props.put(WSHandlerConstants.PW_CALLBACK_CLASS, ClientPasswordCallback.class.getName());
        props.put(WSHandlerConstants.USER, "przodownik");
        WSS4JOutInterceptor wss4JOutInterceptor = new WSS4JOutInterceptor(props);
        endpoint.getOutInterceptors().add(wss4JOutInterceptor);
        LoggingOutInterceptor outLog = new LoggingOutInterceptor();
        outLog.setPrettyLogging(true);
        endpoint.getOutInterceptors().add(outLog);

        HTTPConduit conduit = (HTTPConduit) client.getConduit();
        HTTPClientPolicy policy = new HTTPClientPolicy();
        policy.setConnectionTimeout(5 * 1000);
        policy.setReceiveTimeout(5 * 1000);
        conduit.setClient(policy);

        HelloWorld service = (HelloWorld) obj;
        try {
            log.info("+++ {}", service.sayHi("slawek"));
        } catch (Exception e) {
            if (e instanceof WebServiceException && e.getCause() instanceof SocketTimeoutException) {
                log.error("This is timeout exception.", e);
            } else {
                log.error("{}", e);

            }
            Assert.fail();
        }
    }

}
