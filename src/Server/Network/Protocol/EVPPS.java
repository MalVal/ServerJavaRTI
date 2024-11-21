package Server.Network.Protocol;

import Common.Network.RequestSecure.ClientDigestRequest;
import Common.Network.ResponseSecure.ClientResponseSecure;
import Common.Network.ResponseSecure.ServerSalt;
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
import Server.Model.SearchViewModel.CaddySearchVM;
import Server.Model.SearchViewModel.ClientSearchVM;

import java.net.Socket;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;

public class EVPPS implements Protocol {

    private final DataBaseConnection dataBaseConnection;
    private Caddy currentCaddy;
    private Client currentClient;
    private final PublicKey clientPublicKey;
    private PrivateKey sessionKey;

    private ServerSalt salt;

    public EVPPS(DataBaseConnection dataBaseConnection) {
        this.dataBaseConnection = dataBaseConnection;
        this.currentCaddy = null;
        this.currentClient = null;
        this.salt = null;
        this.clientPublicKey = null;
        this.sessionKey = null;
    }

    private void resetProtocol() {
        this.currentCaddy = null;
        this.currentClient = null;
        this.salt = null;
    }

    @Override
    public String getName() {
        return "EVPPS (Ethan Valentin Purchase Protocol Secure,Or EVPPP Ethan Valentin Purchase Protocol Pro)";
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
                    if(clientArrayList.isEmpty()) {
                        throw new Exception("Client doesn't exist");
                    }
                    currentClient = clientArrayList.getFirst();
                }

                this.salt = new ServerSalt();
                return new ClientResponseSecure(currentClient.getId(), this.salt);
            }
            catch (Exception e) {
                return new ErrorResponse(e.getMessage());
            }
        }
        if(request instanceof ClientDigestRequest cdr) {
            try {
                if(cdr.VerifyDigest(this.currentClient.getId(), this.salt)) {
                    // Digest is correct
                }
                else {
                    // If the digest is incorrect
                    this.resetProtocol();
                    return new ErrorResponse("Digest verification failed");
                }
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

                    double price = bookArrayList.getFirst().getPrice() * addCaddyItemRequest.getQuantity();
                    CaddyDAO caddyDAO = new CaddyDAO(dataBaseConnection);
                    CaddySearchVM caddySearchVM = new CaddySearchVM(this.currentCaddy.getId());
                    ArrayList<Caddy> caddy = caddyDAO.load(caddySearchVM);
                    price = price + caddy.getFirst().getAmount();
                    caddy.getFirst().setAmount(price);
                    caddyDAO.save(caddy.getFirst());

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
                        BookDAO bookDAO = new BookDAO(dataBaseConnection);
                        BookSearchVM bookSearchVM = new BookSearchVM();
                        bookSearchVM.setIdBook(caddyItem.getBookId());
                        ArrayList<Book> bookArrayList = bookDAO.loadBook(bookSearchVM);
                        int newQuantityToAdd = bookArrayList.getFirst().getStockQuantity();
                        newQuantityToAdd += caddyItem.getQuantity();
                        bookArrayList.getFirst().setStockQuantity(newQuantityToAdd);
                        bookDAO.save(bookArrayList.getFirst());
                        caddyItem.setQuantity(null);
                        caddyItemDAO.delete(caddyItem);
                    }

                    CaddyDAO caddyDAO = new CaddyDAO(dataBaseConnection);
                    caddyDAO.delete(currentCaddy);
                    currentCaddy = null;
                    return new CancelCaddyResponse(true);
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

                    BookDAO bookDAO = new BookDAO(dataBaseConnection);
                    BookSearchVM bookSearchVM = new BookSearchVM();
                    bookSearchVM.setIdBook(deleteCaddyItemRequest.getIdBook());
                    ArrayList<Book> bookArrayList = bookDAO.loadBook(bookSearchVM);
                    int newQuantityToAdd = bookArrayList.getFirst().getStockQuantity();
                    newQuantityToAdd += deleteCaddyItemRequest.getQuantity();
                    bookArrayList.getFirst().setStockQuantity(newQuantityToAdd);
                    bookDAO.save(bookArrayList.getFirst());

                    double price = bookArrayList.getFirst().getPrice() * deleteCaddyItemRequest.getQuantity();
                    CaddyDAO caddyDAO = new CaddyDAO(dataBaseConnection);
                    CaddySearchVM caddySearchVM = new CaddySearchVM(this.currentCaddy.getId());
                    ArrayList<Caddy> caddy = caddyDAO.load(caddySearchVM);
                    price = caddy.getFirst().getAmount() - price;
                    caddy.getFirst().setAmount(price);
                    caddyDAO.save(caddy.getFirst());

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
            try {
                BookDAO bookDAO = new BookDAO(dataBaseConnection);
                return new SelectBookResponse(bookDAO.loadBook(selectBookRequest.getBookSearchVM()));
            }
            catch (SQLException sqlException) {
                return new ErrorResponse(sqlException.getMessage());
            }
        }
        if(request instanceof GetCaddyPriceRequest) {
            try {
                if(currentClient != null) {
                    CaddyDAO caddyDAO = new CaddyDAO(dataBaseConnection);
                    ArrayList<Caddy> caddy = caddyDAO.load(new CaddySearchVM(currentCaddy.getId()));
                    return new GetCaddyPriceResponse(caddy.getFirst().getAmount());
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
        if(request instanceof LogoutRequest) {
            throw new ConnectionEndException(null);
        }

        return null;
    }
}
