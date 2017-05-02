package pandora.clone.services;

import org.neo4j.driver.v1.*;
import org.neo4j.driver.v1.exceptions.ClientException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import pandora.clone.authorization.JwtTokenUtil;
import pandora.clone.models.User;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

import static org.neo4j.driver.v1.Values.parameters;

/**
 * Created by sampastoriza on 4/26/17.
 */

@Component
public class UserServices {

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    public String createUser(User user) throws ClientException {
        Driver driver = GraphDatabase.driver("bolt://localhost:7687", AuthTokens.basic("neo4j", "database"));
        Session session = driver.session();
        session.run("create (u:User{username: {username}, password: {password}, email: {email}})",
                parameters("username", user.getUsername(), "password", user.getPassword(), "email", user.getEmail()));

        session.close();
        driver.close();

        return jwtTokenUtil.login(user.getUsername(), user.getPassword());
    }

    public User retrieveUser(long id) {
        Driver driver = GraphDatabase.driver("bolt://localhost:7687", AuthTokens.basic("neo4j", "database"));
        Session session = driver.session();
        StatementResult result = session.run("match (u:User) where ID(u)={id} return u.username as username, u.email as email",
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

        session.close();
        driver.close();

        return user;
    }

    public String retrieveToken(HttpServletRequest request, HttpServletResponse response, String tokenHeader) {
        String token = request.getHeader(tokenHeader);
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
