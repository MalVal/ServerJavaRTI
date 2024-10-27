package Server.Network.Response;

public class AddCaddyItemResponse implements Response {

    private Boolean response;

    public AddCaddyItemResponse(Boolean response) {
        this.response = response;
    }

    public Boolean getResponse() {
        return response;
    }

    public void setResponse(Boolean response) {
        this.response = response;
    }
}
