package Protocol;

import Request.Request;
import Response.Response;
import Request.*;
import Exception.ConnectionEndException;

import java.net.Socket;

public class EVPP implements Protocol {
    @Override
    public String getName() {
        return "EVPP (Ethan Valentin Purchase Protocol)";
    }

    @Override
    public Response processRequest(Request request, Socket socket) throws ConnectionEndException {

        if(request instanceof ClientRequest) {
            ClientRequest clientRequest = (ClientRequest) request;
        }
        if(request instanceof AddCaddyItemRequest) {
            AddCaddyItemRequest addCaddyItemRequest = (AddCaddyItemRequest) request;
        }
        if(request instanceof CancelCaddyRequest) {
            CancelCaddyRequest cancelCaddyRequest = (CancelCaddyRequest) request;
        }
        if(request instanceof DeleteCaddyItemRequest) {
            DeleteCaddyItemRequest deleteCaddyItemRequest = (DeleteCaddyItemRequest) request;
        }
        if(request instanceof PayCaddyRequest) {
            PayCaddyRequest payCaddyRequest = (PayCaddyRequest) request;
        }
        if(request instanceof SelectBookRequest) {
            SelectBookRequest selectBookRequest = (SelectBookRequest) request;
        }

        return null;
    }
}
