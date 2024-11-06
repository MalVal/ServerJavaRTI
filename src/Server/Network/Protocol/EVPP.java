package Server.Network.Protocol;

import Server.Model.DataAcessObject.BookDAO;
import Server.Model.DataAcessObject.CaddyDAO;
import Server.Model.DataAcessObject.CaddyItemDAO;
import Server.Model.DataAcessObject.ClientDAO;
import Server.Model.DataBase.DataBaseConnection;
import Server.Exception.ConnectionEndException;
import Common.Model.Entities.Book;
import Server.Model.Entities.Caddy;
import Common.Model.Entities.CaddyItem;
import Common.Model.SearchViewModel.BookSearchVM;
import Server.Model.SearchViewModel.CaddyItemSearchVM;
import Common.Network.Request.*;
import Common.Network.Response.*;
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
                    ClientSearchVM cs = new ClientSearchVM();
                    cs.setLastName(clientRequest.getLastName());
                    cs.setFirstName(clientRequest.getFirstName());
                    ArrayList<Client> clientArrayList = clientDAO.loadClient(cs);
                    if(!clientArrayList.isEmpty()) {
                        throw new Exception("Client already exists");
                    }
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
                if(currentClient != null) {
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

                    CaddyItemSearchVM caddyItemSearchVM = new CaddyItemSearchVM();
                    caddyItemSearchVM.setCaddyId(currentCaddy.getId());
                    caddyItemSearchVM.setBookId(bookArrayList.getFirst().getId());
                    ArrayList<CaddyItem> caddyItemArrayList = caddyItemDAO.load(caddyItemSearchVM);

                    if(!caddyItemArrayList.isEmpty()) {
                        caddyItem.setId(caddyItemArrayList.getFirst().getId());
                    }
                    caddyItemDAO.save(caddyItem);
                    bookDAO.save(bookArrayList.getFirst());

                    return new AddCaddyItemResponse(true);
                }
                else
                {
                    throw new Exception("Not connected");
                }
            }
            catch (Exception e) {
                return new ErrorResponse(e.getMessage());
            }
        }
        if(request instanceof GetCaddyItemRequest) {
            try {
                if(currentClient != null) {
                    CaddyItemSearchVM caddyItemSearchVM = new CaddyItemSearchVM();
                    caddyItemSearchVM.setCaddyId(currentCaddy.getId());
                    CaddyItemDAO caddyItemDAO = new CaddyItemDAO(dataBaseConnection);
                    ArrayList<CaddyItem> items = caddyItemDAO.load(caddyItemSearchVM);
                    return new GetCaddyItemResponse(items);
                }
                else {
                    throw new Exception("Not connected");
                }
            }
            catch (Exception e) {
                return new ErrorResponse(e.getMessage());
            }
        }
        if(request instanceof CancelCaddyRequest) {
            try {
                if(currentClient != null) {
                    CaddyItemDAO caddyItemDAO = new CaddyItemDAO(dataBaseConnection);
                    CaddyItemSearchVM caddyItemSearchVM = new CaddyItemSearchVM();
                    caddyItemSearchVM.setCaddyId(currentCaddy.getId());
                    ArrayList<CaddyItem> caddyItems = caddyItemDAO.load(caddyItemSearchVM);

                    for(CaddyItem caddyItem : caddyItems) {
                        caddyItem.setQuantity(null);
                        caddyItemDAO.delete(caddyItem);
                    }

                    CaddyDAO caddyDAO = new CaddyDAO(dataBaseConnection);
                    caddyDAO.delete(currentCaddy);
                    currentCaddy = null;
                    return new CancelCaddyResponse(false);
                }
                else {
                    throw new Exception("Not connected");
                }
            }
            catch (Exception e) {
                return new ErrorResponse(e.getMessage());
            }
        }
        if(request instanceof DeleteCaddyItemRequest) {
            try {
                if(currentClient != null) {
                    DeleteCaddyItemRequest deleteCaddyItemRequest = (DeleteCaddyItemRequest) request;

                    CaddyItemDAO caddyItemDAO = new CaddyItemDAO(dataBaseConnection);
                    CaddyItemSearchVM caddyItemSearchVM = new CaddyItemSearchVM();
                    caddyItemSearchVM.setCaddyId(currentCaddy.getId());
                    caddyItemSearchVM.setBookId(deleteCaddyItemRequest.getIdBook());
                    ArrayList<CaddyItem> caddyItemArrayList = caddyItemDAO.load(caddyItemSearchVM);

                    if(caddyItemArrayList.isEmpty()) {
                        return new DeleteCaddyItemResponse(false);
                    }

                    if(deleteCaddyItemRequest.getQuantity() == null) {
                        caddyItemDAO.delete(caddyItemArrayList.getFirst().getId());
                        return new DeleteCaddyItemResponse(true);
                    }

                    int quantity = caddyItemArrayList.getFirst().getQuantity() - deleteCaddyItemRequest.getQuantity();
                    CaddyItem caddyItem = new CaddyItem(caddyItemArrayList.getFirst().getId(), currentCaddy.getId(), deleteCaddyItemRequest.getIdBook(), quantity);
                    if(quantity < 0) {
                        return new DeleteCaddyItemResponse(false);
                    }
                    else if(quantity == 0) {
                        caddyItemDAO.delete(caddyItem);
                    }
                    else
                    {
                        caddyItemDAO.save(caddyItem);
                    }

                    return new DeleteCaddyItemResponse(true);
                }
                else {
                    throw new Exception("Not connected");
                }
            }
            catch (Exception e) {
                return new ErrorResponse(e.getMessage());
            }
        }
        if(request instanceof PayCaddyRequest) {
            try {
                if(currentClient != null) {
                    if(currentCaddy == null) {
                        return new PayCaddyResponse(false);
                    }
                    CaddyDAO caddyDAO = new CaddyDAO(dataBaseConnection);
                    currentCaddy.setPayed(true);
                    caddyDAO.save(currentCaddy);
                    currentCaddy = null;
                    return new PayCaddyResponse(true);
                }
                else
                {
                    throw new Exception("Not connected");
                }
            }
            catch (Exception e) {
                return new ErrorResponse(e.getMessage());
            }
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
