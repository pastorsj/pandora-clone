package pandora.clone.audio;

import org.neo4j.driver.v1.*;
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
public class Audio {

    private List<Record> songs;

    public Audio() {
        this.songs = new ArrayList<>();
        this.retrieveSongs();
    }

    public Song retrieveSong(int id) {
        Driver driver = GraphDatabase.driver("bolt://localhost:7687", AuthTokens.basic("neo4j", "database"));
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
        Driver driver = GraphDatabase.driver("bolt://localhost:7687", AuthTokens.basic("neo4j", "database"));
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

    public byte[] playSong(long id) {
        Driver driver = GraphDatabase.driver("bolt://localhost:7687", AuthTokens.basic("neo4j", "database"));
        Session session = driver.session();
        StatementResult result = session.run("match (s:Song) where ID(s) = {id} return s.filepath as filepath", parameters("id", id));
        Record record = result.peek();
        return this.playSong(record);
    }

    public byte[] playSong(Record record) {
        try {
            String filepath = record.get("filepath").asString();
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
}
