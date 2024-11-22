package Common.Network.RequestSecure;

import Common.Network.Request.Request;

public class AddCaddyItemRequestSecure implements Request {
    private final byte[] data;

    public AddCaddyItemRequestSecure(byte[] data) {
        this.data = data;
    }

    public byte[] getData() {
        return data;
    }
}
