package server;

public class Administrator {
    private int id;
    private String fullName;
    private String phone;
    private String login;
    private String password;

    // Конструкторы
    public Administrator() {}

    public Administrator(int id, String fullName, String phone, String login, String password) {
        this.id = id;
        this.fullName = fullName;
        this.phone = phone;
        this.login = login;
        this.password = password;
    }

    // Геттеры и сеттеры
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    @Override
    public String toString() {
        return id + " | " + fullName + " | " + phone + " | " + login;
    }
}
