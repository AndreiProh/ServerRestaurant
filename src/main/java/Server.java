
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.ResultSet;
import java.sql.SQLException;


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
        private OutputStream outputStream;
        private PrintWriter writer;
        private  BufferedReader reader;
        private User user;
        private Buy buy;

        public ClientHandler(Socket socket) {
            this.user = new User();
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                outputStream = socket.getOutputStream();
                writer = new PrintWriter(outputStream, true);

                String clientMessage;
                while ((clientMessage = reader.readLine()) != null) {
                    System.out.println("Received from client: " + clientMessage);
                    messageHandler(clientMessage);
                    //writer.println("Echo: " + clientMessage);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void messageHandler(String clientMessage) {
            System.out.println(clientMessage);
            JsonObject jsonMessage = new Gson().fromJson(clientMessage, JsonObject.class);
            System.out.println(jsonMessage.get("type").getAsString());
            String typeOfMessage = jsonMessage.get("type").getAsString();
            if (typeOfMessage.equals(Const.LOG_UP)) {
                System.out.println("in IF");
               // User user = new User();
                user.setUserName(jsonMessage.get("username").getAsString());
                user.setLastName(jsonMessage.get("lastname").getAsString());
                user.setFirstName(jsonMessage.get("firstname").getAsString());
                user.setPassword(jsonMessage.get("password").getAsString());
                System.out.println(user.getUserName()+user.getFirstName()+user.getLastName()+user.getPassword());
                DatabaseHandler dbHandler = new DatabaseHandler();
                int userRegisterdCode = dbHandler.signUpUser(user);
                jsonMessage.addProperty(Const.STATUS, userRegisterdCode);
                sendMessage(jsonMessage);
            }

            if (typeOfMessage.equals(Const.AUTHORIZATION)) {
                System.out.println("in IF authoriz");

                user.setUserName(jsonMessage.get("username").getAsString());
                user.setPassword(jsonMessage.get("password").getAsString());

                DatabaseHandler db = new DatabaseHandler();
                user = db.getUser(user);

                if (user.getId() != 0){
                    jsonMessage.addProperty(Const.STATUS, 1);
                    user.setAuthorized(true);
                    buy = new Buy(user);
                    System.out.println(user.getId());
                }else
                    jsonMessage.addProperty(Const.STATUS, 0);
                sendMessage(jsonMessage);
            }

            if (typeOfMessage.equals(Const.ORDER)) {
                System.out.println("IN ORDER");
                DatabaseHandler db = new DatabaseHandler();
                if (jsonMessage.get(Const.STATUS).getAsString().equals(Const.ADD)) {
                    buy.addDishToOrder(db.getDish(jsonMessage.get(Const.ID_DISH).getAsInt()));
                    System.out.println("IN ADD");
                }
                if (jsonMessage.get(Const.STATUS).getAsString().equals(Const.SUBTRACT)) {
                    buy.subDishFromOrder(jsonMessage.get(Const.ID_DISH).getAsInt());
                    System.out.println("IN SUB");
                }
                if (jsonMessage.get(Const.STATUS).getAsString().equals(Const.CONFIRM)) {
                    System.out.println("IN CONFIRM");
                    db.addBuy(buy);
                }


            }


            System.out.println("in mesageHand");




        }

        private int checkResultSet(ResultSet resultSet){
            int count = 0;
            try {
                while(resultSet.next()) {
                       count++;
                }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            return count;
        }



        private void sendMessage(JsonObject jsonObject) {
            writer.println(new Gson().toJson(jsonObject));
        }

    }
}
