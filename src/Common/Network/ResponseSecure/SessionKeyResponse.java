package Common.Network.ResponseSecure;

import Common.Network.Response.Response;

public class SessionKeyResponse implements Response {
    final byte[] sessionKey;

    public SessionKeyResponse(byte[] sessionKey) {
        this.sessionKey = sessionKey;
    }

    public byte[] getSessionKey() {
        return sessionKey;
    }
}
