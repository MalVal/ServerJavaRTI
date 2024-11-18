package Client.Client.Network;

import Common.Network.Request.*;
import Common.Network.Response.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Client {

    private final Socket socket;
    private final ObjectInputStream inputStream;
    private final ObjectOutputStream outputStream;

    public Client(String ipAddress, Integer port) throws Exception {
        try {
            socket = new Socket(ipAddress, port);
            outputStream = new ObjectOutputStream(socket.getOutputStream());
            inputStream = new ObjectInputStream(socket.getInputStream());
        }
        catch (IOException e) {
            throw new Exception(e.getMessage());
        }
    }

    public Response send(Request request) throws IOException, ClassNotFoundException {
        outputStream.writeObject(request);
        return (Response) inputStream.readObject();
    }

    public void close() throws IOException {
        outputStream.close();
        inputStream.close();
        socket.close();
    }
}
