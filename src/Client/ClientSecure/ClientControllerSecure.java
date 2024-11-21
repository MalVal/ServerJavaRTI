package Client.ClientSecure;

import Client.Interfaces.ClientInterface;
import Client.GUI.PurchaseInterface;
import Client.Network.Client;
import Common.Cryptology.Cryptology;
import Common.Model.SearchViewModel.BookSearchVM;
import Common.Network.RequestSecure.ClientDigestRequest;
import Common.Network.Response.*;
import Common.Network.Request.*;
import Common.Network.ResponseSecure.ClientResponseSecure;
import Common.Network.ResponseSecure.ServerSalt;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.*;
import java.security.cert.X509Certificate;

public class ClientControllerSecure implements ClientInterface {

    private Client clientNetwork;
    private final PurchaseInterface gui;
    private Integer currentClientId;
    private final String ipAddress;
    private final Integer portServer;
    private final PrivateKey privateKey;
    private final PublicKey publicKey;
    private SecretKey sessionKey;
    private final X509Certificate certificate;

    public ClientControllerSecure(String ipServer, Integer portServer, PurchaseInterface gui) throws Exception {
        this.ipAddress = ipServer;
        this.portServer = portServer;
        this.currentClientId = null;
        this.gui = gui;
        this.gui.setController(this);

        // Generate the keys
        KeyPairGenerator cleGen = KeyPairGenerator.getInstance("RSA","BC");
        cleGen.initialize(1024,new SecureRandom());
        KeyPair keys = cleGen.generateKeyPair();
        this.publicKey = keys.getPublic();
        this.privateKey = keys.getPrivate();
        // Construire un certificat auto-signé
        this.certificate = // Comment avoir le certificat pour l'envoyer ?

        // Display the graphic user interface
        gui.display();
        gui.displayConnectionMenu();
    }

    @Override
    public void connection(String lastname, String firstname, Boolean isNew) {
        try {
            try {
                // Connection to the server
                this.clientNetwork = new Client(this.ipAddress, this.portServer);
                // Generate the session key
                KeyGenerator keyGen = KeyGenerator.getInstance("DESede", "BC");
                keyGen.init(168);
                this.sessionKey = keyGen.generateKey();
            }
            catch (Exception e) {
                throw new Exception("Connection is impossible : " + e.getMessage());
            }
            // Crypt the session key
            byte[] sessionKeyCrypt;
            sessionKeyCrypt = Cryptology.CryptASymRSA(this.privateKey, sessionKey.getEncoded());
            // Send the lastname and the firstname to the server
            Response response = this.clientNetwork.send(new ClientRequest(lastname, firstname, isNew));
            if(response instanceof ClientResponseSecure) {
                // Retrieve the id and the salt of the server
                this.currentClientId = ((ClientResponseSecure) response).getIdClient();
                ServerSalt salt = ((ClientResponseSecure) response).getSalt();
                // The client send the digest
                response = this.clientNetwork.send(new ClientDigestRequest(this.currentClientId, salt));
                // ????

                // if response ok ->
                this.updateData(false);
                this.gui.displayConnectedPanel();
            }
            else if (response instanceof ErrorResponse) {
                throw new Exception(((ErrorResponse) response).getMessage());
            }
            else {
                throw new Exception("Invalid response");
            }
        }
        catch (Exception exception) {
            if(!exception.getMessage().contains("Connection is impossible"))
            {
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
            Response response = this.clientNetwork.send(new AddCaddyItemRequest(idBook, quantity));
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
}
