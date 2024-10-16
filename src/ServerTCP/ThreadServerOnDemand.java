package ServerTCP;

import Logger.Logger;
import Protocol.Protocol;

import java.io.IOException;
import java.net.*;

public class ThreadServerOnDemand extends ThreadServer
{
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
