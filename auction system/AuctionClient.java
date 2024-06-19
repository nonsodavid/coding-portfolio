import java.util.Scanner;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SealedObject;
import java.rmi.Naming;
import java.rmi.RemoteException;


public class AuctionClient {

    private static SecretKey secretKey;

    public static void main(String[] args) {
        // Load the AES key from 'keys/testKey.aes' and initialize 'secretKey'
        try {
            //Getting the user's home directory
            FileInputStream keyFile = new FileInputStream("keys/testKey.aes");
            ObjectInputStream keyIn = new ObjectInputStream(keyFile);
            secretKey = (SecretKey) keyIn.readObject();
            keyIn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            Auction auction = (Auction) Naming.lookup("//localhost/AuctionServer");

            // Get the itemID from user input
            Scanner scanner = new Scanner(System.in);
            System.out.print("Enter the itemID: ");
            int itemID = scanner.nextInt();
            
            SealedObject sealedObject = auction.getSpec(itemID);

            if (sealedObject != null) {
                // Decrypt the SealedObject using the AES key
                Cipher cipher = Cipher.getInstance("AES");
                cipher.init(Cipher.DECRYPT_MODE, secretKey); // Load the AES key

                AuctionItem item = (AuctionItem) sealedObject.getObject(cipher);

                System.out.println("Item ID: " + item.getItemID());
                System.out.println("Name: " + item.getName());
                System.out.println("Description: " + item.getDescription());
                System.out.println("Highest Bid: $" + item.getHighestBid());
            } else {
                System.out.println("Item not found.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
