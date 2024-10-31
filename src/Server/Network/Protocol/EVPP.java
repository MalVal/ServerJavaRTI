package Server.Network.Protocol;

import Server.Model.DataAcessObject.BookDAO;
import Server.Model.DataAcessObject.CaddyDAO;
import Server.Model.DataAcessObject.CaddyItemDAO;
import Server.Model.DataAcessObject.ClientDAO;
import Server.Model.DataBase.DataBaseConnection;
import Server.Exception.ConnectionEndException;
import Server.Model.Entities.Book;
import Server.Model.Entities.Caddy;
import Server.Model.Entities.CaddyItem;
import Server.Model.SearchViewModel.BookSearchVM;
import Server.Network.Request.*;
import Server.Network.Response.*;
import Server.Model.Entities.Client;
import Server.Model.SearchViewModel.ClientSearchVM;

import java.net.Socket;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;

public class EVPP implements Protocol {

    private final DataBaseConnection dataBaseConnection;
    private Caddy currentCaddy;
    private Client currentClient;

    public EVPP(DataBaseConnection dataBaseConnection) {
        this.dataBaseConnection = dataBaseConnection;
        this.currentCaddy = null;
        this.currentClient = null;
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
                    currentClient = new Client(null, clientRequest.getLastName(), clientRequest.getFirstName());
                    clientDAO.save(currentClient);
                }
                else {
                    ClientSearchVM cs = new ClientSearchVM();
                    cs.setLastName(clientRequest.getLastName());
                    cs.setFirstName(clientRequest.getFirstName());
                    ArrayList<Client> clientArrayList = clientDAO.loadClient(cs);
                    currentClient = clientArrayList.getFirst();
                }

                return new ClientResponse(currentClient.getId());
            }
            catch (Exception e) {
                return new ErrorResponse(e.getMessage());
            }
        }
        if(request instanceof AddCaddyItemRequest) {
            try {
                AddCaddyItemRequest addCaddyItemRequest = (AddCaddyItemRequest) request;

                if(currentCaddy == null) {
                    currentCaddy = new Caddy(null, currentClient.getId(), LocalDate.now(), 0.0, false);
                    CaddyDAO caddyDAO = new CaddyDAO(dataBaseConnection);
                    caddyDAO.save(currentCaddy);
                }

                CaddyItemDAO caddyItemDAO = new CaddyItemDAO(dataBaseConnection);
                CaddyItem caddyItem = new CaddyItem(null, currentCaddy.getId(), addCaddyItemRequest.getIdBook(), addCaddyItemRequest.getQuantity());

                BookDAO bookDAO = new BookDAO(dataBaseConnection);
                BookSearchVM bookSearchVM = new BookSearchVM();
                bookSearchVM.setIdBook(addCaddyItemRequest.getIdBook());
                ArrayList<Book> bookArrayList = bookDAO.loadBook(bookSearchVM);

                if(bookArrayList.getFirst().getStockQuantity() < addCaddyItemRequest.getQuantity()) {
                    return new AddCaddyItemResponse(false);
                }

                int quantity = bookArrayList.getFirst().getStockQuantity();
                quantity -= addCaddyItemRequest.getQuantity();
                bookArrayList.getFirst().setStockQuantity(quantity);

                bookDAO.save(bookArrayList.getFirst());
                caddyItemDAO.save(caddyItem);
                return new AddCaddyItemResponse(true);
            }
            catch (Exception e) {
                return new ErrorResponse(e.getMessage());
            }
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
