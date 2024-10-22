package Network.Response;

public class PayCaddyResponse implements Response {

    private Boolean response;

    public PayCaddyResponse(Boolean response) {
        this.response = response;
    }

    public Boolean getResponse() {
        return response;
    }

    public void setResponse(Boolean response) {
        this.response = response;
    }
}
