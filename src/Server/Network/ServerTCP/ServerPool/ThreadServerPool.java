package Server.Network.ServerTCP.ServerPool;

import Server.Logger.Logger;

import Server.Network.Protocol.Protocol;
import Server.Network.ServerTCP.ThreadServer;


import java.io.IOException;

import java.net.*;


public class ThreadServerPool extends ThreadServer
{
    private final Queue connectionsPending;
    private final ThreadGroup pool;
    private final int poolSize;
    public ThreadServerPool(int port, Protocol protocol, int poolSize, Logger logger) throws IOException
    {
        super(port, protocol, logger);
        connectionsPending = new Queue();
        pool = new ThreadGroup("POOL");
        this.poolSize = poolSize;
    }

    @Override
    public void run()
    {
        logger.trace("Starting of the server (Pool)...");

        try
        {
            for (int i = 0; i< poolSize; i++) {
                new ThreadClientPool(protocol,connectionsPending,pool,logger).start();
            }
        }
        catch (IOException ex)
        {
            logger.trace("Error when creating the pool of threads");
            return;
        }

        while(!this.isInterrupted())
        {
            Socket csocket;
            try
            {
                this.serverSocket.setSoTimeout(2000);
                csocket = this.serverSocket.accept();
                logger.trace("Connection accepted");

                connectionsPending.addConnexion(csocket);
            }
            catch (SocketTimeoutException ex)
            {
                logger.trace("Server (pool) was interrupted.");
            }
            catch (IOException ex)
            {
                logger.trace("Error I/O");
            }
        }
        logger.trace("Server (Pool) interrupted.");
        pool.interrupt();
    }
}