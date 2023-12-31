import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AuctionItem implements Serializable {

    private static int nextItemID = 1;

    private int itemID;
    private String name;
    private String description;
    private int reservePrice;
    private int highestBid;
    private boolean closed;

    private static List<AuctionItem> auctionItems = new ArrayList<>();

    public AuctionItem(int itemId, String name, String description) {
        this.itemID = nextItemID++;
        this.name = name;
        this.description = description;
        this.highestBid = reservePrice; 
        this.closed = false;
        auctionItems.add(this);
    }
    public AuctionItem(int itemId, String name, String description,int reservePrice) {
        this.itemID = nextItemID++;
        this.name = name;
        this.reservePrice = reservePrice;
        this.description = description;
        this.highestBid = reservePrice;
        this.closed = false;
        auctionItems.add(this);
    }
    public AuctionItem(String name, String description, int reservePrice) {
        this.itemID = nextItemID++;
        this.name = name;
        this.description = description;
        this.reservePrice = reservePrice;
        this.highestBid = reservePrice;
        this.closed = false;
        auctionItems.add(this);
    }

    public static AuctionItem createAuctionItem(String name, String description, int reservePrice) {
        return new AuctionItem(name, description, reservePrice);
    }

    public static List<AuctionItem> listItems() {
        return new ArrayList<>(auctionItems);
    }

    public int getItemID() {
        return itemID;
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

    public int getHighestBid() {
        return highestBid;
    }

    public boolean isClosed() {
        return closed;
    }

    public void setHighestBid(int highestBid) {
        this.highestBid = highestBid;
    }

    public void setClosed(boolean closed) {
        this.closed = closed;
    }
}
