package pandora.clone.models;

/**
 * Created by sampastoriza on 4/21/17.
 */
public class User {

    private final long id;
    private final String username;
    private final String password;
    private final String email;

    public User(long id, String username, String password, String email) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
    }

    public long getId() {
        return this.id;
    }

    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }

    public String getEmail() {
        return this.email;
    }
}
