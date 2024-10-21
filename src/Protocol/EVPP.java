package Protocol;

import Request.*;
import Response.*;
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
            return new ClientResponse();
        }
        if(request instanceof AddCaddyItemRequest) {
            AddCaddyItemRequest addCaddyItemRequest = (AddCaddyItemRequest) request;
            return new AddCaddyItemResponse();
        }
        if(request instanceof CancelCaddyRequest) {
            CancelCaddyRequest cancelCaddyRequest = (CancelCaddyRequest) request;
            return new CancelCaddyResponse();
        }
        if(request instanceof DeleteCaddyItemRequest) {
            DeleteCaddyItemRequest deleteCaddyItemRequest = (DeleteCaddyItemRequest) request;
            return new DeleteCaddyItemResponse();
        }
        if(request instanceof PayCaddyRequest) {
            PayCaddyRequest payCaddyRequest = (PayCaddyRequest) request;
            return new PayCaddyResponse();
        }
        if(request instanceof SelectBookRequest) {
            SelectBookRequest selectBookRequest = (SelectBookRequest) request;
            return new SelectBookResponse();
        }

        return null;
    }
}
