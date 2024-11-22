package Client.ClientSecure;

import Client.Interfaces.ClientInterface;
import Client.GUI.PurchaseInterface;
import Client.Network.Client;
import Common.Cryptology.Cryptology;
import Common.Model.SearchViewModel.BookSearchVM;
import Common.Network.RequestSecure.AddCaddyItemRequestSecure;
import Common.Network.RequestSecure.ClientDigestRequest;
import Common.Network.Response.*;
import Common.Network.Request.*;
import Common.Network.ResponseSecure.ClientResponseSecure;
import Common.Network.ResponseSecure.ServerSalt;
import Common.Network.ResponseSecure.SessionKeyResponse;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;

public class ClientControllerSecure implements ClientInterface {

    private Client clientNetwork;
    private final PurchaseInterface gui;
    private Integer currentClientId;
    private final String ipAddress;
    private final Integer portServer;
    private SecretKey sessionKey;

    public ClientControllerSecure(String ipServer, Integer portServer, PurchaseInterface gui) throws Exception {
        // Assign variables
        this.ipAddress = ipServer;
        this.portServer = portServer;
        this.currentClientId = null;
        this.gui = gui;
        this.gui.setController(this);
        this.sessionKey = null;
        // Display the graphic user interface
        gui.display();
        gui.displayConnectionMenu();
    }

    @Override
    public void connection(String lastname, String firstname, Boolean isNew) {
        try {
            // Connect to the server
            this.clientNetwork = new Client(this.ipAddress, this.portServer);
            // Send the lastname and the firstname to the server
            Response response = this.clientNetwork.send(new ClientRequest(lastname, firstname, isNew));
            if(response instanceof ClientResponseSecure) {
                // Retrieve the id and the salt of the server
                this.currentClientId = ((ClientResponseSecure) response).getIdClient();
                ServerSalt salt = ((ClientResponseSecure) response).getSalt();
                // The client send the digest
                response = this.clientNetwork.send(new ClientDigestRequest(this.currentClientId, salt));
                if(response instanceof SessionKeyResponse) {
                    // Décryptage de la clé de session
                    byte[] sessionKeyDecrypt = Cryptology.DecryptASymRSA(this.retrievePrivateKey(), ((SessionKeyResponse) response).getSessionKey());
                    this.sessionKey = new SecretKeySpec(sessionKeyDecrypt,"DESede");
                    // Changer le GUI
                    this.updateData(false);
                    this.gui.displayConnectedPanel();
                }
                else {
                    throw new Exception(((ErrorResponse) response).getMessage());
                }
            }
            else if (response instanceof ErrorResponse) {
                throw new Exception(((ErrorResponse) response).getMessage());
            }
            else {
                throw new Exception("Invalid response");
            }
        }
        catch (Exception exception) {
            if(!exception.getMessage().contains("Connection is impossible")) {
                this.disconnect();
            }
            this.gui.displayMessage(exception.getMessage(), true);
        }
    }

    @Override
    public void retrieveBooks(Integer idBook, String authorLastName, String subjectName, String title, String isbn, Integer publishYear) {
        try {
            Response response = this.clientNetwork.send(new SelectBookRequest(new BookSearchVM(idBook, authorLastName, subjectName, title, isbn, publishYear)));
            if(response instanceof SelectBookResponse) {
                this.gui.displayBooks(((SelectBookResponse) response).getBooks());
            }
            else if (response instanceof ErrorResponse) {
                throw new Exception(((ErrorResponse) response).getMessage());
            }
            else {
                throw new Exception("Invalid response");
            }
        }
        catch (Exception exception) {
            this.gui.displayMessage(exception.getMessage(), true);
        }
    }

    @Override
    public void retrieveCaddy() {
        try {
            Response response = this.clientNetwork.send(new GetCaddyItemRequest());
            if(response instanceof GetCaddyItemResponse) {
                this.gui.displayCaddy(((GetCaddyItemResponse) response).getCaddy());
            }
            else if (response instanceof ErrorResponse) {
                throw new Exception(((ErrorResponse) response).getMessage());
            }
            else {
                throw new Exception("Invalid response");
            }
        }
        catch (Exception exception) {
            this.gui.displayMessage(exception.getMessage(), true);
        }
    }

