import java.io.Serializable;

public class AuctionSaleItem implements Serializable {
    private String name;
    private String description;
    private int reservePrice;

    public AuctionSaleItem(String name, String description, int reservePrice) {
        this.name = name;
        this.description = description;
        this.reservePrice = reservePrice;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getReservePrice() {
        return reservePrice;
    }
}

