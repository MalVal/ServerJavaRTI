package Server.Network.Response;

public class CancelCaddyResponse implements Response {

    private Boolean response;

    public CancelCaddyResponse(Boolean response) {
        this.response = response;
    }

    public Boolean getResponse() {
        return response;
    }

    public void setResponse(Boolean response) {
        this.response = response;
    }
}
