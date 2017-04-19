package server;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.RegexFileFilter;

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
        Collection<File> files = FileUtils.listFiles(new File(this.songsDir), new RegexFileFilter("^(.*.mp3)"), DirectoryFileFilter.DIRECTORY);
        for (File file : files) {
            if (file.getName().endsWith(".mp3")) {
                songs.add(file.getAbsolutePath());
            }
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
            File tmp = new File("tmp.wav");
            AudioInputStream converted = AudioSystem.getAudioInputStream(convertFormat, mp3Stream);
            AudioSystem.write(converted, AudioFileFormat.Type.WAVE, tmp);

            this.currentSong = AudioUtil.getSoundFile("tmp.wav");
            this.out = this.client.getOutputStream();
            this.in = new FileInputStream(this.currentSong);
            byte buffer[] = new byte[1024];
            int count;
            while ((count = this.in.read(buffer)) != -1)
                try {
                    out.write(buffer, 0, count);
                } catch (Exception e) {
                    //e.printStackTrace();
                    break;
                }
            tmp.delete();
        } catch (IOException e) {
            System.out.println();
            //e.printStackTrace();
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
            System.out.println(this.songs.size());
            int nextSongIndex = r.nextInt(this.songs.size());
            String song = this.songs.get(nextSongIndex);
            this.playSong(song);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}