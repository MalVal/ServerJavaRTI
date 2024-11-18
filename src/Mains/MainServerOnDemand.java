package Mains;

import Server.Logger.CmdLogger;
import Server.Model.DataBase.DataBaseConnection;
import Server.Network.Protocol.EVPP;
import Server.Network.ServerTCP.ServerOnDemand.ThreadServerOnDemand;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Properties;

public class MainServerOnDemand {
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
            String portPayment = properties.getProperty("serv.portPayment");

            DataBaseConnection dbc = new DataBaseConnection(type, server, name, user, password);

            ThreadServerOnDemand serverOnDemand = new ThreadServerOnDemand(Integer.parseInt(portPayment), new EVPP(dbc), new CmdLogger());
            serverOnDemand.start();
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
