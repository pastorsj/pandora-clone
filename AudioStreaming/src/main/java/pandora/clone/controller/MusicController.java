package pandora.clone.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import pandora.clone.models.Song;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by sampastoriza on 4/21/17.
 */

@RestController
public class MusicController {


    @GetMapping("/song/play/random")
    public void play(HttpServletResponse response) {
        Song s = new Song();
        s.retrieveSongs();
        byte[] bytes = s.playNextSong();
        try {
            System.out.println("Writing to the stream");
            ServletOutputStream sos = response.getOutputStream();
            sos.write(bytes, 0, bytes.length);
            sos.flush();
            System.out.println("Finished");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @GetMapping("/song/info/{id}")
    public Song getInformation(@PathVariable Integer id) {
        Song s = new Song();
        return s.retrieveSong(id);
    }
}

