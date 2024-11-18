package Server.Network.ServerTCP.ServerPool;

import java.net.Socket;
import java.util.LinkedList;

public class Queue {
    private final LinkedList<Socket> queue;

    public Queue() {
        queue = new LinkedList<>();
    }

    public synchronized void addConnexion(Socket socket) {
        queue.addLast(socket);
        notify();
    }

    public synchronized Socket getConnexion() throws InterruptedException {
        while (queue.isEmpty()) wait();
        return queue.remove();
    }
}