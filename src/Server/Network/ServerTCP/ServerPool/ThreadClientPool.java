package Server.Network.ServerTCP.ServerPool;

import Server.Logger.Logger;
import Server.Network.Protocol.Protocol;
import Server.Network.ServerTCP.ThreadClient;

import java.io.IOException;

public class ThreadClientPool extends ThreadClient
{
    private final Queue pendingConnection;

    public ThreadClientPool(Protocol protocol, Queue queue, ThreadGroup group, Logger logger) throws IOException
    {
        super(protocol, group, logger);
        pendingConnection = queue;
    }

    @Override
    public void run()
    {
        logger.trace("Client (Pool) started...");
        boolean interrompu = false;
        while(!interrompu)
        {
            try
            {
                logger.trace("Waiting for connection...");
                this.clientSocket = pendingConnection.getConnexion();
                logger.trace("Connected");
                super.run();
            }
            catch (InterruptedException ex)
            {
                logger.trace("Request of interruption...");
                interrompu = true;
            }
        }
        logger.trace("Client (Pool) is ending.");
    }
}
