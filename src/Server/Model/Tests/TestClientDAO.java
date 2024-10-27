package Server.Model.Tests;


import Server.Model.DataAcessObject.ClientDAO;
import Server.Model.DataBase.DataBaseConnection;
import Server.Model.Entities.Client;
import Server.Model.SearchViewModel.ClientSearchVM;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Properties;

public class TestClientDAO {
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

            ClientSearchVM clientSearchVM = new ClientSearchVM();
            ClientDAO dao = new ClientDAO(dbc);
            Client c = new Client();
            c.setLastName("Malchair");
            c.setFirstName("Valentin");
            dao.save(c);
            ArrayList<Client> clients = dao.loadClient(clientSearchVM);

            for(Client client : clients) {
                System.out.println("----------------------------CLIENT---------------------------");
                System.out.println(client);
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
