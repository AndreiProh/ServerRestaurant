import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;

public class DatabaseHandler extends Configs {
    Connection dbConnection;
    public Connection getDbConnection()
            throws ClassNotFoundException, SQLException {
        String connectionString = "jdbc:mysql://" + dbHost + ":"
                + dbPort + "/" + dbName;
        Class.forName("com.mysql.jdbc.Driver");
        dbConnection = DriverManager.getConnection(connectionString,
                dbUser, dbPass);
        return dbConnection;
    }
    public int signUpUser(User user){
        int amountUsers = 0;
        int userRegisteredCode = 0;
        try {
            amountUsers = checkUserName(user);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        if (amountUsers == 0) {
            String insert = "INSERT INTO " + Const.USER_TABLE + "(" +
                    Const.USER_NAME + "," + Const.USER_PASSWORD + "," +
                    Const.USER_LAST_NAME + "," + Const.USER_FIRST_NAME + ")" +
                    "VALUES(?,?,?,?)";
            System.out.println("in signUp");

            try {
                PreparedStatement prSt = getDbConnection().prepareStatement(insert);
                prSt.setString(1, user.getUserName());
                prSt.setString(2, user.getPassword());
                prSt.setString(3, user.getLastName());
                prSt.setString(4, user.getFirstName());
                prSt.executeUpdate();
                userRegisteredCode = 1;
            } catch (SQLException e) {
                throw new RuntimeException(e);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        } else
            userRegisteredCode = 2;
        return userRegisteredCode;
    }
    public User getUser(User user) {
        ResultSet resSet = null;
        String select = "SELECT * FROM " + Const.USER_TABLE +
                " JOIN user_status on user.id_user_status = user_status.id_user_status  " +
                "WHERE " +
                Const.USER_NAME + "=? AND " + Const.USER_PASSWORD + "=?";

        try {
            PreparedStatement prSt = getDbConnection().prepareStatement(select);
            prSt.setString(1, user.getUserName());
            prSt.setString(2, user.getPassword());

            resSet = prSt.executeQuery();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
            try {
                resSet.next();
                user.setId(resSet.getInt(1));
                user.setLastName(resSet.getString(Const.USER_LAST_NAME));
                user.setFirstName(resSet.getString(Const.USER_FIRST_NAME));
                user.setUserStatus(resSet.getString("name_status"));
            } catch (SQLException e) {
                user.setId(0);
            }

        return user;

    }

    public Dish getDish(int id) {
        ResultSet resSet = null;
        String select = "SELECT * FROM " + Const.DISH_TABLE + " WHERE " +
                Const.DISH_ID + " =?";
        try {
            PreparedStatement prSt = getDbConnection().prepareStatement(select);
            prSt.setString(1, Integer.toString(id));
            resSet = prSt.executeQuery();
            resSet.next();
            System.out.println(resSet.getString(2));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        try {
            Dish dish = new Dish(id, resSet.getString("name_dish"),
                    resSet.getInt("id_category"), resSet.getDouble("price") );
            return dish;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public Dish getDish(String name) {
        ResultSet resSet = null;
        String select = "SELECT * FROM " + Const.DISH_TABLE + " WHERE " +
                Const.DISH_NAME + " =?";
        try {
            PreparedStatement prSt = getDbConnection().prepareStatement(select);
            prSt.setString(1, name);
            resSet = prSt.executeQuery();
            resSet.next();
            System.out.println(resSet.getString(2));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        try {
            Dish dish = new Dish(resSet.getInt("id_dish"), resSet.getString("name_dish"),
                    resSet.getInt("id_category"), resSet.getDouble("price") );
            return dish;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public int addBuy(Buy buy) {
        int status = 0;
        System.out.println(buy.getOrderDish().isEmpty());
        if (!buy.getOrderDish().isEmpty()) {
            ResultSet resultSet = null;
            String insert = "INSERT INTO " + Const.BUY_TABLE + "(" +
                    Const.BUY_DESCRIPTION + " ," + Const.USER_ID + ", status, date " + ")" +
                    "VALUES(?,?,?,NOW())";
            try {
                Connection connection = getDbConnection();
                PreparedStatement prSt = connection.prepareStatement(insert);
                prSt.setString(1, buy.getDescription());
                prSt.setString(2, Integer.toString(buy.getUser().getId()));
                prSt.setString(3,"Готовится");
                prSt.executeUpdate();
                buy.setId(getLastInsertID(connection));
                addOrderDishFromBuy(buy);
                addDelivery(buy.getId());
                addPayment(buy);
                status = 1;
            } catch (SQLException e) {
                status = 2;
            } catch (ClassNotFoundException e) {
                status = 2;
            }

        }
        return status;
    }
    public List<Dish> getListOfDish() throws SQLException {
        List<Dish> list = new ArrayList<>();
        ResultSet resultSet = null;
        String select = "SELECT *FROM " + Const.DISH_TABLE;
        try {
            PreparedStatement prSt = getDbConnection().prepareStatement(select);
            resultSet = prSt.executeQuery();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        while (resultSet.next()) {
            list.add(new Dish(resultSet.getInt("id_dish"), resultSet.getString("name_dish"),
                    resultSet.getInt("id_category"), resultSet.getDouble("price") ));
        }
        return list;
    }

    public List<Delivery> getListOfDelivery() throws SQLException {
        List<Delivery> list = new ArrayList<>();
        ResultSet resultSet = null;
        String select = "SELECT id_delivery, deliveries.id_buy, id_courier, address, status_delivery, start_time_delivery, finish_time_delivery\n" +
                "from deliveries \n" +
                "join buy on deliveries.id_buy = buy.id_buy\n" +
                "join user on buy.id_user = user.id_user;";  // Константа для таблицы доставки
        try {
            PreparedStatement prSt = getDbConnection().prepareStatement(select);
            resultSet = prSt.executeQuery();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        while (resultSet.next()) {
            list.add(new Delivery(
                    resultSet.getInt("id_delivery"),
                    resultSet.getInt("id_buy"),
                    resultSet.getInt("id_courier"),
                    resultSet.getString("address"),
                    resultSet.getString("status_delivery"),
                    new SimpleDateFormat("yyyy-MM-dd:HH:mm:ss").format(resultSet.getTimestamp("start_time_delivery"))
            ));
        }
        return list;
    }

    public List<Payment> getListPayment() throws SQLException {
        List<Payment> list = new ArrayList<>();
        ResultSet resultSet = null;

        String select = "SELECT id_payment, id_buy, amount, payment_method, payment_status, payment_date " +
                        "FROM  payments";

        try {
            PreparedStatement prSt = getDbConnection().prepareStatement(select);
            resultSet = prSt.executeQuery();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        while (resultSet.next()) {
            list.add(new Payment(
                    resultSet.getInt("id_payment"),
                    resultSet.getInt("id_buy"),
                    resultSet.getDouble("amount"),
                    resultSet.getString("payment_method"),
                    resultSet.getString("payment_status"),
                    new SimpleDateFormat("yyyy-MM-dd:HH:mm:ss").format(resultSet.getTimestamp("payment_date"))
            ));
        }

        return list;
    }

    public List<OrderDTO> getListOfOrderDTO() throws SQLException {
        List<OrderDTO> orderList = new ArrayList<>();
        ResultSet buyResultSet = null;
        ResultSet orderDishResultSet = null;

        // SQL запрос для получения информации о заказах из таблицы buy
        String selectBuyQuery = "SELECT id_buy, buy_description, buy_start_time FROM buy";

        try {
            // Получение данных из таблицы buy
            PreparedStatement buyStatement = getDbConnection().prepareStatement(selectBuyQuery);
            buyResultSet = buyStatement.executeQuery();

            while (buyResultSet.next()) {
                int idBuy = buyResultSet.getInt("id_buy");
                String orderNotes = buyResultSet.getString("buy_description");
                String orderDateTime = buyResultSet.getTimestamp("buy_start_time").toString();

                // Создаем пустую Map для блюд и их количества
                Map<String, Integer> dishes = new HashMap<>();

                // SQL запрос для получения информации о блюдах для конкретного заказа
                String selectOrderDishQuery = "SELECT d.name_dish, od.amount FROM order_dish od "
                        + "JOIN dish d ON od.id_dish = d.id_dish WHERE od.id_buy = ?";

                // Получение данных из таблицы order_dish по id_buy
                PreparedStatement orderDishStatement = getDbConnection().prepareStatement(selectOrderDishQuery);
                orderDishStatement.setInt(1, idBuy);
                orderDishResultSet = orderDishStatement.executeQuery();

                // Заполняем Map блюдами и их количеством
                while (orderDishResultSet.next()) {
                    String dishName = orderDishResultSet.getString("name_dish");
                    int quantity = orderDishResultSet.getInt("amount");
                    dishes.put(dishName, quantity);
                }

                // Создаем объект OrderDTO и добавляем его в список
                OrderDTO order = new OrderDTO(idBuy, dishes, orderNotes, orderDateTime);
                orderList.add(order);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error while fetching order data", e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Database connection error", e);
        } finally {
            // Закрываем resultSetы и statementы после использования
            if (buyResultSet != null) {
                buyResultSet.close();
            }
            if (orderDishResultSet != null) {
                orderDishResultSet.close();
            }
        }
        return orderList;
    }
    //Проверяем есть ли пользователь с таким логином.
    // Возвращает 0 если нет(количество пользователей с таким Логином)
    private int checkUserName(User user) throws SQLException {
        ResultSet result = null;
        String insert = "SELECT *FROM " + Const.USER_TABLE +  " WHERE " + Const.USER_NAME + " =?";
        System.out.println("in check");

        try {
            PreparedStatement prSt = getDbConnection().prepareStatement(insert);
            prSt.setString(1,user.getUserName());

           result =  prSt.executeQuery();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        int count = 0;
        while (result.next()) {
            count++;
        }
        return count;
    }
    private void addOrderDishFromBuy(Buy buy) {
        String insert;
        for (OrderDish od: buy.getOrderDish()) {
            insert = "INSERT INTO " + Const.ORDER_DISH_TABLE +"(" +
                    Const.BUY_ID + ", " + Const.DISH_ID + ", " + Const.ORDER_AMOUNT + ")" +
                    "VALUES(?,?,?)";
            try {
                PreparedStatement prSt = getDbConnection().prepareStatement(insert);
                prSt.setString(1, Integer.toString(buy.getId()));
                prSt.setString(2, Integer.toString(od.getDish().getId()));
                prSt.setString(3, Integer.toString(od.getQuantity()));
                prSt.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void addDelivery(int id_buy) {
        System.out.println("IN DELIVERY");
        String insert = "INSERT INTO deliveries(id_buy, id_courier, status_delivery, time_delivery)" +
                " VALUES(?,?,?,NOW())";
        int id_courier = new Random().nextInt(3) + 1;
        try {
            PreparedStatement prSt = getDbConnection().prepareStatement(insert);
            prSt.setString(1, Integer.toString(id_buy));
            prSt.setString(2, Integer.toString(id_courier));
            prSt.setString(3, "Назначен");
            prSt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

    }

    private void addPayment(Buy buy) {
        System.out.println("IN PAYMENT");
        String insert = "INSERT INTO payments(id_buy, payment_date, amount, payment_method)" +
                " VALUES(?,NOW(),?,?)";
        try {
            PreparedStatement prSt = getDbConnection().prepareStatement(insert);
            prSt.setString(1, Integer.toString(buy.getId()));
            prSt.setString(2, Double.toString(buy.getTotalOrderPrice()));
            prSt.setString(3, "Карта");
            prSt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    private int getLastInsertID(Connection connection) {
        ResultSet resultSet = null;
        String select = "SELECT LAST_INSERT_ID()";
        try {
            PreparedStatement prSt = connection.prepareStatement(select);
            resultSet = prSt.executeQuery();
            resultSet.next();
            return resultSet.getInt(1);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
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

}
