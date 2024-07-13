import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Buy {
    private int id;
    private User user;
    private String description;
    private List<OrderDish> orderDish;

    public Buy(int id, User user, String decription) {
        this.id = id;
        this.user = user;
        this.description = decription;
        this.orderDish = new ArrayList<OrderDish>();
    }

    public Buy(int id, User user) {
        this.id = id;
        this.user = user;
        this.description = "";
        this.orderDish = new ArrayList<OrderDish>();
    }
    public Buy(User user) {
        this.id = 0;
        this.user = user;
        this.description = "";
        this.orderDish = new ArrayList<OrderDish>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<OrderDish> getOrderDish() {
        return orderDish;
    }

    public void setOrderDish(List<OrderDish> orderDish) {
        this.orderDish = orderDish;
    }

    public String toString() {
        String str = "";
        for (OrderDish od:orderDish) {
            str += od.getDish().getName() + ":" + od.getQuantity() + " ";
        }
        return str;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getTotalOrderPrice() {
        int sum = 0;
        for (OrderDish a:orderDish) {
            sum += a.getTotalPrice();
        }
        return  sum;
    }


    public void addDishToOrder(Dish dish) {
        int check = 0;
        for (OrderDish od:orderDish) {
            if (od.getDish().getId() == dish.getId()) {
                od.setQuantity(od.getQuantity() + 1);
                check++;
            }
        }
        if (check == 0)
            orderDish.add(new OrderDish(dish, 1));
    }

    public void subDishFromOrder(int id) {
        Iterator<OrderDish> iterator = orderDish.iterator();
        while (iterator.hasNext()) {
            OrderDish od = iterator.next();
            if (od.getDish().getId() == id) {
                if (od.getQuantity() > 1) {
                    od.setQuantity(od.getQuantity() - 1);
                } else
                    iterator.remove();
            }
        }

    }




}
