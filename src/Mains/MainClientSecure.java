package Mains;

import Client.ClientSecure.ClientControllerSecure;
import Client.GUI.MainWindow;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

public class MainClientSecure {
    public static void main(String[] args) {
        Properties properties = new Properties();
        String fileName = "src/config.properties";
        try (InputStream input = new FileInputStream(fileName)) {
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
