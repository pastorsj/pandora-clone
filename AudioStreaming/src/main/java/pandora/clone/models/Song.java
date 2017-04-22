package pandora.clone.models;

import org.neo4j.driver.v1.*;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.neo4j.driver.v1.Values.parameters;

/**
 * Created by sampastoriza on 4/21/17.
 */
public class Song {

    private String artist = "";
    private String year = "";
    private String album = "";
    private String genre = "";
    private String title = "";
    private String track = "";

    private List<Record> songs;

    public Song() {
        this.songs = new ArrayList<>();
    }

    public Song retrieveSong(int id) {
        Driver driver = GraphDatabase.driver("bolt://localhost:7687", AuthTokens.basic("neo4j", "database"));
        Session session = driver.session();
        StatementResult result = session.run("match (s:Song) where ID(s) = {id} return s.artist as artist, s.year as year," +
                "s.album as album, s.genre as genre, s.title as title, s.track as track", parameters("id", id));

        Record record = result.peek();

        this.artist = record.get("artist").asString();
        this.year = record.get("year").asString();
        this.album = record.get("album").asString();
        this.genre = record.get("genre").asString();
        this.title = record.get("title").asString();
        this.track = record.get("track").asString();

        System.out.println("Artist " +  this.artist);

        session.close();
        driver.close();

        return this;
    }

    public void retrieveSongs() {
        Driver driver = GraphDatabase.driver("bolt://localhost:7687", AuthTokens.basic("neo4j", "database"));
        Session session = driver.session();
        StatementResult result = session.run("MATCH (s:Song) " +
                "RETURN s.filepath as filepath, s.artist as artist, s.year as year," +
                "s.album as album, s.genre as genre, s.title as title, s.track as track");
        while ( result.hasNext() )
        {
            Record record = result.next();
            this.songs.add(record);
        }
        session.close();
        driver.close();
    }

    public byte[] playSong(Record songRecord) {
        try {
            String filepath = songRecord.get("filepath").asString();
            System.out.println("Filepath: " + filepath);
            File file = new File(filepath);
            System.out.println(file.exists());
            AudioInputStream mp3Stream = AudioSystem.getAudioInputStream(file);
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

            byte[] ret = Files.readAllBytes(tmp.toPath());

            tmp.delete();
            return ret;

        } catch (IOException e) {
            e.printStackTrace();
        } catch (UnsupportedAudioFileException e) {
            e.printStackTrace();
        }
        return null;
    }

    public byte[] playNextSong() {
        Random r = new Random();
        int nextSongIndex = r.nextInt(this.songs.size());
        Record song = this.songs.get(nextSongIndex);
        return this.playSong(song);
    }

    public String getArtist() {
        return artist;
    }

    public String getYear() {
        return year;
    }

    public String getAlbum() {
        return album;
    }

    public String getGenre() {
        return genre;
    }

    public String getTitle() {
        return title;
    }

    public String getTrack() {
        return track;
    }
}
