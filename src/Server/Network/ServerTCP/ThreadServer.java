package Server.Network.ServerTCP;

import Server.Logger.Logger;
import Server.Network.Protocol.Protocol;

import java.io.IOException;
import java.net.ServerSocket;
public abstract class ThreadServer extends Thread
{
    protected int port;
    protected Protocol protocol;
    protected Logger logger;

    protected ServerSocket serverSocket;

    public ThreadServer(int port, Protocol protocol, Logger logger) throws IOException
    {
        super("TH Server (port=" + port + ",protocol=" + protocol.getName() + ")");
        this.port = port;
        this.protocol = protocol;
        this.logger = logger;
        serverSocket = new ServerSocket(port);
    }
}