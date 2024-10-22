package Model.Tests;

import Model.DataAcessObject.BookDAO;
import Model.DataBase.DataBaseConnection;
import Model.Entities.Book;
import Model.SearchViewModel.BookSearchVM;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Properties;

public class TestBookDAO {
    public static void main(String[] args) {

        Properties properties = new Properties();
        String fileName = "src/config.properties";

        try (InputStream input = new FileInputStream(fileName)) {
            properties.load(input);
            String type = properties.getProperty("db.type");
            String server = properties.getProperty("db.server");
            String name = properties.getProperty("db.name");
            String user = properties.getProperty("db.user");
            String password = properties.getProperty("db.password");

            System.out.println("Connection ...");
            DataBaseConnection dbc = new DataBaseConnection(type, server, name, user, password);
            System.out.println("Connection à la base de données réussie !");

            BookSearchVM bookSearchVM = new BookSearchVM();
            bookSearchVM.setAuthorLastName("Malchair");
            BookDAO dao = new BookDAO();
            ArrayList<Book> books = dao.loadBook(bookSearchVM, dbc);

            for(Book book : books) {
                System.out.println("----------------------------BOOK---------------------------");
                System.out.println(book);
            }

            dbc.close();
        }
        catch (SQLException sqlEx) {
            System.out.println("SQL exception :" + sqlEx.getMessage());
        }
        catch (ClassNotFoundException classException) {
            System.out.println("Class not found exception :" + classException.getMessage());
        }
        catch (IOException ioException) {
            System.out.println("IO exception :" + ioException.getMessage());
        }
        catch (Exception e) {
            System.out.println("Error :" + e.getMessage());
        }
    }
}
