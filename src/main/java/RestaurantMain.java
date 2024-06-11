public class RestaurantMain {
    public static void main(String[] args) {
        int port = 3345;
        Server server = new Server(port);
        server.start();
    }
}
