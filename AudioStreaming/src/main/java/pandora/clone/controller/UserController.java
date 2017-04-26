package pandora.clone.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import pandora.clone.authorization.JwtTokenUtil;
import pandora.clone.models.User;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by sampastoriza on 4/21/17.
 */

@RestController
public class UserController {

    @Value("${jwt.header}")
    private String tokenHeader;

    @PostMapping("/register")
    public String register(@RequestBody User user) {
        return user.createUser();
    }

    @GetMapping("/login/{username}/{password}")
    public String login(@PathVariable String username, @PathVariable String password) {
        JwtTokenUtil jwtTokenUtil = new JwtTokenUtil();
        return jwtTokenUtil.login("0", username);
    }

    @GetMapping("/user/{id}")
    public User getUser(@PathVariable long id, HttpServletRequest request) {
        String token = request.getHeader(tokenHeader);
        JwtTokenUtil jwtTokenUtil = new JwtTokenUtil();
        jwtTokenUtil.parseJWT(token);
        System.out.println(jwtTokenUtil.getUsernameFromToken(token));
        return new User("user", "password", "email");
    }
}
