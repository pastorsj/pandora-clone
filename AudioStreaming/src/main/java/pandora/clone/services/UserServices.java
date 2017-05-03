package pandora.clone.services;

import org.neo4j.driver.v1.*;
import org.neo4j.driver.v1.Value;
import org.neo4j.driver.v1.exceptions.ClientException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.Component;
import pandora.clone.authorization.JwtTokenUtil;
import pandora.clone.models.User;
import redis.clients.jedis.Jedis;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static org.neo4j.driver.v1.Values.parameters;

/**
 * Created by sampastoriza on 4/26/17.
 */

@Component
public class UserServices implements InitializingBean {

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    private Session session;

    @org.springframework.beans.factory.annotation.Value("${neo4j.password}")
    private String neo4jPassword;

    @org.springframework.beans.factory.annotation.Value("${neo4j.username}")
    private String neo4jUsername;

    @org.springframework.beans.factory.annotation.Value("${neo4j.server}")
    private String neo4jServer;

    @Autowired
    public UserServices() {
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Driver driver = GraphDatabase.driver(neo4jServer, AuthTokens.basic(neo4jUsername, neo4jPassword));
        this.session = driver.session();
    }

    public String createUser(User user) throws ClientException {
        String password = user.getPassword();
        MessageDigest messageDigest = null;
        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        messageDigest.update(password.getBytes());
        String encryptedPassword = new String(messageDigest.digest());

        this.session.run("create (u:User{username: {username}, password: {password}, email: {email}})",
                parameters("username", user.getUsername(), "password", encryptedPassword, "email", user.getEmail()));

        return jwtTokenUtil.login(user.getUsername(), encryptedPassword);
    }

    public User retrieveUser(long id) {
        StatementResult result = this.session.run("match (u:User) where ID(u)={id} return u.username as username, u.email as email",
                parameters("id", id));

        if(!result.hasNext()) {
            return null;
        }

        Record record = result.peek();

        String username = record.get("username").asString();
        String email = record.get("email").asString();
        User user  = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setId(id);

        return user;
    }

    public String retrieveToken(HttpServletRequest request, HttpServletResponse response, String tokenHeader) {
        String token = request.getHeader(tokenHeader);

        System.out.println(token);

        token = token.substring(7);
        boolean isValid = jwtTokenUtil.parseJWT(token);
        if(!isValid) {
            response.setStatus(403);
            return null;
        } else {
            String username = jwtTokenUtil.getUsernameFromToken(token);
            if(username == null) {
                try {
                    response.sendError(404, "User does not exist");
                    return null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return username;
        }
    }
}
