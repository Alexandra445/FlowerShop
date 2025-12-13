package server;

public class FlowerType {
    private int id;
    private String name;

    // Конструкторы
    public FlowerType() {}

    public FlowerType(int id, String name) {
        this.id = id;
        this.name = name;
    }

    // Геттеры/сеттеры
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    @Override
    public String toString() {
        return id + " | " + name;
    }
}
