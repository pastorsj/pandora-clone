package pandora.clone.controller;

import org.neo4j.driver.v1.exceptions.ClientException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pandora.clone.authorization.JwtTokenUtil;
import pandora.clone.models.User;
import pandora.clone.services.UserServices;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.ws.Response;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

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

    @CrossOrigin(origins = "http://localhost:8100")
    @PostMapping("/register")
    public String register(@RequestBody User user, HttpServletResponse response) {
        try {
            String jwt = userService.createUser(user);
            System.out.println("JWT " + jwt);
            return jwt;
        } catch (ClientException e) {
            try {
                response.sendError(500, e.getMessage());
            } catch (IOException e1) {
                response.setStatus(500);
            }
            return null;
        }
    }

    @CrossOrigin(origins = "http://localhost:8100")
    @GetMapping("/login")
    public ResponseEntity<String> login(HttpServletRequest request, HttpServletResponse response) throws IOException, NoSuchAlgorithmException {
        String usernamePassword = request.getHeader(tokenHeader);
        if (usernamePassword.split(":").length == 2) {
            String username = usernamePassword.split(":")[0];
            String password = usernamePassword.split(":")[1];

            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(password.getBytes());
            String encryptedPassword = new String(messageDigest.digest());

            String jwt = jwtTokenUtil.login(username, encryptedPassword);
            if (jwt == null) {
                response.sendError(403, "Username or password does not match");
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
            return new ResponseEntity<>(jwt, HttpStatus.OK);

        }
        response.sendError(404, "Username or password not found");
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @CrossOrigin(origins = "http://localhost:8100")
    @GetMapping("/user/{id}")
    public User getUser(@PathVariable long id, HttpServletRequest request, HttpServletResponse response) {
        try {
            String token = request.getHeader(tokenHeader);
            token = token.substring(7);
            boolean isValid = jwtTokenUtil.parseJWT(token);
            if (!isValid) {
                response.setStatus(403);
                return null;
            } else {
                User user = userService.retrieveUser(id);
                if (user == null) {
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

    @CrossOrigin(origins = "http://localhost:8100")
    @GetMapping("/user/refresh")
    public ResponseEntity<String> refreshToken(HttpServletRequest request, HttpServletResponse response) {
        String token = request.getHeader(tokenHeader);
        token = token.substring(7);
        if (jwtTokenUtil.canTokenBeRefreshed(token)) {
            String jwt = jwtTokenUtil.refreshToken(token);
            return new ResponseEntity<>(jwt, HttpStatus.OK);
        } else {
            try {
                response.sendError(403, "You must login since the jwt has expired");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
