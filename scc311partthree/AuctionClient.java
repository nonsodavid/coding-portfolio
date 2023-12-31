import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.security.PublicKey;
import java.rmi.RemoteException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.UUID;

public class AuctionClient {
    static String token;
    static PublicKey pubKey;
    static int userID;
    static int nextUserID;
    private static Map<Integer, PublicKey> userPublicKeys = new HashMap<>();
    public static void main(String[] args) {
        token =  generateToken();
        pubKey = userPublicKeys.get(userID);
        try {
            System.out.println("Client started");
            Auction auction = (Auction) Naming.lookup("//localhost/AuctionServer");
            Scanner scanner = new Scanner(System.in);
            System.out.print("\nEnter your email for registration: ");
            String userEmail = scanner.nextLine();
            int userId = auction.register(userEmail,pubKey);
            System.out.println("Your user id is: "+ userId);
        askUserInteraction(userId,auction);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static String generateToken() {
        UUID uuid = UUID.randomUUID();
        String token = uuid.toString().replaceAll("-", "");

        return token;
    }
    private synchronized int generateUserID() {
        return nextUserID++;
    }

    public static Auction connectToAuctionServer(String hostname, int port) throws RemoteException, NotBoundException {
        Registry registry = LocateRegistry.getRegistry(hostname, port);
        return (Auction) registry.lookup("Auction");
    }
    public static void askUserInteraction(int userId,Auction auction){
        try{
            Scanner scanner = new Scanner(System.in);
            System.out.println("\nSelect an option:");
                System.out.println("1. Create a new auction");
                System.out.println("2. List auction items");
                System.out.println("3. Make a bid");
                System.out.println("4. Close An Auction");
                System.out.println("5. Exit");
                System.out.print("Enter your choice: ");
                int choice = scanner.nextInt();

                switch (choice) {
                    case 1:
                        System.out.println("\nEnter the details for the new auction item:");
                        System.out.print("\n");
                        String idk = scanner.nextLine();

                        System.out.print("\nEnter Item Name: ");
                        String itemName = scanner.nextLine();

                        System.out.print("\nEnter Item Description: ");
                        String itemDescription = scanner.nextLine();

                        System.out.print("\nEnter Reserve Price: ");
                        int reservePrice = scanner.nextInt();

                        AuctionSaleItem auctionItem = new AuctionSaleItem(itemName, itemDescription, reservePrice);
                        int auctionId = auction.newAuction(userId, auctionItem,token);
                        System.out.println("\nNew Auction ID: " + auctionId);
                        System.out.print("\nItem created successfully\n");
                        askUserInteraction(userId,auction);
                        break;

                    case 2:
                        try {
                            AuctionItem[] items = auction.listItems(userID,token);
                            System.out.println("All Auction Items:");
                            for (AuctionItem item : items) {
                                System.out.println("Item ID: " + item.getItemID());
                                System.out.println("Name: " + item.getName());
                                System.out.println("Description: " + item.getDescription());
                                System.out.println("Highest Bid: " + item.getHighestBid());
                                System.out.println();
                                askUserInteraction(userId,auction);
                            }
                        } catch (RemoteException e) {
                            System.out.println(e.getMessage());
                        }
                        break;

                    case 3:
                        System.out.print("\nEnter the item ID you want to bid on: ");
                        int itemID = scanner.nextInt();
                        System.out.print("\nEnter your bid: ");
                        int bidAmount = scanner.nextInt();
                        boolean success = auction.bid(userId, itemID, bidAmount,token);
                        if (success) {
                            System.out.println("\nBid placed successfully.");
                        } else {
                            System.out.println("\nBid failed.");
                        }
                        askUserInteraction(userId,auction);
                        break;
                    case 4:
                        System.out.print("\nEnter the item ID of the auction to close: ");
                        int itemIDToClose = scanner.nextInt();
                        AuctionResult result = auction.closeAuction(userId, itemIDToClose,token);
                        if (result != null) {
                            System.out.println("\nAuction closed.");
                            System.out.println("Winning Bidder: " + result.getWinningEmail());
                            System.out.println("Winning Price: " + result.getWinningPrice());
                        } else {
                            System.out.println("Auction close failed. Please check the item ID.");
                        }
                        askUserInteraction(userId,auction);
                        break;

                    case 5:
                         System.out.println("Exiting application");
                        System.exit(0);
                        break;

                    default:
                        System.out.println("\nInvalid choice. Please select a valid option.");
                        askUserInteraction(userId,auction);
                        break;
                }

} catch (Exception e) {
            e.printStackTrace();
        }
    }
}
