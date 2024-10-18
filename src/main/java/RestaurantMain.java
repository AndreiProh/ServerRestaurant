import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RestaurantMain {
    public static void main(String[] args) {
        int port = 3345;// Задаём номер порта на котором будет работать сервер
        Server server = new Server(port);//и передаём его экземпляру класса Server
        server.start();// Запускаем сервер
    }
}
