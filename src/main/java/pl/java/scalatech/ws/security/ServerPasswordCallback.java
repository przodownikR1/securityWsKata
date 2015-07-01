package pl.java.scalatech.ws.security;

import java.io.IOException;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;

import lombok.extern.slf4j.Slf4j;

import org.apache.wss4j.common.ext.WSPasswordCallback;

@Slf4j
public class ServerPasswordCallback implements CallbackHandler {

    /*
     * @Override
     * public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
     * WSPasswordCallback pc = (WSPasswordCallback) callbacks[0];
     * log.info("+++  serverPasswordCallback");
     * if (pc.getIdentifier().equals("max")) {
     * // set the password on the callback. This will be compared to the
     * // password which was sent from the client.
     * pc.setPassword("maxPassword");
     * }
     * }
     */
    @Override
    public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
        log.info("+++  serverPasswordCallback");
        for (Callback callback : callbacks) {
            if (callback instanceof WSPasswordCallback)
                handle((WSPasswordCallback) callback);
        }

    }

    private void handle(WSPasswordCallback callback) {
        callback.setPassword("changeit");
    }

}