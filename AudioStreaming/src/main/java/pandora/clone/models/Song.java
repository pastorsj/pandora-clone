package pandora.clone.models;

/**
 * Created by sampastoriza on 4/21/17.
 */
public class Song {

    private int id = 0;
    private String artist = "";
    private String year = "";
    private String album = "";
    private String genre = "";
    private String title = "";
    private String track = "";
    private int duration = 0;
    private boolean liked = false;

    public Song() {
    }

    public Song(int id, String artist, String year, String album, String genre, String title, String track, int duration, boolean liked) {
        this.id = id;
        this.artist = artist;
        this.year = year;
        this.album = album;
        this.genre = genre;
        this.title = title;
        this.track = track;
        this.duration = duration;
        this.liked = liked;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setTrack(String track) {
        this.track = track;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setLiked(boolean liked) {
        this.liked = liked;
    }

    public boolean getLiked() {
        return this.liked;
    }

    public int getId() {
        return this.id;
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

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
}
