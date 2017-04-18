package server;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by sampastoriza on 4/16/17.
 * Might want a directory listener later
 */

public class AudioStream implements Runnable {
    private Socket client;
    private String songsDir = "./AudioStreaming/songs";
    private File currentSong;
    private List<String> songs;
    private FileInputStream in = null;

    public AudioStream(Socket client) {
        this.client = client;
        this.songs = new ArrayList<>();
    }

    @Override
    public void run() {
        this.playStream();
    }

    public void playStream() {
        File f = new File(this.songsDir);
        for (File file : f.listFiles()) {
            if(file.getName().endsWith(".wav")) {
                songs.add(file.getAbsolutePath());
            }
        }
        this.shuffleSongs();
    }

    public void shuffleSongs() {
        // Random for now
        while(true) {
            this.playNextSong();
        }
    }

    public void playSong(String song) {
        try {
            this.currentSong = AudioUtil.getSoundFile(song);
            OutputStream out = this.client.getOutputStream();
            this.in = new FileInputStream(this.currentSong);
            byte buffer[] = new byte[2048];
            int count;
            while ((count = this.in.read(buffer)) != -1)
                out.write(buffer, 0, count);
        } catch (IOException e) {
            System.out.println();
            e.printStackTrace();
        }
    }

    public void playNextSong() {
        try {
            if (this.in != null) {
                this.in.close();
            }
            Random r = new Random();
            int nextSongIndex = r.nextInt(songs.size());
            String song = this.songs.get(nextSongIndex);
            this.playSong(song);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}