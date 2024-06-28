public class User {
    private int id;
    private  String userName;
    private  String lastName;
    private  String firstName;
    private String password;
    private boolean authorized;

    public User(String userName, String lastName, String firstName, String password) {
        this.userName = userName;
        this.lastName = lastName;
        this.firstName = firstName;
        this.password = password;
        this.authorized = false;
    }

    public User() {
        this.id = 0;
        this.userName = null;
        this.lastName = null;
        this.firstName = null;
        this.password = null;
        this.authorized = false;
    }

    public User(String userName, String password) {
        this.id = 0;
        this.userName = userName;
        this.lastName = null;
        this.firstName = null;
        this.password = password;
        this.authorized = false;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isAuthorized() {
        return authorized;
    }

    public void setAuthorized(boolean authorized) {
        this.authorized = authorized;
    }
}
