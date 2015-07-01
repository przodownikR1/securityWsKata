package pl.java.scalatech.ws.security;

import java.io.IOException;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;

import lombok.extern.slf4j.Slf4j;

import org.apache.wss4j.common.ext.WSPasswordCallback;

@Slf4j
public class ClientPasswordCallback implements CallbackHandler {

    /*
     * @Override
     * public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
     * WSPasswordCallback pc = (WSPasswordCallback) callbacks[0];
     * if ("max".equals(pc.getIdentifier())) {
     * pc.setPassword("maxPassword");
     * } // else {...} - can add more users, access DB, etc.
     * }
     */

    @Override
    public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
        log.info("+++  clientPasswordCallback");
        for (Callback callback : callbacks) {
            if (callback instanceof WSPasswordCallback)
                handle((WSPasswordCallback) callback);
        }

    }

    private void handle(WSPasswordCallback callback) {
        callback.setPassword("changeit");
    }

}
