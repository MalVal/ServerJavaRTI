package Server.Model.Tests;

import Server.Model.DataAcessObject.BookDAO;
import Server.Model.DataAcessObject.CaddyDAO;
import Server.Model.DataBase.DataBaseConnection;
import Server.Model.Entities.Book;
import Server.Model.Entities.Caddy;
import Server.Model.SearchViewModel.BookSearchVM;
import Server.Model.SearchViewModel.CaddySearchVM;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Properties;

public class TestCaddyDAO {

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

            CaddySearchVM caddySearchVM = new CaddySearchVM();
            CaddyDAO dao = new CaddyDAO(dbc);

            dao.save(new Caddy(null, 2, LocalDate.now(), 0.0, false));

            ArrayList<Caddy> caddies = dao.load(caddySearchVM);

            for(Caddy caddy : caddies) {
                System.out.println("----------------------------CADDY---------------------------");
                System.out.println(caddy);
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
