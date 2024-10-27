package Server.Network.Protocol;

import Server.Network.Response.Response;
import Server.Network.Request.Request;
import Server.Exception.ConnectionEndException;

import java.net.Socket;

public interface Protocol
{
    String getName();
    Response processRequest(Request request, Socket socket) throws ConnectionEndException;
}