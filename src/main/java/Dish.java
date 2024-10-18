public class Dish {
    private int id;
    private String name;
    private String description;
    private int category;
    private double price;

    public Dish(int id, String name, int category, double price, String description) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.price = price;
        this.description = description;
    }
    public Dish() {
        this.id = 0;
        this.name = null;
        this.category = 0;
        this.price = 0;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String toString() {
        return "Блюдо: " + name +  ", категории " + category + ", стоимостью " + price + " руб";
    }
}
