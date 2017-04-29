package pandora.clone.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import pandora.clone.authorization.JwtTokenUtil;
import pandora.clone.models.User;
import pandora.clone.services.MusicServices;
import pandora.clone.models.Song;

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

    @GetMapping("/song/random")
    public void playRandom(HttpServletResponse response) {
        byte[] bytes = musicServices.playNextSong();
        try {
            ServletOutputStream sos = response.getOutputStream();
            sos.write(bytes, 0, bytes.length);
            sos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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

    @GetMapping("/song/play/random")
    public Song play() {
        return musicServices.getRandomSong();
    }

    @GetMapping("genres")
    public ResponseEntity<List> getGenres() {
        return new ResponseEntity<>(musicServices.getGenres(), HttpStatus.OK);
    }

    @GetMapping("genre/{genre}")
    public ResponseEntity<Song> playGenre(@PathVariable String genre, HttpServletResponse response) {
        Song song = musicServices.playByGenre(genre);
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

    @GetMapping("/song/like/{id}")
    public ResponseEntity<String> likeSong(@PathVariable Integer id, HttpServletRequest request, HttpServletResponse response) {
        try {
            String token = request.getHeader(tokenHeader);
            token = token.substring(7);
            boolean isValid = jwtTokenUtil.parseJWT(token);
            if(!isValid) {
                response.setStatus(403);
                return null;
            } else {
                String username= jwtTokenUtil.getUsernameFromToken(token);
                if(username == null) {
                    try {
                        response.sendError(404, "User does not exist");
                        return null;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                musicServices.likeSong(id, username);
                return new ResponseEntity<>(HttpStatus.OK);
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(403);
            return null;
        }
    }
}

