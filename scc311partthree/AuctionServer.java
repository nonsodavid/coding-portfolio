import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class AuctionServer extends UnicastRemoteObject implements Auction {
    private List<User> users;
    private List<AuctionItem> auctionItems;
    private List<AuctionSaleItem> saleItems;
    private List<AuctionResult> auctionResults;
    private static PrivateKey serverPrivateKey;
    private Map<Integer, PublicKey> userPublicKeys = new ConcurrentHashMap<>();
    private String serverChallenge;
    private Map<Integer, String> userTokens = new HashMap<>();

    public AuctionServer() throws RemoteException {
        super();
        users = new ArrayList<>();
        auctionItems = new ArrayList<>();
        saleItems = new ArrayList<>();
        auctionResults = new ArrayList<>();
    }

    @Override
    public ChallengeInfo challenge(int userID, String clientChallenge) throws RemoteException {
        serverChallenge = generateRandomChallenge();
        byte[] response = sign(clientChallenge.getBytes(), serverPrivateKey);
        String serverChallenge = generateRandomChallenge();
        return new ChallengeInfo(response, serverChallenge);
    }

    @Override
    public Integer register(String email, PublicKey pubKey) throws RemoteException {
        User newUser = new User(email);
        users.add(newUser);
        return newUser.getUserId();
    }

    @Override
    public int getPrimaryReplicaID() throws RemoteException {
        return 1;
    }

    @Override
    public TokenInfo authenticate(int userID, byte[] signature) throws RemoteException {
        PublicKey userPublicKey = userPublicKeys.get(userID);
        boolean isSignatureValid = verify(serverChallenge.getBytes(), signature, userPublicKey);

        if (isSignatureValid) {
            String token = generateUniqueToken();
            long expiryTime = System.currentTimeMillis() + 10000;

            TokenInfo tokenInfo = new TokenInfo(token, expiryTime);
            return tokenInfo;
        } else {
            return null;
        }
    }

    private String generateRandomChallenge() {
        byte[] randomBytes = new byte[16];
        new SecureRandom().nextBytes(randomBytes);
        String randomChallenge = Base64.getEncoder().encodeToString(randomBytes);

        return randomChallenge;
    }

    @Override
    public AuctionItem getSpec(int itemID, String token) throws RemoteException {
        for (AuctionItem item : auctionItems) {
            if (item.getItemID() == itemID) {
                return item;
            }
        }
        return null;
    }

    @Override
    public Integer newAuction(int userID, AuctionSaleItem item, String token) throws RemoteException {
        AuctionItem newItem = new AuctionItem(generateUniqueItemID(), item.getName(), item.getDescription());
        auctionItems.add(newItem);
        return newItem.getItemID();
    }

    @Override
    public AuctionItem[] listItems(int userID, String token) throws RemoteException {
        return auctionItems.toArray(new AuctionItem[0]);
    }

    @Override
    public boolean bid(int userID, int itemID, int price, String token) throws RemoteException {
        AuctionItem item = findItemByID(itemID);
        if (item != null && !item.isClosed() && price > item.getHighestBid()) {
            item.setHighestBid(price);
            return true;
        }
        return false;
    }

    private AuctionItem findItemByID(int itemID) {
        for (AuctionItem item : auctionItems) {
            if (item.getItemID() == itemID) {
                return item;
            }
        }
        return null;
    }

    private int generateUniqueItemID() {
        Random random = new Random();
        int min = 100000;
        int max = 999999;
        int randomNumber = random.nextInt((max - min) + 1) + min;
        return randomNumber;

    }

    @Override
    public AuctionResult closeAuction(int userID, int itemID, String token) throws RemoteException {
        AuctionItem closedItem = null;
        int winningBid = 0;
        User winningUser = null;
        closedItem.setHighestBid(winningBid);
        closedItem.setClosed(true);

        AuctionResult result = new AuctionResult(winningUser.getEmail(), winningBid);
        auctionResults.add(result);
        return result;
    }
 public static void writeKeyToFile(Key key, String filePath) {
        try (FileOutputStream fos = new FileOutputStream(filePath);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {

            oos.writeObject(key);
            System.out.println("Key has been written to: " + filePath);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {

        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            serverPrivateKey = keyPair.getPrivate();
            PublicKey publicKey = keyPair.getPublic();
            String filePath = "keys/serverKey.pub";

            // Write the key to the file
            writeKeyToFile(publicKey, filePath);
            Random random = new Random();
            int randNum = 1000 + random.nextInt(9000);
            AuctionServerImpl server = new AuctionServerImpl(serverPrivateKey);
            java.rmi.registry.LocateRegistry.createRegistry(randNum);
            java.rmi.Naming.rebind("AuctionServer", server);
            System.out.println("\nServer started, please wait...");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private byte[] sign(byte[] data, PrivateKey privateKey) {
        try {
            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initSign(privateKey);
            signature.update(data);
            return signature.sign();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private boolean verify(byte[] data, byte[] signature, PublicKey pubKey) {
        try {
            Signature sig = Signature.getInstance("SHA256withRSA");
            sig.initVerify(pubKey);
            sig.update(data);
            return sig.verify(signature);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private String generateUniqueToken() {
        String token;
        do {
            token = UUID.randomUUID().toString();
        } while (userTokens.containsValue(token));
        return token;
    }

}
