package Server.Network.ServerTCP;

import Server.Logger.Logger;
import Server.Network.Protocol.Protocol;
import Server.Network.Request.Request;
import Server.Network.Response.Response;
import Server.Exception.ConnectionEndException;

import java.io.*;
import java.net.Socket;

public abstract class ThreadClient extends Thread
{
    protected Protocol protocol;
    protected Socket clientSocket;
    protected Logger logger;
    private final int number;

    private static int currentNumber = 1;

    public ThreadClient(Protocol protocol, Socket clientSocket, Logger logger) throws IOException
    {
        super("Client " + currentNumber + " (protocol=" + protocol.getName() + ")");
        this.protocol = protocol;
        this.clientSocket = clientSocket;
        this.logger = logger;
        this.number = currentNumber++;
    }

    @Override
    public void run()
    {
        try
        {
            ObjectOutputStream output = null;
            ObjectInputStream input = null;

            try
            {
                input = new ObjectInputStream(clientSocket.getInputStream());
                output = new ObjectOutputStream(clientSocket.getOutputStream());

                while(true)
                {
                    Request request = (Request) input.readObject();
                    Response response = protocol.processRequest(request, clientSocket);
                    output.writeObject(response);
                }
            }
            catch(ConnectionEndException ex)
            {
                logger.trace("End of connection by the protocol");
                if(ex.getResponse() != null)
                {
                    output.writeObject(ex.getResponse());
                }
            }
        }
        catch (IOException ex)
        {
            logger.trace("Error I/O :" + ex.getMessage());
        }
        catch (ClassNotFoundException ex)
        {
            logger.trace("Error : request invalid");
        }
        finally
        {
            try
            {
                clientSocket.close();
            }
            catch (IOException ex)
            {
                logger.trace("Error when closing socket");
            }
        }
    }
}