package Client.Controller;

import Client.GUI.MainWindow;
import Client.GUI.PurchaseInterface;
import Client.Network.Client;
import Common.Model.SearchViewModel.BookSearchVM;
import Common.Network.Request.*;
import Common.Network.Response.*;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

public class ClientController implements ClientInterface {

    public static void main(String[] args) {

        Properties properties = new Properties();
        String fileName = "src/config.properties";

        try (InputStream input = new FileInputStream(fileName)) {
            properties.load(input);
            String addressIp = properties.getProperty("serv.address");
            String portPayment = properties.getProperty("serv.portPayment");

            new ClientController(addressIp, Integer.parseInt(portPayment), new MainWindow());
        }
        catch (Exception e) {
            System.out.println("Error :" + e.getMessage());
        }
    }

    private Client clientNetwork;
    private final PurchaseInterface gui;
    private Integer currentClientId;
    private final String ipAddress;
    private final Integer portServer;

    public ClientController(String ipServer, Integer portServer, PurchaseInterface gui) {
        this.ipAddress = ipServer;
        this.portServer = portServer;
        this.currentClientId = null;
        this.gui = gui;
        this.gui.setController(this);
        gui.display();
        gui.displayConnectionMenu();
    }

    @Override
    public void connection(String lastname, String firstname, Boolean isNew) {
        try {
            try {
                this.clientNetwork = new Client(this.ipAddress, this.portServer);
            }
            catch (Exception e) {
                throw new Exception("Connection is impossible : " + e.getMessage());
            }

            Response response = this.clientNetwork.send(new ClientRequest(lastname, firstname, isNew));
            if(response instanceof ClientResponse) {
                this.currentClientId = ((ClientResponse) response).getIdClient();
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

    private void disconnect() {
        try {
            this.gui.displayConnectionMenu();
            this.currentClientId = null;
            this.clientNetwork.send(new LogoutRequest());
        }
        catch (Exception exception) {
            System.out.println(exception.getMessage());
        }
    }

    private void updateData(Boolean caddy) {
        this.retrieveBooks(null, null, null, null, null, null);
        if(caddy) {
            this.retrieveCaddy();
        }
        else {
            this.gui.clearCaddy();
        }
    }
}
