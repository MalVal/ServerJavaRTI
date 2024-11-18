package Common.Network.ResponseSecure;

import Common.Network.Response.Response;

public class ClientResponseSecure implements Response {

    private Integer idClient;
    private ServerSalt salt;

    public ClientResponseSecure(Integer idClient, ServerSalt salt) {
        this.idClient = idClient;
        this.salt = salt;
    }

    public Integer getIdClient() {
        return idClient;
    }

    public void setIdClient(Integer idClient) {
        this.idClient = idClient;
    }

    public ServerSalt getSalt() {
        return salt;
    }

    public void setSalt(ServerSalt salt) {
        this.salt = salt;
    }
}
