package pl.java.scalatech.ws.util;

import java.io.IOException;
import java.util.Map;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;
import org.apache.cxf.ws.security.wss4j.WSS4JOutInterceptor;
import org.apache.wss4j.common.ext.WSPasswordCallback;
import org.apache.wss4j.dom.WSConstants;
import org.apache.wss4j.dom.handler.WSHandlerConstants;

import pl.java.scalatech.ws.service.HelloWorld;

import com.google.common.collect.Maps;

public final class UsernameTokenUtil<T> {

    public T getSecuredProxy(String address, String login, final String password) {
        JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
        factory.setServiceClass(HelloWorld.class);
        factory.setAddress(address);
        createLoggerWS(factory);
        final Map<String, Object> authConfig = createAuthConfigProperty(login, password);
        factory.getOutInterceptors().add(new WSS4JOutInterceptor(authConfig));
        Object obj = factory.create();
        createConduit(obj);
        return (T) obj;

    }

    private void createLoggerWS(JaxWsProxyFactoryBean factory) {
        LoggingInInterceptor inLog = new LoggingInInterceptor();
        inLog.setPrettyLogging(true);
        LoggingOutInterceptor outLog = new LoggingOutInterceptor();
        outLog.setPrettyLogging(true);
        factory.getInInterceptors().add(inLog);
        factory.getOutInterceptors().add(outLog);
    }

    private void createConduit(Object obj) {
        Client client = ClientProxy.getClient(obj);
        HTTPConduit conduit = (HTTPConduit) client.getConduit();
        HTTPClientPolicy policy = new HTTPClientPolicy();
        policy.setConnectionTimeout(5 * 1000);
        policy.setReceiveTimeout(5 * 1000);
        conduit.setClient(policy);
    }

    private Map<String, Object> createAuthConfigProperty(String login, final String password) {
        final Map<String, Object> authConfig = Maps.newHashMap();
        authConfig.put(WSHandlerConstants.ACTION, WSHandlerConstants.USERNAME_TOKEN);
        authConfig.put(WSHandlerConstants.USER, login);
        authConfig.put(WSHandlerConstants.PASSWORD_TYPE, WSConstants.PW_TEXT);
        authConfig.put(WSHandlerConstants.PW_CALLBACK_REF, new CallbackHandler() {
            @Override
            public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
                WSPasswordCallback pc = (WSPasswordCallback) callbacks[0];
                pc.setIdentifier((String) authConfig.get(WSHandlerConstants.USER));
                pc.setPassword(password);
            }
        });
        return authConfig;
    }

}
