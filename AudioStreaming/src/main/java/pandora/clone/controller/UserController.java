package pandora.clone.controller;

import org.neo4j.driver.v1.*;
import org.springframework.web.bind.annotation.*;
import pandora.clone.models.User;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by sampastoriza on 4/21/17.
 */

@RestController
public class UserController {


    @PostMapping("/register")
    public long register(@RequestBody User user) {
        return user.createUser();
    }

    @GetMapping("/login/{username}/{password}")
    public boolean login(@PathVariable String username, @PathVariable String password) {
        return username.equals("pastorsj") && password.equals("password");
    }

    @GetMapping("/user/{id}")
    public User getUser(@PathVariable long id) {
        return new User("user", "password", "email");
    }
}
