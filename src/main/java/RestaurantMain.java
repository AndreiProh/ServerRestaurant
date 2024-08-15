import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RestaurantMain {
    public static void main(String[] args) {
        int port = 3345;
//     DatabaseHandler db = new DatabaseHandler();


//       Dish dish = db.getDish(2);
//       System.out.println(dish.getPrice());
//        System.out.println(db.getDish(3));
//        User user = new User("Andrei", "12345");
//        System.out.println(db.getUser(user).getId());
//        user.setId(1);
//        Buy order = new Buy(user);
//        order.setDescription("Побыстрее");
//        order.addDishToOrder(db.getDish(2));
//        order.addDishToOrder(db.getDish(2));
//        order.addDishToOrder(db.getDish(3));
//        order.subDishFromOrder(2);
//        order.subDishFromOrder(2);
//
//
//        System.out.println(order);
//        db.addBuy(order);
        Server server = new Server(port);
        server.start();

    }
}
