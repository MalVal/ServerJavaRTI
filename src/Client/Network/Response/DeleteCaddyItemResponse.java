package Client.Network.Response;

public class DeleteCaddyItemResponse implements Response {

    private Boolean response;

    public DeleteCaddyItemResponse(Boolean response) {
        this.response = response;
    }

    public Boolean getResponse() {
        return response;
    }

    public void setResponse(Boolean response) {
        this.response = response;
    }
}
