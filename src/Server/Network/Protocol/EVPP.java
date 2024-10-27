package Server.Network.Protocol;

import Server.Model.DataAcessObject.BookDAO;
import Server.Model.DataAcessObject.ClientDAO;
import Server.Model.DataBase.DataBaseConnection;
import Server.Exception.ConnectionEndException;
import Server.Network.Request.*;
import Server.Network.Response.*;
import Server.Model.Entities.Client;
import Server.Model.SearchViewModel.ClientSearchVM;

import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;

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
            try {
                ClientRequest clientRequest = (ClientRequest) request;
                ClientDAO clientDAO = new ClientDAO(dataBaseConnection);

                if(clientRequest.isNew()) {
                    clientDAO.save(new Client(null, clientRequest.getLastName(), clientRequest.getFirstName()));
                }

                ClientSearchVM cs = new ClientSearchVM();
                cs.setLastName(clientRequest.getLastName());
                cs.setFirstName(clientRequest.getFirstName());
                ArrayList<Client> clientArrayList = clientDAO.loadClient(cs);

                return new ClientResponse(clientArrayList.getFirst().getId());
            }
            catch (Exception e) {
                return new ErrorResponse(e.getMessage());
            }
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
            BookDAO bookDAO = new BookDAO(dataBaseConnection);
            try {
                return new SelectBookResponse(bookDAO.loadBook(selectBookRequest.getBookSearchVM()));
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
