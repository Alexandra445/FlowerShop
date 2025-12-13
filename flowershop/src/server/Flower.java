package server;

import java.math.BigDecimal;

public class Flower {
    private int id;
    private String name;
    private int typeId;
    private int quantity;
    private BigDecimal price;

    public Flower(int id, String name, int typeId, int quantity, BigDecimal price) {
        this.id = id;
        this.name = name;
        this.typeId = typeId;
        this.quantity = quantity;
        this.price = price;
    }

    // Геттеры и сеттеры
    public int getId() { return id; }
    public String getName() { return name; }
    public int getTypeId() { return typeId; }
    public int getQuantity() { return quantity; }
    public BigDecimal getPrice() { return price; }

    public void setQuantity(int quantity) { this.quantity = quantity; }

    public String getTypeName() {
        return new FlowerTypeService().getTypeName(typeId);
    }
}
