package Client.Client.GUI;

import Client.Client.Controller.ClientInterface;
import Common.Model.Entities.Book;
import Common.Model.Entities.CaddyItem;

import java.util.ArrayList;

public interface PurchaseInterface {
    void setController(ClientInterface controller);
    void display();
    void displayConnectionMenu();
    void displayConnectedPanel();
    void displayMessage(String message, Boolean isError);
    void displayBooks(ArrayList<Book> books);
    void displayCaddy(ArrayList<CaddyItem> items);
    void clearCaddy();
    void setTotalAmount(Double total);
}
