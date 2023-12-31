import java.io.Serializable;

public class AuctionResult implements Serializable {
    private String winningEmail;
    private int winningPrice;

    public AuctionResult(String winningEmail, int winningPrice) {
        this.winningEmail = winningEmail;
        this.winningPrice = winningPrice;
    }

    public String getWinningEmail() {
        return winningEmail;
    }

    public int getWinningPrice() {
        return winningPrice;
    }
}
