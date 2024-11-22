package Server.Network.Protocol;

import Common.Cryptology.Cryptology;
import Common.Network.RequestSecure.AddCaddyItemRequestSecure;
import Common.Network.RequestSecure.ClientDigestRequest;
import Common.Network.ResponseSecure.ClientResponseSecure;
import Common.Network.ResponseSecure.ServerSalt;
import Common.Network.ResponseSecure.SessionKeyResponse;
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

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;

public class EVPPS implements Protocol {

    private final DataBaseConnection dataBaseConnection;
    private Caddy currentCaddy;
    private Client currentClient;
    private SecretKey sessionKey;

    private ServerSalt salt;

    public EVPPS(DataBaseConnection dataBaseConnection) {
        this.dataBaseConnection = dataBaseConnection;
        this.currentCaddy = null;
        this.currentClient = null;
        this.salt = null;
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
                // Verify if it's a new client
                if(clientRequest.isNew()) {
                    ClientSearchVM cs = new ClientSearchVM();
                    cs.setLastName(clientRequest.getLastName());
                    cs.setFirstName(clientRequest.getFirstName());
                    ArrayList<Client> clientArrayList = clientDAO.loadClient(cs);
                    if(!clientArrayList.isEmpty()) {
                        throw new Exception("Client already exists");
                    }
                    currentClient = new Client(null, clientRequest.getLastName(), clientRequest.getFirstName());
                    // Create the new client
                    clientDAO.save(currentClient);
                }
                else {
                    // The client exists
                    ClientSearchVM cs = new ClientSearchVM();
                    cs.setLastName(clientRequest.getLastName());
                    cs.setFirstName(clientRequest.getFirstName());
                    ArrayList<Client> clientArrayList = clientDAO.loadClient(cs);
                    if(clientArrayList.isEmpty()) {
                        throw new Exception("Client doesn't exist");
                    }
                    currentClient = clientArrayList.getFirst();
                }
                // Create the salt
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
                    // Generate the session key
                    KeyGenerator keyGen = KeyGenerator.getInstance("DESede", "BC");
                    keyGen.init(168);
                    this.sessionKey = keyGen.generateKey();
                    // Crypt the session key
                    byte[] sessionKeyCrypt = Cryptology.CryptASymRSA(this.retrievePublicKeyClient(), sessionKey.getEncoded());
                    // Send the session key to the client
                    return new SessionKeyResponse(sessionKeyCrypt);
                }
                else {
                    // If the digest is incorrect
                    this.resetProtocol();
                    // Send an error
                    return new ErrorResponse("Digest verification failed");
                }
            }
            catch (Exception e) {
                return new ErrorResponse(e.getMessage());
            }
        }
        if(request instanceof AddCaddyItemRequestSecure) {
            try {
                if(currentClient != null) {
                    AddCaddyItemRequestSecure addCaddyItemRequestSecure = (AddCaddyItemRequestSecure) request;

                    if(currentCaddy == null) {
                        currentCaddy = new Caddy(null, currentClient.getId(), LocalDate.now(), 0.0, false);
                        CaddyDAO caddyDAO = new CaddyDAO(dataBaseConnection);
                        caddyDAO.save(currentCaddy);
                    }
                    // Decrypt the data
                    byte[] dataCrypt = addCaddyItemRequestSecure.getData();
                    byte[] data = Cryptology.DecryptTripleSymDES(this.sessionKey, dataCrypt);
                    ByteArrayInputStream stream = new ByteArrayInputStream(data);
                    DataInputStream dis = new DataInputStream(stream);
                    Integer idBook = dis.readInt();
                    Integer quantity = dis.readInt();
                    // Créer le caddy item
                    CaddyItemDAO caddyItemDAO = new CaddyItemDAO(dataBaseConnection);
                    CaddyItem caddyItem = new CaddyItem(null, currentCaddy.getId(), idBook, quantity);
                    // Vérifier la quantité
                    BookDAO bookDAO = new BookDAO(dataBaseConnection);
                    BookSearchVM bookSearchVM = new BookSearchVM();
                    bookSearchVM.setIdBook(idBook);
                    ArrayList<Book> bookArrayList = bookDAO.loadBook(bookSearchVM);
                    if(bookArrayList.getFirst().getStockQuantity() < quantity) {
                        return new AddCaddyItemResponse(false);
                    }
                    // Mettre à jour la quantité
                    int newQuantity = bookArrayList.getFirst().getStockQuantity();
                    newQuantity -= quantity;
                    bookArrayList.getFirst().setStockQuantity(newQuantity);
                    // Mettre à jour le prix total du caddy
                    double price = bookArrayList.getFirst().getPrice() * quantity;
                    CaddyDAO caddyDAO = new CaddyDAO(dataBaseConnection);
                    CaddySearchVM caddySearchVM = new CaddySearchVM(this.currentCaddy.getId());
                    ArrayList<Caddy> caddy = caddyDAO.load(caddySearchVM);
                    price = price + caddy.getFirst().getAmount();
                    caddy.getFirst().setAmount(price);
                    caddyDAO.save(caddy.getFirst());
                    // Ajouter le caddy item au caddy
                    CaddyItemSearchVM caddyItemSearchVM = new CaddyItemSearchVM();
                    caddyItemSearchVM.setCaddyId(currentCaddy.getId());
                    caddyItemSearchVM.setBookId(bookArrayList.getFirst().getId());
                    ArrayList<CaddyItem> caddyItemArrayList = caddyItemDAO.load(caddyItemSearchVM);
                    if(!caddyItemArrayList.isEmpty()) {
                        caddyItem.setId(caddyItemArrayList.getFirst().getId());
                    }
                    caddyItemDAO.save(caddyItem);
                    bookDAO.save(bookArrayList.getFirst());
                    // Répondre au client
                    return new AddCaddyItemResponse(true);
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

    private PublicKey retrievePublicKeyClient() throws KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException {
        KeyStore ks = KeyStore.getInstance("JKS");
        ks.load(new FileInputStream("resources/serverKeyStore.jks"),"123".toCharArray());
        X509Certificate cert = (X509Certificate)ks.getCertificate("clientevpps");
        return cert.getPublicKey();
    }
}
