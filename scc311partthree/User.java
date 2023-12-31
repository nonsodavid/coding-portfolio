import java.io.Serializable;

public class User implements Serializable {
    private static int nextUserId = 1;

    private int userId;
    private String email;

    public User(String email) {
        this.userId = nextUserId++;
        this.email = email;
    }

    public int getUserId() {
        return userId;
    }

    public String getEmail() {
        return email;
    }
}
