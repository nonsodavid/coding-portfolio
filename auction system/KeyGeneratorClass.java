
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;

public class KeyGeneratorClass {

    public static void main(String[] args) {
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(128); // You can change the key size if needed

            SecretKey secretKey = keyGen.generateKey();

            // Save the key to a file
            try {
                FileOutputStream keyFile = new FileOutputStream("keys/testKey.aes");
                ObjectOutputStream keyOut = new ObjectOutputStream(keyFile);
                keyOut.writeObject(secretKey);
                System.out.println("AES key generated and saved");
            }
            catch (Exception e) {
            e.printStackTrace();
        }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
