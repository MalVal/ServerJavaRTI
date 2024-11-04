package Client.GUI;

import Client.Controller.ClientController;
import Client.Controller.ClientInterface;
import Common.Model.Entities.Book;

import java.util.ArrayList;

public interface PurchaseInterface {
    void setController(ClientInterface controller);
    void display();
    void displayConnectionMenu();
    void displayPurchaseMenu();
    void displayMessage(String message, Boolean isError);
    void displayBooks(ArrayList<Book> books);
}
