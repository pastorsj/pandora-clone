package pandora.clone.controller;

import org.springframework.web.bind.annotation.*;
import pandora.clone.models.User;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by sampastoriza on 4/21/17.
 */

@RestController
public class UserController {

    private final AtomicLong counter = new AtomicLong();

    @PostMapping("/register")
    public long register(@RequestBody String username, String password, String email) {
        User u = new User(counter.getAndIncrement(), username, password, email);
        return u.getId();
    }

    @GetMapping("/login/{username}/{password}")
    public boolean login(@PathVariable String username, @PathVariable String password) {
        return username.equals("pastorsj") && password.equals("password");
    }

    @GetMapping("/user/{id}")
    public User getUser(@PathVariable long id) {
        return new User(id, "user", "password", "email");
    }
}
