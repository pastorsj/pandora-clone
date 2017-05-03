package pandora.clone.services;

import org.neo4j.driver.v1.*;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import pandora.clone.models.Song;
import redis.clients.jedis.Jedis;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import static org.neo4j.driver.v1.Values.parameters;

/**
 * Created by sampastoriza on 4/25/17.
 */
@Component
public class MusicServices implements InitializingBean {

    private Session session;
    private Jedis jedis;

    @Value("${neo4j.password}")
    private String neo4jPassword;

    @Value("${neo4j.username}")
    private String neo4jUsername;

    @Value("${neo4j.server}")
    private String neo4jServer;

    @Value("${redis.server}")
    private String redisServer;

    @Autowired
    public MusicServices() {
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Driver driver = GraphDatabase.driver(neo4jServer, AuthTokens.basic(neo4jUsername, neo4jPassword));
        this.session = driver.session();
        this.jedis = new Jedis(redisServer);
    }

    public byte[] playSong(String filepath) {
        try {
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

    public List<String> getGenres() {
        StatementResult result = this.session.run("match (s:Song) return distinct s.genre as genre");

        List<String> genres = new ArrayList<>();
        while(result.hasNext()) {
            Record record = result.next();

            String genre = record.get("genre").asString();
            genres.add(genre);
        }
        return genres;
    }

    public Song playSongByGenre(String username, String genre) {
        String songId = this.jedis.spop(username);

        if (songId == null) {
            this.populateByGenre(username, genre);
            songId = this.jedis.spop(username);
        }

        int id = Integer.parseInt(songId);

        StatementResult result = this.session.run("match (s:Song) where ID(s)={id}" +
                "return s.filepath as filepath, s.artist as artist, s.year as year," +
                "s.album as album, s.genre as genre, s.title as title, s.track as track;", parameters("id", id));

        if (result.hasNext()) {
            Record record = result.next();
            Song s = new Song(id,
                    record.get("artist").asString(),
                    record.get("year").asString(),
                    record.get("album").asString(),
                    record.get("genre").asString(),
                    record.get("title").asString(),
                    record.get("track").asString());

            this.playSong(record.get("filepath").asString());
            return s;
        }
        return null;
    }

    public void populateByGenre(String username, String genre) {
        this.jedis.del(username);

        StatementResult result = this.session.run("match (s:Song) where toUpper(s.genre) = toUpper({genre})" +
                "return ID(s) as id;", parameters("genre", genre));

        result.list().stream().forEach(record -> {
            jedis.sadd(username, Integer.toString(record.get("id").asInt()));
        });
    }

    public Song playRandomSong(String username) {
        String songId = this.jedis.spop(username);

        if (songId == null) {
            this.populateRandomPlaylist(username);
            songId = this.jedis.spop(username);
        }

        System.out.println("songId " + songId);

        int id = Integer.parseInt(songId);

        StatementResult result = this.session.run("match (s:Song) where ID(s)={id}" +
                "return s.filepath as filepath, s.artist as artist, s.year as year," +
                "s.album as album, s.genre as genre, s.title as title, s.track as track;", parameters("id", id));

        if (result.hasNext()) {
            Record record = result.next();
            Song s = new Song(id,
                    record.get("artist").asString(),
                    record.get("year").asString(),
                    record.get("album").asString(),
                    record.get("genre").asString(),
                    record.get("title").asString(),
                    record.get("track").asString());

            this.playSong(record.get("filepath").asString());
            return s;
        }
        return null;
    }

    public void populateRandomPlaylist(String username) {
        this.jedis.del(username);

        StatementResult result = this.session.run("MATCH (s:Song) " +
                "RETURN ID(s) as id, s.filepath as filepath, s.artist as artist, s.year as year," +
                "s.album as album, s.genre as genre, s.title as title, s.track as track");

        result.list().stream().forEach(record -> {
            jedis.sadd(username, Integer.toString(record.get("id").asInt()));
        });
    }

    public void likeSong(Integer id, String username) {
        Driver driver = GraphDatabase.driver(neo4jServer, AuthTokens.basic(neo4jUsername, neo4jPassword));
        Session session = driver.session();
        session.run("match (u:User {username: {username}}), (s:Song) where ID(s)={id} create (u)-[:LIKES]->(s);",
                parameters("id", id, "username", username));
    }

    public byte[] playSong(Integer id) {
        StatementResult result = this.session.run("match (s:Song) where ID(s)={id}" +
                "return s.filepath as filepath;", parameters("id", id));

        if (result.hasNext()) {
            Record record = result.next();
            return this.playSong(record.get("filepath").asString());
        }
        return null;
    }
}
