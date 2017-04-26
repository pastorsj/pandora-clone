package pandora.clone.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import pandora.clone.authorization.JwtTokenUtil;
import pandora.clone.models.User;
import pandora.clone.services.UserServices;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by sampastoriza on 4/21/17.
 */

@RestController
public class UserController {

    @Value("${jwt.header}")
    private String tokenHeader;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private UserServices userService;

    @PostMapping("/register")
    public String register(@RequestBody User user) {
        return userService.createUser(user);
    }

    @GetMapping("/login/{username}/{password}")
    public String login(@PathVariable String username, @PathVariable String password, HttpServletResponse response) {
       String jwt = jwtTokenUtil.login(username, password);
       if(jwt == null) {
           response.setStatus(403);
           return null;
       }
       return jwt;
    }

    @GetMapping("/user/{id}")
    public User getUser(@PathVariable long id, HttpServletRequest request, HttpServletResponse response) {
        try {
            String token = request.getHeader(tokenHeader);
            token = token.substring(7);
            boolean isValid = jwtTokenUtil.parseJWT(token);
            if(!isValid) {
                response.setStatus(403);
                return null;
            } else {
                User user = userService.retrieveUser(id);
                if(user == null) {
                    try {
                        response.sendError(404, "User does not exist");
                        return null;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                return user;
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(403);
            return null;
        }
    }
}
