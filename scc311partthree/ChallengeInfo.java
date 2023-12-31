import java.io.Serializable;

public class ChallengeInfo implements Serializable {
    byte[] response;           
    String clientChallenge;  
          public ChallengeInfo(byte[] response,String clientChallenge)
          {
                    this.clientChallenge=clientChallenge;
                    this.response=response;
          }
          public ChallengeInfo(){}
}
