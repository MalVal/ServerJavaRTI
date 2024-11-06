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

    public ClientController(String ipServer, Integer portServer, PurchaseInterface gui) {
        this.currentClientId = null;
        this.gui = gui;
        this.gui.setController(this);
        gui.display();
        gui.displayConnectionMenu();
        try {
            this.clientNetwork = new Client(ipServer, portServer);
        }
        catch (Exception e) {
            this.gui.displayMessage("Connection is impossible : " + e.getMessage(), true);
        }
    }

    @Override
    public void connection(String lastname, String firstname, Boolean isNew) {
        try {
            Response response = this.clientNetwork.send(new ClientRequest(lastname, firstname, isNew));
            if(response instanceof ClientResponse) {
                this.currentClientId = ((ClientResponse) response).getIdClient();
                this.retrieveBooks(null, null, null, null, null, null);
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
                    this.retrieveBooks(null, null, null, null, null, null);
                    this.gui.displayMessage("Add with success", false);
                    this.retrieveCaddy();
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
                    this.retrieveBooks(null, null, null, null, null, null);
                    this.gui.displayMessage("Remove with success", false);
                    this.retrieveCaddy();
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
}
