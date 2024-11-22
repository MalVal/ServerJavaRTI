package Common.Network.RequestSecure;

import Common.Network.Request.Request;

public class ClientRequestSecure implements Request {
    private final byte[] data1;

    public ClientRequestSecure(byte[] data1) {
        this.data1 = data1;
    }

    public byte[] getData1() {
        return data1;
    }
}