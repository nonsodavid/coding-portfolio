import java.nio.charset.StandardCharsets;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class AuctionServerImpl extends UnicastRemoteObject implements Auction {
    private Map<Integer, String> registeredUsers = new HashMap<>();
    private Map<Integer, AuctionSaleItem> ongoingAuctions = new HashMap<>();
    private List<AuctionItem> listedItems = new ArrayList<>();
    private int auctionItemIdCounter = 1;
    private int nextUserID = 1;
    private Map<Integer, PublicKey> userPublicKeys = new HashMap<>();
    private Map<Integer, String> challenges = new HashMap<>();
    private Map<Integer, TokenInfo> userTokens = new HashMap<>();
    private PrivateKey serverPrivateKey;

    public AuctionServerImpl(PrivateKey serverPrivateKey) throws RemoteException {
        this.serverPrivateKey = serverPrivateKey;
    }
   
    @Override
    public AuctionItem getSpec(int itemID,String name) throws RemoteException {
        for (AuctionItem item : listedItems) {
            if (item.getItemID() == itemID) {
                return item;
            }
        }
        return null;
    }
    @Override
    public int getPrimaryReplicaID() throws RemoteException {
        return 1;
    }

    @Override
    public Integer newAuction(int userID, AuctionSaleItem item, String token) throws RemoteException {
        if (!registeredUsers.containsKey(userID)) {
            return -1; 
        }

        int itemID = auctionItemIdCounter++;
        ongoingAuctions.put(itemID, item);
        return itemID;
    }

    @Override
    public AuctionItem[] listItems(int userID, String token) throws RemoteException {
        return listedItems.toArray(new AuctionItem[0]);
    }

    @Override
    public boolean bid(int userID, int itemID, int price,String token) throws RemoteException {
        if (!registeredUsers.containsKey(userID) || !ongoingAuctions.containsKey(itemID)) {
            return false; 
        }

        AuctionSaleItem item = ongoingAuctions.get(itemID);
        if (price > item.getReservePrice()) {
            listedItems.add(new AuctionItem(itemID, item.getName(), item.getDescription(), price));
            ongoingAuctions.remove(itemID);
            return true;
        }

        return false;
    }

    @Override
    public AuctionResult closeAuction(int userID, int itemID, String token) throws RemoteException {
        if (!registeredUsers.containsKey(userID) || !ongoingAuctions.containsKey(itemID)) {
            return null; 
        }

        AuctionSaleItem item = ongoingAuctions.get(itemID);
        String winningEmail = "";
        int winningPrice = 0;

        if (listedItems.isEmpty()) {
            return new AuctionResult(winningEmail, winningPrice);
        }

        for (AuctionItem listedItem : listedItems) {
            if (listedItem.getItemID() == itemID) {
                if (listedItem.getHighestBid() > winningPrice) {
                    winningPrice = listedItem.getHighestBid();
                    winningEmail = registeredUsers.get(userID);
                }
            }
        }

        return new AuctionResult(winningEmail, winningPrice);
    }
    @Override
    public Integer register(String email, PublicKey pubKey) throws RemoteException {
      
        int userID = generateUserID(); 
        userPublicKeys.put(userID, pubKey);
        return userID;
    }

    @Override
    public ChallengeInfo challenge(int userID, String clientChallenge) throws RemoteException {
        SecureRandom random = new SecureRandom();
        byte[] serverChallenge = new byte[32];
        random.nextBytes(serverChallenge);
        challenges.put(userID, clientChallenge);
        byte[] signature = sign(serverChallenge);

        ChallengeInfo challengeInfo = new ChallengeInfo();
        challengeInfo.response = signature;
        challengeInfo.clientChallenge = new String(serverChallenge, StandardCharsets.UTF_8);

        return challengeInfo;
    }

    @Override
    public TokenInfo authenticate(int userID, byte[] signature) throws RemoteException {
        PublicKey pubKey = userPublicKeys.get(userID);
        boolean verified = verify(challenges.get(userID).getBytes(StandardCharsets.UTF_8), signature, pubKey);

        if (verified) {
            String token =  generateToken();
            long expiryTime = System.currentTimeMillis() + 10000; 
            TokenInfo tokenInfo = new TokenInfo();
            tokenInfo.token = token;
            tokenInfo.expiryTime = expiryTime;
            userTokens.put(userID, tokenInfo);
            return tokenInfo;
        } else {
            // Authentication failed
            return null;
        }
    }

    private byte[] sign(byte[] data) {
        try {
            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initSign(serverPrivateKey);
            signature.update(data);
            byte[] signatureBytes = signature.sign();
            return signatureBytes;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private boolean verify(byte[] data, byte[] signature, PublicKey pubKey) {
        try {
            Signature verifier = Signature.getInstance("SHA256withRSA");
            verifier.initVerify(pubKey);
 verifier.update(data);
            return verifier.verify(signature);
        } catch (Exception e) {
            e.printStackTrace(); 
            return false;
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
}
