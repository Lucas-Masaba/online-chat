import java.io.*;
import java.net.*;
import java.util.*;

public class ChatServer {
  private static final int PORT = 12345;
  private static Set<ClientHandler> clientHandlers = new HashSet<>();
  private static int clientIdCounter = 1;

  public static void main(String[] args) {
    System.out.println("Chat server started...");
    try (ServerSocket serverSocket = new ServerSocket(PORT)) {
      while (true) {
        Socket clientSocket = serverSocket.accept();
        ClientHandler clientHandler = new ClientHandler(clientSocket, clientIdCounter++);
        clientHandlers.add(clientHandler);
        clientHandler.start();
      }
    } catch (IOException e) {
      System.out.println("Error starting the server: " + e.getMessage());
    }
  }

  private static class ClientHandler extends Thread {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private int clientId;

    public ClientHandler(Socket socket, int clientId) {
      this.socket = socket;
      this.clientId = clientId;
    }

    public void run() {
      try {
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);

        String welcomeMessage = "Client " + clientId + " has joined the chat!";
        broadcastMessage(welcomeMessage);

        String message;
        while ((message = in.readLine()) != null) {
          System.out.println("Received from Client " + clientId + ": " + message);
          broadcastMessage("Client " + clientId + ": " + message);
        }
      } catch (IOException e) {
        System.out.println("Error in client handler: " + e.getMessage());
      } finally {
        try {
          socket.close();
        } catch (IOException e) {
          System.out.println("Error closing socket: " + e.getMessage());
        }
        clientHandlers.remove(this);
        broadcastMessage("Client " + clientId + " has left the chat.");
      }
    }

    private void broadcastMessage(String message) {
      synchronized (clientHandlers) {
        for (ClientHandler handler : clientHandlers) {
          handler.out.println(message);
        }
      }
    }
  }
}
