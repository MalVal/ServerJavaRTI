package Common.Network.Response;

import Common.Model.Entities.Book;

import java.util.ArrayList;

public class SelectBookResponse implements Response {

    private ArrayList<Book> books;

    public SelectBookResponse(ArrayList<Book> books) {
        this.books = books;
    }

    public ArrayList<Book> getBooks() {
        return books;
    }

    public void setBooks(ArrayList<Book> books) {
        this.books = books;
    }
}
