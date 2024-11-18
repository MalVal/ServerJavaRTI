package Client.Client.Controller;

import Client.Client.GUI.PurchaseInterface;
import Client.Client.Network.Client;
import Common.Model.SearchViewModel.BookSearchVM;
import Common.Network.Request.*;
import Common.Network.Response.*;

public class ClientController implements ClientInterface {

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
