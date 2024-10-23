package Server.Network.Protocol;

import Network.Response.Response;
import Network.Request.Request;
import Server.Exception.ConnectionEndException;

import java.net.Socket;

public interface Protocol
{
    String getName();
    Response processRequest(Request request, Socket socket) throws ConnectionEndException;
}