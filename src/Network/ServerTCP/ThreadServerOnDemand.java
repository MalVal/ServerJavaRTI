package Network.ServerTCP;

import Model.DataBase.DataBaseConnection;
import Logger.*;
import Network.Protocol.EVPP;
import Network.Protocol.Protocol;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.sql.SQLException;
import java.util.Properties;

public class ThreadServerOnDemand extends ThreadServer
{
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

    public ThreadServerOnDemand(int port, Protocol protocol, Logger logger) throws IOException
    {
        super(port, protocol, logger);
    }

    @Override
    public void run()
    {
        logger.trace("Start of the sever (on demand)...");
        while(!this.isInterrupted())
        {
            try
            {
                Socket clientSocket = new Socket();
                clientSocket.setSoTimeout(2000);
                clientSocket = serverSocket.accept();
                logger.trace("Connection accepted");
                Thread threadClient = new ThreadClientOnDemand(protocol, clientSocket, logger);
                threadClient.start();
            }
            catch (SocketTimeoutException ex)
            {
                logger.trace("Server (on demand) was interrupted.");
            }
            catch (IOException ex)
            {
                logger.trace("Error I/O :" + ex.getMessage());
            }
        }
        try
        {
            serverSocket.close();
        }
        catch (IOException ex)
        {
            logger.trace("Error I/O :" + ex.getMessage());
        }
    }
}
