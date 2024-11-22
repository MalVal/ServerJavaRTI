package Mains;

import Client.ClientSecure.ClientControllerSecure;
import Client.GUI.MainWindow;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.io.FileInputStream;
import java.io.InputStream;

import java.security.Security;
import java.util.Properties;

public class MainClientSecure {
    public static void main(String[] args) {
        Properties properties = new Properties();
        String fileName = "src/config.properties";
        try (InputStream input = new FileInputStream(fileName)) {
            // Ajouter les algorithmes
            Security.addProvider(new BouncyCastleProvider());
            // Rechercher les informations du serveur
            properties.load(input);
            String addressIp = properties.getProperty("serv.address");
            String portPayment = properties.getProperty("serv.portPaymentSecure");
            // Création du client
            new ClientControllerSecure(addressIp, Integer.parseInt(portPayment), new MainWindow());
        }
        catch (Exception e) {
            System.out.println("Error :" + e.getMessage());
        }
    }
}
