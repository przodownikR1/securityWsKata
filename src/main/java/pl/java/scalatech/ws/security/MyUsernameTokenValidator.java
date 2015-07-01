package pl.java.scalatech.ws.security;

import org.apache.wss4j.common.ext.WSSecurityException;
import org.apache.wss4j.dom.handler.RequestData;
import org.apache.wss4j.dom.message.token.UsernameToken;
import org.apache.wss4j.dom.validate.UsernameTokenValidator;

public class MyUsernameTokenValidator extends UsernameTokenValidator {

    @Override
    protected void verifyPlaintextPassword(UsernameToken usernameToken, RequestData data) throws WSSecurityException {
        if (usernameToken != null && usernameToken.getPassword() != null) {
            try {
                String password = usernameToken.getPassword(); // Some kind of encryption
                if (!password.equals("")) {
                    usernameToken.setPassword(password);
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException();
            }
        }
        super.verifyDigestPassword(usernameToken, data);
    }

}
