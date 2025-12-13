package server;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class Order {
    private int id;
    private Integer clientId;
    private Integer flowerId;     // если заказан отдельный цветок
    private Integer bouquetId;    // если заказан букет
    private int statusId;
    private Timestamp orderDate;
    private Timestamp deliveryTime;
    private int flowerCount;
    private int bouquetCount;
    private Integer administratorId;
    private BigDecimal totalPrice;
    private String bouquetName;
    private String statusName;
    private String flowerName;

    public Order(int id, Integer clientId, Integer flowerId, Integer bouquetId, int statusId,
                 Timestamp orderDate, Timestamp deliveryTime, int flowerCount, int bouquetCount,
                 Integer administratorId, BigDecimal totalPrice, String bouquetName, String statusName, String flowerName) {
        this.id = id;
        this.clientId = clientId;
        this.flowerId = flowerId;
        this.bouquetId = bouquetId;
        this.statusId = statusId;
        this.orderDate = orderDate;
        this.deliveryTime = deliveryTime;
        this.flowerCount = flowerCount;
        this.bouquetCount = bouquetCount;
        this.administratorId = administratorId;
        this.totalPrice = totalPrice;
        this.bouquetName = bouquetName;
        this.statusName = statusName;
        this.flowerName = flowerName;
    }

    // Геттеры и сеттеры
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Integer getClientId() { return clientId; }
    public void setClientId(Integer clientId) { this.clientId = clientId; }

    public Integer getFlowerId() { return flowerId; }
    public void setFlowerId(Integer flowerId) { this.flowerId = flowerId; }

    public Integer getBouquetId() { return bouquetId; }
    public void setBouquetId(Integer bouquetId) { this.bouquetId = bouquetId; }

    public int getStatusId() { return statusId; }
    public void setStatusId(int statusId) { this.statusId = statusId; }

    public Timestamp getOrderDate() { return orderDate; }
    public void setOrderDate(Timestamp orderDate) { this.orderDate = orderDate; }

    public Timestamp getDeliveryTime() { return deliveryTime; }
    public void setDeliveryTime(Timestamp deliveryTime) { this.deliveryTime = deliveryTime; }

    public int getFlowerCount() { return flowerCount; }
    public void setFlowerCount(int flowerCount) { this.flowerCount = flowerCount; }

    public int getBouquetCount() { return bouquetCount; }
    public void setBouquetCount(int bouquetCount) { this.bouquetCount = bouquetCount; }

    public Integer getAdministratorId() { return administratorId; }
    public void setAdministratorId(Integer administratorId) { this.administratorId = administratorId; }

    public BigDecimal getTotalPrice() { return totalPrice; }
    public void setTotalPrice(BigDecimal totalPrice) { this.totalPrice = totalPrice; }

    public String getBouquetName() { return bouquetName; }
    public void setBouquetName(String bouquetName) { this.bouquetName = bouquetName; }

    public String getStatusName() { return statusName; }
    public void setStatusName(String statusName) { this.statusName = statusName; }

    public String getFlowerName() { return flowerName; }
    public void setFlowerName(String flowerName) { this.flowerName = flowerName; }
}
