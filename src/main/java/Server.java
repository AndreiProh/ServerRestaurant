
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class Server {
    //Поля класса
    private int port;
    private static Map<String, ClientHandler> connectedClients = new ConcurrentHashMap<>();
    //Конструктор класса
    public Server(int port) {
        this.port = port;
    }
    //Метод для запуска сервера:
    public void start() {
        //Создаем сокет сервера на указанном порту и ждем подключения клиента
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server is listening on port " + port);
            while (true) {
                //При появлении подключения создаем отдельный сокет
                //для общения с клиентом и передаём его в класс ClientHandler
                //для взаимодействия в отдельном потоке
                Socket socket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(socket);
                clientHandler.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class ClientHandler extends Thread {
        /*Поля класса необходимые для взаимодействия с другими
         классами приложения*/
        private Socket socket;
        private OutputStream outputStream;
        private PrintWriter writer;
        private  BufferedReader reader;
        private User user;
        private Buy buy;
        //Конструктор:
        public ClientHandler(Socket socket) {
            this.user = new User();
            this.socket = socket;
        }
        @Override
        public void run() {
            try {
                //Буфер чтения из сокета:
                reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                outputStream = socket.getOutputStream();
                //Буфер записи в сокет:
                writer = new PrintWriter(outputStream, true);
                //Строка с принятым сообщением:
                String clientMessage;
                while ((clientMessage = reader.readLine()) != null) {
                    System.out.println("Received from client: " + clientMessage);
                    messageHandler(clientMessage);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        /*Обработчик сообщений. Принимает на вход строку сообщения
        и в зависимости от содержания вызывает методы для дальнейших действий:*/
        private void messageHandler(String clientMessage) {
            //Преобразуем строку в объект JSON для удобства извлечения данных:
            JsonObject jsonMessage = new Gson().fromJson(clientMessage, JsonObject.class);
            //Извлекаем тип полученного сообщения:
            String typeOfMessage = jsonMessage.get("type").getAsString();
            //Вызываем метод для обработки сообщения:
            switch (typeOfMessage) {
                case Const.LOG_UP:
                    handleLogUpMessage(jsonMessage);
                    break;
                case Const.AUTHORIZATION:
                    handleAuthorizationMessage(jsonMessage);
                    break;
                case Const.ORDER:
                    handleOrderMessage(jsonMessage);
                    break;
                case "exit":
                    handleExitMessage(jsonMessage);
                    break;
                case "update":
                    handleUpdateMessage(jsonMessage);
                    break;
                case "admin" :
                    handleAdminMessage(jsonMessage);
                    break;
                case "delivery" :
                    handleCourierMessage(jsonMessage);
                    break;
                default:
                    System.out.println("Unknown message type: " + typeOfMessage);
            }
        }

        private void handleCourierMessage(JsonObject jsonMessage) {
            DatabaseHandler db = new DatabaseHandler();
            int executeStatus = db.changeStatusDelivery(jsonMessage.get("id").getAsInt(),
                    jsonMessage.get("delivery_status").getAsString());
            jsonMessage.addProperty("status", executeStatus);
            sendMessage(jsonMessage);
        }

        private void handleUpdateMessage(JsonObject jsonMessage) {
            DatabaseHandler db = new DatabaseHandler();
            try {
                JsonArray jsonArray = new Gson().toJsonTree(db.getListOfDish()).getAsJsonArray();
                jsonMessage.add("dishes",jsonArray);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            sendMessage(jsonMessage);
        }

        private void handleExitMessage(JsonObject jsonMessage) {
            user = new User();
            buy = new Buy(user);
            System.out.println(user.getId() +user.getUserName() + user.isAuthorized());
            System.out.println(buy.getId() + buy.toString());
            sendMessage(jsonMessage);
        }

        private void handleOrderMessage(JsonObject jsonMessage) {
            DatabaseHandler db = new DatabaseHandler();
            if (buy != null) {
                System.out.println("IN CONFIRM");
                Map<String, Number> mapOrder = new HashMap<>();
                JsonElement mapElement = jsonMessage.get("dishes");
                mapOrder = new Gson().fromJson(mapElement, HashMap.class);
                for (Map.Entry<String, Number> entry : mapOrder.entrySet()) {
                    for (int i = 0; i < entry.getValue().intValue(); i++) {
                        buy.addDishToOrder(db.getDish(entry.getKey()));
                    }

                }
                int statusOfOrder = db.addBuy(buy);
                jsonMessage.addProperty("status", statusOfOrder);

            } else
                jsonMessage.addProperty("status", 3);
            sendMessage(jsonMessage);
        }

        private void handleAuthorizationMessage(JsonObject jsonMessage) {
            user.setUserName(jsonMessage.get("username").getAsString());
            user.setPassword(hashPassword(jsonMessage.get("password").getAsString()));

            DatabaseHandler db = new DatabaseHandler();
            user = db.getUser(user);

            if (user.getId() != 0){
                if (user.getUserStatus().equals("client")) {
                    jsonMessage.addProperty(Const.STATUS, 1);
                    user.setAuthorized(true);
                    buy = new Buy(user);
                    connectedClients.put(user.getUserName(), this);
                    System.out.println(user.getId());
                }
                if (user.getUserStatus().equals("admin")) {
                    jsonMessage.addProperty(Const.STATUS, 2);
                    try {
                        JsonArray jsonArray = new Gson().toJsonTree(db.getListOfOrderDTO()).getAsJsonArray();
                        jsonMessage.add("orders",jsonArray);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
                if (user.getUserStatus().equals("courier")) {
                    jsonMessage.addProperty(Const.STATUS, 3);
                    try {
                        JsonArray jsonArray = new Gson().toJsonTree(db.getListOfDelivery()).getAsJsonArray();
                        jsonMessage.add("deliveries",jsonArray);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
            }else
                jsonMessage.addProperty(Const.STATUS, 0);
            sendMessage(jsonMessage);

        }

        private void handleLogUpMessage(JsonObject jsonMessage) {
            user.setUserName(jsonMessage.get("username").getAsString());
            user.setLastName(jsonMessage.get("lastname").getAsString());
            user.setFirstName(jsonMessage.get("firstname").getAsString());
            user.setPassword(hashPassword(jsonMessage.get("password").getAsString()));
            System.out.println(user.getUserName()+user.getFirstName()+user.getLastName()+user.getPassword());
            DatabaseHandler dbHandler = new DatabaseHandler();
            int userRegisterdCode = dbHandler.signUpUser(user);
            jsonMessage.addProperty(Const.STATUS, userRegisterdCode);
            user = new User();
            sendMessage(jsonMessage);
        }

        private void handleAdminMessage(JsonObject jsonMessage) {
            DatabaseHandler db = new DatabaseHandler();
            int executeStatus = db.confirmReadyOrder(jsonMessage.get("id").getAsInt());
            jsonMessage.addProperty("status", executeStatus);
            sendMessage(jsonMessage);
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
        private String hashPassword(String password) {
            return String.valueOf(password.hashCode());
        }


        private void sendMessage(JsonObject jsonObject) {
            writer.println(new Gson().toJson(jsonObject));
        }

    }
}
