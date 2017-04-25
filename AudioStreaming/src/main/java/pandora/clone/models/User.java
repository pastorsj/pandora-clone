package pandora.clone.models;

import io.jsonwebtoken.*;
import io.jsonwebtoken.impl.crypto.MacProvider;
import org.neo4j.driver.v1.*;
import pandora.clone.authorization.JwtAuthorization;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.net.Inet4Address;
import java.security.Key;
import java.util.Date;

import static org.neo4j.driver.v1.Values.parameters;

/**
 * Created by sampastoriza on 4/21/17.
 */
public class User {
    private String username;
    private String password;
    private String email;

    public User() {}

    public User(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }

    public String createUser() {
        Driver driver = GraphDatabase.driver("bolt://localhost:7687", AuthTokens.basic("neo4j", "database"));
        Session session = driver.session();
        StatementResult result = session.run("create (u:User{username: {username}, password: {password}, email: {email}}) return ID(u) as id",
                parameters("username", this.username, "password", this.password, "email", this.email));

        Record record = result.next();

        String id = record.get("id").asString();

        JwtAuthorization jwt = new JwtAuthorization();
        return jwt.login(this.username, id);
    }

    public String login(String username, String password) {
        JwtAuthorization jwt = new JwtAuthorization();
        return jwt.login(username, "0");
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
