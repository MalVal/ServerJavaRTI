package Common.Network.ResponseSecure;

import java.io.Serializable;
import java.util.Date;

public class ServerSalt implements Serializable {
    private final Long time;
    private final Double random;

    public ServerSalt() {
        this.time = new Date().getTime();
        this.random = Math.random();
    }

    public long getTime() {
        return time;
    }

    public double getRandom() {
        return random;
    }
}
