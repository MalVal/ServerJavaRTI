package Mains;

import Server.Logger.CmdLogger;
import Server.Model.DataBase.DataBaseConnection;
import Server.Network.Protocol.EVPPS;
import Server.Network.ServerTCP.ServerPool.ThreadServerPool;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.Security;
import java.sql.SQLException;
import java.util.Properties;

public class MainServerPool {
    public static void main(String[] args) {

        Security.addProvider(new BouncyCastleProvider());

        Properties properties = new Properties();
        String fileName = "src/config.properties";

        try (InputStream input = new FileInputStream(fileName)) {
            // Récupération des informations du serveur
            properties.load(input);
            String type = properties.getProperty("db.type");
            String server = properties.getProperty("db.server");
            String name = properties.getProperty("db.name");
            String user = properties.getProperty("db.user");
            String password = properties.getProperty("db.password");
            String portPayment = properties.getProperty("serv.portPaymentSecure");
            // Création de l'unique connection à la db
            DataBaseConnection dbc = new DataBaseConnection(type, server, name, user, password);
            // Création du serveur
            ThreadServerPool serverPool = new ThreadServerPool(Integer.parseInt(portPayment), new EVPPS(dbc), 5,new CmdLogger());
            serverPool.start();
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
