public class RestaurantMain {
    public static void main(String[] args) {
        int port = 3345;
        DatabaseHandler db = new DatabaseHandler();
        System.out.println(db.getDish(3));
        User user = new User();
        user.setId(1);
        Buy order = new Buy(user);
        order.setDescription("Побыстрее");
        order.addDishToOrder(db.getDish(3));
        db.addOrder(order);
        Server server = new Server(port);
        server.start();

    }
}
