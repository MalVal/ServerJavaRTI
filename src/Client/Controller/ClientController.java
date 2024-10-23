package Client.Controller;

import Client.Network.Client;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

public class ClientController {

    public static void main(String[] args) {
        Properties properties = new Properties();
        String fileName = "src/config.properties";

        try (InputStream input = new FileInputStream(fileName)) {
            properties.load(input);
            String addressIp = properties.getProperty("serv.address");
            String portPayment = properties.getProperty("serv.portPayment");

            new ClientController(addressIp, Integer.parseInt(portPayment));
        }
        catch (Exception e) {
            System.out.println("Error :" + e.getMessage());
        }
    }

    private Client clientNetwork;

    public ClientController(String ipServer, Integer portServer) {
        try {
            clientNetwork = new Client(ipServer, portServer);
        }
        catch (Exception e) {
            System.out.println("Connection is impossible :" + e.getMessage());
        }
    }
}
