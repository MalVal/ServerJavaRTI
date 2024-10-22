package Protocol;

import DataBase.DataBaseConnection;
import Request.*;
import Response.*;
import Exception.ConnectionEndException;

import java.net.Socket;
import java.util.ArrayList;

public class EVPP implements Protocol {

    private DataBaseConnection dataBaseConnection;

    public EVPP(DataBaseConnection dataBaseConnection) {
        this.dataBaseConnection = dataBaseConnection;
    }

    @Override
    public String getName() {
        return "EVPP (Ethan Valentin Purchase Protocol)";
    }

    @Override
    public synchronized Response processRequest(Request request, Socket socket) throws ConnectionEndException {

        if(request instanceof ClientRequest) {
            ClientRequest clientRequest = (ClientRequest) request;
            return new ClientResponse(1);
        }
        if(request instanceof AddCaddyItemRequest) {
            AddCaddyItemRequest addCaddyItemRequest = (AddCaddyItemRequest) request;
            return new AddCaddyItemResponse(true);
        }
        if(request instanceof CancelCaddyRequest) {
            CancelCaddyRequest cancelCaddyRequest = (CancelCaddyRequest) request;
            return new CancelCaddyResponse(false);
        }
        if(request instanceof DeleteCaddyItemRequest) {
            DeleteCaddyItemRequest deleteCaddyItemRequest = (DeleteCaddyItemRequest) request;
            return new DeleteCaddyItemResponse(false);
        }
        if(request instanceof PayCaddyRequest) {
            PayCaddyRequest payCaddyRequest = (PayCaddyRequest) request;
            return new PayCaddyResponse(true);
        }
        if(request instanceof SelectBookRequest) {
            SelectBookRequest selectBookRequest = (SelectBookRequest) request;
            return new SelectBookResponse(new ArrayList<>());
        }
        if(request instanceof LogoutRequest) {
            throw new ConnectionEndException(null);
        }

        return null;
    }
}
