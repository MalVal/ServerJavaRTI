package Server.Exception;

import Network.Response.Response;

public class ConnectionEndException extends Exception
{
    private final Response response;

    public ConnectionEndException(Response response)
    {
        super("End of connection was decided by the protocol");
        this.response = response;
    }

    public Response getResponse() {
        return response;
    }
}