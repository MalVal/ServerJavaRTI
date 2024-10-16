package Protocol;

import Request.Request;
import Response.Response;
import Exception.ConnectionEndException;

import java.net.Socket;

public interface Protocol
{
    String getName();
    Response processRequest(Request request, Socket socket) throws ConnectionEndException;
}