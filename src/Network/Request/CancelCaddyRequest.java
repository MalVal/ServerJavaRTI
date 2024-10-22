package Network.Request;

public class CancelCaddyRequest implements Request {

    private Integer idClient;

    public CancelCaddyRequest(Integer idClient) {
        this.idClient = idClient;
    }

    public Integer getIdClient() {
        return idClient;
    }

    public void setIdClient(Integer idClient) {
        this.idClient = idClient;
    }
}
