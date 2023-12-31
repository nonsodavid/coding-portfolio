import java.io.Serializable;

public class TokenInfo implements Serializable {
    String token;                 
    long expiryTime;  
                 public TokenInfo(String token,long expiryTime){
                    this.token=token;
                    this.expiryTime=expiryTime;
                 }
                 public TokenInfo(){}
}
