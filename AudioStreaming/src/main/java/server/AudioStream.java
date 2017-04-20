package server;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.neo4j.driver.v1.*;

import javax.sound.sampled.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

/**
 * Created by sampastoriza on 4/16/17.
 */

public class AudioStream implements Runnable {
    private Socket client;
    private String songsDir = "./AudioStreaming/songs";
    private File currentSong;
    private List<String> songs;
    private FileInputStream in = null;
    private OutputStream out = null;

    public AudioStream(Socket client) {
        this.client = client;
        this.songs = new ArrayList<>();
    }

    @Override
    public void run() {
        this.playStream();
    }

    public void playStream() {
        Driver driver = GraphDatabase.driver("bolt://localhost:7687", AuthTokens.basic("neo4j", "database"));
        Session session = driver.session();
        StatementResult result = session.run("MATCH (s:Song) RETURN s.filepath as filepath");
        while ( result.hasNext() )
        {
            Record record = result.next();
            String filePath = record.get("filepath").asString();
            System.out.println("Filepath: " + filePath);
            songs.add(filePath);
        }
        this.playNextSong();
    }

    public void playSong(String song) {
        try {
            AudioInputStream mp3Stream = AudioSystem.getAudioInputStream(new File(song));
            AudioFormat sourceFormat = mp3Stream.getFormat();
            AudioFormat convertFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,
                    sourceFormat.getSampleRate(), 16,
                    sourceFormat.getChannels(),
                    sourceFormat.getChannels() * 2,
                    sourceFormat.getSampleRate(),
                    false);
            String tmpName = "tmp" + Thread.currentThread().getId();
            File tmp = new File(tmpName);
            AudioInputStream converted = AudioSystem.getAudioInputStream(convertFormat, mp3Stream);
            AudioSystem.write(converted, AudioFileFormat.Type.WAVE, tmp);

            this.currentSong = AudioUtil.getSoundFile(tmpName);
            this.out = this.client.getOutputStream();
            this.in = new FileInputStream(this.currentSong);
            byte buffer[] = new byte[1024];
            int count;
            while ((count = this.in.read(buffer)) != -1)
                try {
                    out.write(buffer, 0, count);
                } catch (Exception e) {
                    e.printStackTrace();
                    break;
                }
            tmp.delete();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (UnsupportedAudioFileException e) {
            e.printStackTrace();
        }
    }

    public void playNextSong() {
        try {
            if (this.in != null) {
                this.in.close();
                this.out.flush();
            }
            Random r = new Random();
            //System.out.println(this.songs.size());
            int nextSongIndex = r.nextInt(this.songs.size());
            String song = this.songs.get(nextSongIndex);
            this.playSong(song);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}