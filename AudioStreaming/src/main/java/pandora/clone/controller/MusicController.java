package pandora.clone.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import pandora.clone.authorization.JwtTokenUtil;
import pandora.clone.models.Song;
import pandora.clone.services.MusicServices;
import pandora.clone.services.UserServices;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Created by sampastoriza on 4/21/17.
 */

@RestController
public class MusicController {

    @Autowired
    MusicServices musicServices;

    @Value("${jwt.header}")
    private String tokenHeader;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    UserServices userServices;

    @CrossOrigin(origins = "http://localhost:8080")
    @GetMapping("/song/play/{id}")
    public void playSong(@PathVariable Integer id, HttpServletResponse response) {
        byte[] bytes = musicServices.playSong(id);
        try {
            ServletOutputStream sos = response.getOutputStream();
            sos.write(bytes, 0, bytes.length);
            sos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @CrossOrigin(origins = "http://localhost:8080")
    @GetMapping("/play/song/random")
    public ResponseEntity<Song> play(HttpServletRequest request, HttpServletResponse response) {
        String username = userServices.retrieveToken(request, response, tokenHeader);
        if (username == null) {
            try {
                response.sendError(403, "The jwt token has expired. You need to login in again.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Song song = musicServices.playRandomSong(username);
        if (song != null) {
            return new ResponseEntity<>(song, HttpStatus.OK);
        } else {
            try {
                response.sendError(500, "An issue has occurred on the server side");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @CrossOrigin(origins = "http://localhost:8080")
    @GetMapping("genres")
    public ResponseEntity<List> getGenres() {
        return new ResponseEntity<>(musicServices.getGenres(), HttpStatus.OK);
    }

    @CrossOrigin(origins = "http://localhost:8080")
    @GetMapping("/play/genre/{genre}")
    public ResponseEntity<Song> playGenre(@PathVariable String genre, HttpServletRequest request, HttpServletResponse response) {
        String username = userServices.retrieveToken(request, response, tokenHeader);
        if (username == null) {
            try {
                response.sendError(403, "The jwt token has expired. You need to login in again.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Song song = musicServices.playSongByGenre(username, genre);
        if (song != null) {
            return new ResponseEntity<>(song, HttpStatus.OK);
        } else {
            try {
                response.sendError(404, "Genre does not exist");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @CrossOrigin(origins = "http://localhost:8080")
    @GetMapping("/like/song/{id}")
    public ResponseEntity<String> likeSong(@PathVariable Integer id, HttpServletRequest request, HttpServletResponse response) {
        String username = userServices.retrieveToken(request, response, tokenHeader);
        if (username == null) {
            try {
                response.sendError(403, "The jwt token has expired. You need to login in again.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        musicServices.likeSong(id, username);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}

