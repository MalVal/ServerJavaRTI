package Server.Network.Protocol;

import Common.Network.Response.Response;
import Common.Network.Request.Request;
import Server.Exception.ConnectionEndException;

import java.net.Socket;

public interface Protocol
{
    String getName();
    Response processRequest(Request request, Socket socket) throws ConnectionEndException;
}