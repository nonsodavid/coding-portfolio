import java.rmi.Naming;
import java.security.PublicKey;

public class Client {
    public static void main(String[] args) {
        try {
            // Specify the URL of the primary replica
            String primaryReplicaURL = "rmi://localhost:1099/Replica1";
            FrontEnd frontEnd = new FrontEnd(primaryReplicaURL);

            // Use frontEnd to make calls to the auction system
            // Example: frontEnd.register(email, pubKey);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

