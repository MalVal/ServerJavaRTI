package Server.Network.Protocol;

import Server.Model.DataAcessObject.BookDAO;
import Server.Model.DataBase.DataBaseConnection;
import Server.Exception.ConnectionEndException;
import Network.Request.*;
import Network.Response.*;

import java.net.Socket;
import java.sql.SQLException;

public class EVPP implements Protocol {

    private final DataBaseConnection dataBaseConnection;

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
        if(request instanceof SelectBookRequest selectBookRequest) {
            BookDAO bookDAO = new BookDAO();
            try {
                return new SelectBookResponse(bookDAO.loadBook(selectBookRequest.getBookSearchVM(), dataBaseConnection));
            }
            catch (SQLException sqlException) {
                return new ErrorResponse(sqlException.getMessage());
            }
        }
        if(request instanceof LogoutRequest) {
            throw new ConnectionEndException(null);
        }

        return null;
    }
}
