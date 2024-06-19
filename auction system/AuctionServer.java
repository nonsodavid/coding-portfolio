
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SealedObject;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Random;

public class AuctionServer extends UnicastRemoteObject implements Auction {

    private SecretKey secretKey; // Load the key from 'keys/testKey.aes'

    public AuctionServer() throws RemoteException {
        super();

        // Load the AES key from 'keys/testKey.aes' and initialize 'secretKey'
        try {
            FileInputStream keyFile = new FileInputStream("keys/testKey.aes");
            ObjectInputStream keyIn = new ObjectInputStream(keyFile);
            secretKey = (SecretKey) keyIn.readObject();
            keyIn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public SealedObject getSpec(int itemID) throws RemoteException {
        // Hard-coded auction items
        AuctionItem item1 = new AuctionItem(1, "Item 1", "Description 1", 100);
        AuctionItem item2 = new AuctionItem(2, "Item 2", "Description 2", 200);

        AuctionItem item = (itemID == 1) ? item1 : (itemID == 2) ? item2 : null;

        if (item != null) {
            try {
                Cipher cipher = Cipher.getInstance("AES");
                cipher.init(Cipher.ENCRYPT_MODE, secretKey);

                // Create a SealedObject using the AES key
                return new SealedObject(item, cipher);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static void main(String[] args) {
        // Implement the server startup as in Level 1
        try {
            Random random=new Random();
            int randNum=1000+random.nextInt(9000);
            AuctionServer auctionServer = new AuctionServer();
            java.rmi.registry.LocateRegistry.createRegistry(randNum);
            java.rmi.Naming.rebind("AuctionServer", auctionServer);
            System.out.println("Server is running");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
