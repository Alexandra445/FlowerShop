package server;

public class OrderStatus {
    private int id;
    private String name;

    // Конструкторы
    public OrderStatus() {}

    public OrderStatus(int id, String name) {
        this.id = id;
        this.name = name;
    }

    // Геттеры и сеттеры
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    @Override
    public String toString() {
        return id + " | " + name;
    }
}
