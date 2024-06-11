public class User {
    private  String userName;
    private  String lastName;
    private  String firstName;
    private String password;

    public User(String userName, String lastName, String firstName, String password) {
        this.userName = userName;
        this.lastName = lastName;
        this.firstName = firstName;
        this.password = password;
    }

    public User() {
        this.userName = null;
        this.lastName = null;
        this.firstName = null;
        this.password = null;
    }

    public User(String userName, String password) {
        this.userName = userName;
        this.lastName = null;
        this.firstName = null;
        this.password = password;
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
}
