package project;

import org.json.*;
import org.java_websocket.server.WebSocketServer;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import java.net.InetSocketAddress;

public class Server extends WebSocketServer {
    public Server(int port) { 
        super(new InetSocketAddress(port)); 
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        System.out.println("New connection: " + conn.getRemoteSocketAddress());
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        System.out.println("Received: " + message);
        conn.send("Move accepted: " + message);
    }

    @Override
    public void onStart() {
        System.out.println("Starting server");
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        System.out.println("Closed connection: " + conn.getRemoteSocketAddress());
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        System.err.println("Error: " + ex.getMessage());
    }

    public static void main(String[] args) {
        Server server = new Server(3000);
        server.start();
        System.out.println("Server running on localhost:3000");
    }
}
