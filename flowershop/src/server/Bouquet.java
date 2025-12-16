package server;

import java.math.BigDecimal;

public class Bouquet {

    private int id;
    private String name;
    private String description;
    private BigDecimal price;

    public Bouquet(int id, String name, String description, BigDecimal price) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
    }

    // getters
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getPrice() {
        return price;
    }
}
