package Common.Network.Request;

public class PayCaddyRequest implements Request {

    private Integer idClient;

    public PayCaddyRequest(Integer idClient) {
        this.idClient = idClient;
    }

    public Integer getIdClient() {
        return idClient;
    }

    public void setIdClient(Integer idClient) {
        this.idClient = idClient;
    }
}
