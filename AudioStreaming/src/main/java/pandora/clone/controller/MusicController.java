package pandora.clone.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import pandora.clone.audio.Audio;
import pandora.clone.models.Song;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by sampastoriza on 4/21/17.
 */

@RestController
public class MusicController {


    @GetMapping("/song/random")
    public void playRandom(HttpServletResponse response) {
        Audio audio = new Audio();
        byte[] bytes = audio.playNextSong();
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
        Audio audio = new Audio();
        byte[] bytes = audio.playSong(id);
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
        Audio audio = new Audio();
        return audio.getRandomSong();
    }
}

