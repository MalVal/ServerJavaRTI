package Common.Network.ResponseSecure;

import Common.Network.Response.Response;

public class ClientResponseSecure implements Response {

    private final Integer idClient;
    private final ServerSalt salt;

    public ClientResponseSecure(Integer idClient, ServerSalt salt) {
        this.idClient = idClient;
        this.salt = salt;
    }

    public Integer getIdClient() {
        return idClient;
    }

    public ServerSalt getSalt() {
        return salt;
    }
}
