package pandora.clone.controller;

import org.neo4j.driver.v1.*;
import org.springframework.web.bind.annotation.*;
import pandora.clone.authorization.JwtAuthorization;
import pandora.clone.models.User;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by sampastoriza on 4/21/17.
 */

@RestController
public class UserController {


    @PostMapping("/register")
    public String register(@RequestBody User user) {
        return user.createUser();
    }

    @GetMapping("/login/{username}/{password}")
    public String login(@PathVariable String username, @PathVariable String password) {
        JwtAuthorization jwt = new JwtAuthorization();
        return jwt.login(username, "0");
    }

    @GetMapping("/user/{id}")
    public User getUser(@PathVariable long id) {
        return new User("user", "password", "email");
    }
}
