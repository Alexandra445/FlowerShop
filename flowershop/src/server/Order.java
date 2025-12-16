package server;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class Order {

    private int id;
    private int clientId;
    private Integer bouquetId;
    private Integer flowerId;

    private int bouquetCount;
    private int flowerCount;

    private int statusId;
    private Integer administratorId;

    private BigDecimal totalPrice;
    private Timestamp orderDate;
    private Timestamp deliveryTime;

    // Для отображения
    private String bouquetName;
    private String flowerName;
    private String statusName;
    private String clientName;
    private String clientPhone;

    public Order(
            int id,
            int clientId,
            Integer bouquetId,
            Integer flowerId,
            int bouquetCount,
            int flowerCount,
            int statusId,
            Integer administratorId,
            BigDecimal totalPrice,
            Timestamp orderDate,
            Timestamp deliveryTime,
            String bouquetName,
            String flowerName,
            String statusName,
            String clientName,
            String clientPhone
    ) {
        this.id = id;
        this.clientId = clientId;
        this.bouquetId = bouquetId;
        this.flowerId = flowerId;
        this.bouquetCount = bouquetCount;
        this.flowerCount = flowerCount;
        this.statusId = statusId;
        this.administratorId = administratorId;
        this.totalPrice = totalPrice;
        this.orderDate = orderDate;
        this.deliveryTime = deliveryTime;
        this.bouquetName = bouquetName;
        this.flowerName = flowerName;
        this.statusName = statusName;
        this.clientName = clientName;
        this.clientPhone = clientPhone;
    }

    // Геттеры
    public int getId() { return id; }
    public int getClientId() { return clientId; }
    public Integer getBouquetId() { return bouquetId; }
    public Integer getFlowerId() { return flowerId; }
    public int getBouquetCount() { return bouquetCount; }
    public int getFlowerCount() { return flowerCount; }
    public int getStatusId() { return statusId; }
    public Integer getAdministratorId() { return administratorId; }
    public BigDecimal getTotalPrice() { return totalPrice; }
    public Timestamp getOrderDate() { return orderDate; }
    public Timestamp getDeliveryTime() { return deliveryTime; }
    public String getBouquetName() { return bouquetName; }
    public String getFlowerName() { return flowerName; }
    public String getStatusName() { return statusName; }
    public String getClientName() { return clientName; }
    public String getClientPhone() { return clientPhone; }
}
