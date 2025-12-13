package server;

public class BouquetComposition {
    private int id;
    private int bouquetId;
    private int flowerId;
    private int flowerCount;

    // Конструкторы
    public BouquetComposition() {}

    public BouquetComposition(int id, int bouquetId, int flowerId, int flowerCount) {
        this.id = id;
        this.bouquetId = bouquetId;
        this.flowerId = flowerId;
        this.flowerCount = flowerCount;
    }

    // Геттеры и сеттеры
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getBouquetId() { return bouquetId; }
    public void setBouquetId(int bouquetId) { this.bouquetId = bouquetId; }

    public int getFlowerId() { return flowerId; }
    public void setFlowerId(int flowerId) { this.flowerId = flowerId; }

    public int getFlowerCount() { return flowerCount; }
    public void setFlowerCount(int flowerCount) { this.flowerCount = flowerCount; }

    @Override
    public String toString() {
        return id + " | bouquet=" + bouquetId + " | flower=" + flowerId + " | count=" + flowerCount;
    }
}
