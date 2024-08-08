import java.io.*;
import java.net.*;

public class ChatClient {
  private static final String SERVER_ADDRESS = "localhost";
  private static final int SERVER_PORT = 12345;

  public static void main(String[] args) {
    try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        BufferedReader console = new BufferedReader(new InputStreamReader(System.in))) {

      // Start a thread to listen for messages from the server
      new Thread(() -> {
        try {
          String message;
          while ((message = in.readLine()) != null) {
            System.out.println(message);
          }
        } catch (IOException e) {
          System.out.println("Error reading from server: " + e.getMessage());
        }
      }).start();

      // Read messages from the console and send them to the server
      String message;
      while ((message = console.readLine()) != null) {
        out.println(message);
      }

    } catch (IOException e) {
      System.out.println("Error connecting to the server: " + e.getMessage());
    }
  }
}
