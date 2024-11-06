package Client.GUI;

import Client.Controller.ClientInterface;
import Common.Model.Entities.Book;

import java.util.ArrayList;

public interface PurchaseInterface {
    void setController(ClientInterface controller);
    void display();
    void displayConnectionMenu();
    void displayConnectedPanel();
    void displayMessage(String message, Boolean isError);
    void displayBooks(ArrayList<Book> books);
}
