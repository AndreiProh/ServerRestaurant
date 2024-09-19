import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RestaurantMain {
    public static void main(String[] args) {
        int port = 3345;
        DatabaseHandler db = new DatabaseHandler();
//        List<OrderDTO> list;
//        try {
//            list = db.getListOfOrderDTO();
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
//        for (OrderDTO dto : list) {
//            System.out.println(dto);
//        }
//        User user = new User("Andrei", "12345");
//        System.out.println(db.getUser(user));

//       Dish dish = db.getDish(2);
//       System.out.println(dish.getPrice());
//        System.out.println(db.getDish(3));
//       User user = new User("Andrei", "12345");
//        System.out.println(db.getUser(user).getId());
//        user.setId(1);
//        Buy order = new Buy(user);
//       order.setDescription("Побыстрее");
//       order.addDishToOrder(db.getDish(5));
            //      order.addDishToOrder(db.getDish(6));
//       order.addDishToOrder(db.getDish(6));
//        order.subDishFromOrder(2);
//        order.subDishFromOrder(2);
//
//
//       System.out.println(order);
//       System.out.println( db.addBuy(order));
            Server server = new Server(port);
        server.start();

    }
}
