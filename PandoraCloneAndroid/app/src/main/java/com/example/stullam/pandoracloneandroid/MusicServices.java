//package pandora.clone.services;
//
//import android.content.pm.PackageInstaller;
//import android.media.tv.TvInputService;
//
//import org.neo4j.driver.v1.*;
//import org.springframework.beans.factory.InitializingBean;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Component;
//import pandora.clone.models.Song;
//import redis.clients.jedis.Jedis;
//
//import javax.sound.sampled.*;
//import java.io.File;
//import java.io.IOException;
//import java.nio.file.Files;
//import java.sql.Driver;
//import java.util.ArrayList;
//import java.util.List;
//
//import static org.neo4j.driver.v1.Values.parameters;
//
///**
// * Created by sampastoriza on 4/25/17.
// */
//@Component
//public class MusicServices implements InitializingBean {
//
//    private TvInputService.Session session;
//    private Jedis jedis;
//
//    @Value("${neo4j.password}")
//    private String neo4jPassword;
//
//    @Value("${neo4j.username}")
//    private String neo4jUsername;
//
//    @Value("${neo4j.server}")
//    private String neo4jServer;
//
//    @Value("${redis.server}")
//    private String redisServer;
//
//    @Autowired
//    public MusicServices() {
//    }
//
//    @Override
//    public void afterPropertiesSet() throws Exception {
//        Driver driver = GraphDatabase.driver(neo4jServer, AuthTokens.basic(neo4jUsername, neo4jPassword));
//        this.session = driver.session();
//        this.jedis = new Jedis(redisServer);
//    }
//
//
//    public boolean userLikedSong(String username, int id) {
//        StatementResult result = this.session.run("match (u:User)-[like:LIKES]->(s:Song) where ID(s)={id} and u.username={username} return like;",
//                parameters("id", id, "username", username));
//
//        return result.hasNext();
//    }
//
//
//    public Song playByLikes(String username) {
//        String songId = this.jedis.spop(username);
//
//        if (songId == null) {
//            this.populateByLikes(username);
//            songId = this.jedis.spop(username);
//        }
//
//        System.out.println("songId " + songId);
//
//        int id = Integer.parseInt(songId);
//
//        boolean liked = this.userLikedSong(username, id);
//
//        StatementResult result = this.session.run("match (s:Song) where ID(s)={id}" +
//                "return s.filepath as filepath, s.artist as artist, s.year as year," +
//                "s.album as album, s.genre as genre, s.title as title, s.track as track, s.duration as duration;", parameters("id", id));
//
//        if (result.hasNext()) {
//            Record record = result.next();
//            Song s = new Song(id,
//                    record.get("artist").asString(),
//                    record.get("year").asString(),
//                    record.get("album").asString(),
//                    record.get("genre").asString(),
//                    record.get("title").asString(),
//                    record.get("track").asString(),
//                    record.get("duration").asInt(),
//                    liked);
//
//            this.playSong(record.get("filepath").asString());
//            return s;
//        }
//        return null;
//    }
//
//    private void populateByLikes(String username) {
//        this.jedis.del(username);
//
//        StatementResult result = this.session.run("" +
//                "match (u:User {username: {username}})-[:LIKES]->(:Song)<-[:LIKES]-(u2:User) " +
//                "match (u2:User)-[:LIKES]->(s:Song) return ID(s) as id union " +
//                "match (u: User)-[:LIKES]->(s3:Song) return ID(s3) as id;", parameters("username", username));
//
//        System.out.println("Liked songs " + result.summary());
//
//        result.list().stream().forEach(record -> jedis.sadd(username, Integer.toString(record.get("id").asInt())));
//    }
//
//    public void likeSong(Integer id, String username) {
//        this.session.run("match (u:User {username: {username}}), (s:Song) where ID(s)={id} create (u)-[:LIKES]->(s);",
//                parameters("id", id, "username", username));
//    }
//
//    public byte[] playSong(Integer id) {
//        StatementResult result = this.session.run("match (s:Song) where ID(s)={id}" +
//                "return s.filepath as filepath;", parameters("id", id));
//
//        if (result.hasNext()) {
//            Record record = result.next();
//            return this.playSong(record.get("filepath").asString());
//        }
//        return null;
//    }
//
//    public void dislikeSong(Integer id, String username) {
//        this.session.run("match (u:User)-[like:LIKES]->(s:Song) where ID(s)={id} and u.username={username} delete like;",
//                parameters("id", id, "username", username));
//    }
//}