package pandora.clone.services;

import org.neo4j.driver.v1.*;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;
import pandora.clone.models.Song;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.neo4j.driver.v1.Values.parameters;

/**
 * Created by sampastoriza on 4/25/17.
 */
@Component
public class MusicServices implements InitializingBean {

    private List<Record> songs;

    @Value("${neo4j.password}")
    private String neo4jPassword;

    @Value("${neo4j.username}")
    private String neo4jUsername;

    @Value("${neo4j.server}")
    private String neo4jServer;

    @Autowired
    public MusicServices() {
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.songs = new ArrayList<>();
        this.retrieveSongs();
    }

    public Song retrieveSong(int id) {
        Driver driver = GraphDatabase.driver(neo4jServer, AuthTokens.basic(neo4jUsername, neo4jPassword));
        Session session = driver.session();
        StatementResult result = session.run("match (s:Song) where ID(s) = {id} return s.artist as artist, s.year as year," +
                "s.album as album, s.genre as genre, s.title as title, s.track as track", parameters("id", id));

        Record record = result.peek();

        Song s = new Song(id,
                record.get("artist").asString(),
                record.get("year").asString(),
                record.get("album").asString(),
                record.get("genre").asString(),
                record.get("title").asString(),
                record.get("track").asString());

        session.close();
        driver.close();

        return s;
    }

    public void retrieveSongs() {
        Driver driver = GraphDatabase.driver(neo4jServer, AuthTokens.basic(neo4jUsername, neo4jPassword));
        Session session = driver.session();
        StatementResult result = session.run("MATCH (s:Song) " +
                "RETURN ID(s) as id, s.filepath as filepath, s.artist as artist, s.year as year," +
                "s.album as album, s.genre as genre, s.title as title, s.track as track");
        while ( result.hasNext() )
        {
            Record record = result.next();
            this.songs.add(record);
        }
        session.close();
        driver.close();
    }

    public byte[] playSong(int id) {
        Driver driver = GraphDatabase.driver(neo4jServer, AuthTokens.basic(neo4jUsername, neo4jPassword));
        Session session = driver.session();
        StatementResult result = session.run("match (s:Song) where ID(s) = {id} return s.filepath as filepath", parameters("id", id));
        Record record = result.peek();

        session.close();
        driver.close();
        return this.playSong(record);
    }

    public byte[] playSong(Record record) {
        try {
            String filepath = record.get("filepath").asString();
            File file = new File(filepath);
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

    public Song getRandomSong() {
        Random r = new Random();
        int nextSongIndex = r.nextInt(this.songs.size());
        Record record = this.songs.get(nextSongIndex);

        Song s = new Song(record.get("id").asInt(),
                record.get("artist").asString(),
                record.get("year").asString(),
                record.get("album").asString(),
                record.get("genre").asString(),
                record.get("title").asString(),
                record.get("track").asString());

        return s;
    }

    public byte[] playNextSong() {
        Random r = new Random();
        int nextSongIndex = r.nextInt(this.songs.size());
        Record song = this.songs.get(nextSongIndex);
        return this.playSong(song);
    }

    public List<String> getGenres() {
        Driver driver = GraphDatabase.driver(neo4jServer, AuthTokens.basic(neo4jUsername, neo4jPassword));
        Session session = driver.session();
        StatementResult result = session.run("match (s:Song) return distinct s.genre as genre");

        List<String> genres = new ArrayList<>();
        while(result.hasNext()) {
            Record record = result.next();

            String genre = record.get("genre").asString();
            genres.add(genre);
        }

        session.close();
        driver.close();
        return genres;
    }

    public Song playByGenre(String genre) {
        Driver driver = GraphDatabase.driver(neo4jServer, AuthTokens.basic(neo4jUsername, neo4jPassword));
        Session session = driver.session();
        StatementResult result = session.run("match (s:Song) where toUpper(s.genre) = toUpper({genre})" +
                "return ID(s) as id, s.filepath as filepath, s.artist as artist, s.year as year," +
                "s.album as album, s.genre as genre, s.title as title, s.track as track;", parameters("genre", genre));

        List<Record> songList = result.list();

        Random r = new Random();
        int nextSongIndex = r.nextInt(songList.size());
        Record song = songList.get(nextSongIndex);

        int id = song.get("id").asInt();

        Song s = new Song(
                id,
                song.get("artist").asString(),
                song.get("year").asString(),
                song.get("album").asString(),
                song.get("genre").asString(),
                song.get("title").asString(),
                song.get("track").asString());

        this.playSong(id);
        return s;
    }
}
