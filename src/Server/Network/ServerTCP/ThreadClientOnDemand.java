package Server.Network.ServerTCP;

import Server.Logger.Logger;
import Server.Network.Protocol.Protocol;

import java.io.IOException;
import java.net.Socket;

public class ThreadClientOnDemand extends ThreadClient
{
    public ThreadClientOnDemand(Protocol protocol, Socket csocket, Logger logger) throws IOException
    {
        super(protocol, csocket, logger);
    }

    @Override
    public void run()
    {
        logger.trace("Client (on demand) started");
        super.run();
        logger.trace("Client (on demand) stopped");
    }
}
