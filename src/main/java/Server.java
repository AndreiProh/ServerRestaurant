
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;



public class Server {
    private int port;

    public Server(int port) {
        this.port = port;
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server is listening on port " + port);
            while (true) {
                Socket socket = serverSocket.accept();
                new ClientHandler(socket).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class ClientHandler extends Thread {
        private Socket socket;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                 OutputStream outputStream = socket.getOutputStream();
                 PrintWriter writer = new PrintWriter(outputStream, true)) {

                String clientMessage;
                while ((clientMessage = reader.readLine()) != null) {
                    System.out.println("Received from client: " + clientMessage);
                    messageHandler(clientMessage);
                    writer.println("Echo: " + clientMessage);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void messageHandler(String clientMessage) {


        }
    }
}