    @Override
    public void addToCaddy(Integer idBook, Integer quantity) {
        try {
            // Crypter les données à envoyer
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(stream);
            dos.writeInt(idBook);
            dos.writeInt(quantity);
            byte[] message = stream.toByteArray();
            byte[] messageCrypt = Cryptology.CryptTripleSymDES(this.sessionKey, message);
            // Envoyer les données cryptées
            Response response = this.clientNetwork.send(new AddCaddyItemRequestSecure(messageCrypt));
            if(response instanceof AddCaddyItemResponse) {
                if(((AddCaddyItemResponse) response).getResponse()) {
                    this.updateData(true);
                    this.gui.displayMessage("Add with success", false);
                }
                else {
                    this.gui.displayMessage("Error when adding", true);
                }
            }
            else if (response instanceof ErrorResponse) {
                throw new Exception(((ErrorResponse) response).getMessage());
            }
            else {
                throw new Exception("Invalid response");
            }
        }
        catch (Exception exception) {
            this.gui.displayMessage(exception.getMessage(), true);
        }
    }

    @Override
    public void removeFromCaddy(Integer idBook, Integer quantity) {
        try {
            Response response = this.clientNetwork.send(new DeleteCaddyItemRequest(idBook, quantity));
            if(response instanceof DeleteCaddyItemResponse) {
                if(((DeleteCaddyItemResponse) response).getResponse()) {
                    this.updateData(true);
                    this.gui.displayMessage("Remove with success", false);
                }
                else {
                    this.gui.displayMessage("Error when removing", true);
                }
            }
            else if (response instanceof ErrorResponse) {
                throw new Exception(((ErrorResponse) response).getMessage());
            }
            else {
                throw new Exception("Invalid response");
            }
        }
        catch (Exception exception) {
            this.gui.displayMessage(exception.getMessage(), true);
        }
    }

    @Override
    public void cancelCaddy() {
        try {
            Response response = this.clientNetwork.send(new CancelCaddyRequest(currentClientId));
            if(response instanceof CancelCaddyResponse) {
                if(((CancelCaddyResponse) response).getResponse()) {
                    this.disconnect();
                    this.gui.displayMessage("Cancel success", false);
                }
                else {
                    this.gui.displayMessage("Error when cancelling", true);
                }
            }
            else if (response instanceof ErrorResponse) {
                throw new Exception(((ErrorResponse) response).getMessage());
            }
            else {
                throw new Exception("Invalid response");
            }
        }
        catch (Exception exception) {
            this.gui.displayMessage(exception.getMessage(), true);
        }
    }

    @Override
    public void payCaddy() {
        try {
            Response response = this.clientNetwork.send(new PayCaddyRequest(currentClientId));
            if(response instanceof PayCaddyResponse) {
                if(((PayCaddyResponse) response).getResponse()) {
                    this.disconnect();
                    this.gui.displayMessage("Payed with success", false);
                }
                else {
                    this.gui.displayMessage("Error when paying", true);
                }
            }
            else if (response instanceof ErrorResponse) {
                throw new Exception(((ErrorResponse) response).getMessage());
            }
            else {
                throw new Exception("Invalid response");
            }
        }
        catch (Exception exception) {
            this.gui.displayMessage(exception.getMessage(), true);
        }
    }

    private void disconnect() {
        try {
            this.gui.setTotalAmount(0.0);
            this.gui.displayConnectionMenu();
            this.currentClientId = null;
            this.clientNetwork.send(new LogoutRequest());
        }
        catch (Exception ignored) {}
    }

    private void updateData(Boolean caddy) {
        this.retrieveBooks(null, null, null, null, null, null);
        if(caddy) {
            this.retrieveCaddy();
            try {
                Response response = this.clientNetwork.send(new GetCaddyPriceRequest());
                if(response instanceof GetCaddyPriceResponse) {
                    this.gui.setTotalAmount(((GetCaddyPriceResponse) response).getCaddyPrice());
                }
                else if (response instanceof ErrorResponse) {
                    throw new Exception(((ErrorResponse) response).getMessage());
                }
                else {
                    throw new Exception("Invalid response");
                }
            }
            catch (Exception exception) {
                this.gui.displayMessage(exception.getMessage(), true);
            }
        }
        else {
            this.gui.clearCaddy();
        }
    }

    private PrivateKey retrievePrivateKey() throws KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException, UnrecoverableKeyException {
        KeyStore ks = KeyStore.getInstance("JKS");
        ks.load(new FileInputStream("resources/clientKeyStore.jks"),"123".toCharArray());
        return (PrivateKey) ks.getKey("clientevpps", "123".toCharArray());
    }
}
