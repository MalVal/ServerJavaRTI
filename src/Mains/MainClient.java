package Mains;

import Client.ClientStandard.ClientControllerStandard;
import Client.GUI.MainWindow;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

public class MainClient {
    public static void main(String[] args) {
        Properties properties = new Properties();
        String fileName = "src/config.properties";

        try (InputStream input = new FileInputStream(fileName)) {
            properties.load(input);
            String addressIp = properties.getProperty("serv.address");
            String portPayment = properties.getProperty("serv.portPaymentSecure");

            new ClientControllerStandard(addressIp, Integer.parseInt(portPayment), new MainWindow());
        }
        catch (Exception e) {
            System.out.println("Error :" + e.getMessage());
        }
    }
}
