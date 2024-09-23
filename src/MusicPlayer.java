import java.io.File;
import java.io.IOException;
import javax.sound.sampled.*;
import javax.swing.*;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Queue;

public class MusicPlayer {
    // create songQueue
    private static Queue<song> songQueue;

    // create timer
    private Timer timer;

    // current position
    Long currentFrame;
    Clip clip;
    long clipLength;

    // volume
    FloatControl musicGainControl;
    float currentMusicVolume;

    // status of clip
    public String status;

    // create audio input stream
    AudioInputStream audioInputStream;

    // create filePath
    private String filePath;
    private gui GUI;

    // initialize streams and clip
    public MusicPlayer()
            throws UnsupportedAudioFileException,
            IOException, LineUnavailableException {
        // initialize filePath
        filePath = "Error";

        // initialize sonqQueue
        songQueue = new LinkedList<>();
    }

    public void addGui(gui addedGui) {
        GUI = addedGui;
    }
    // add a song to the queue
    public static void addSong(song song) {
        songQueue.offer(song);
        System.out.println(song.getTitle() + " added to the queue.");
    }

    // play next song in queue
    public void playNextSong() throws LineUnavailableException, InterruptedException {
        // stop and close current clip if song is playing
        if(clip != null && clip.isRunning()){
            clip.stop();
            clip.close();
        }

        song nextSong = songQueue.poll();
        if (nextSong != null) {
            System.out.println("Now playing: " + nextSong);
            filePath = nextSong.getAudioFile().getAbsolutePath();
            playAudio(nextSong);
        } else {
            System.out.println("No more songs in the queue.");
        }
    }

    // view next song in queue
    public song viewNextSong() {
        return songQueue.peek();
    }

    // load songs from folder
    public void loadSongsFromFolder(File folder) throws LineUnavailableException, InterruptedException {

        File[] files = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".wav"));

        if (files != null) {
            for (File file : files) {
                // add each song to queue
                String fileName = file.getName();
                MusicPlayer.addSong(new song(fileName, "Unknown Artist", 0, file.getAbsolutePath()));
            }
            playNextSong();
        } else {
            System.out.println("Files were null");
        }

    }

    // play audio
    public void playAudio(song song) throws LineUnavailableException, InterruptedException {
        File audioFile = song.getAudioFile();
        try {
            // create audioinputstream from audio file
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);

            // get a sound clip
            clip = AudioSystem.getClip();

            // open audio clip and load samples from audioinputstream
            clip.open(audioStream);

            // Add a listener to detect when the clip reaches the end
            clip.addLineListener(event -> {
                if (!Objects.equals(status, "play")|| !Objects.equals(status, "paused") ) {
                    if (event.getType() == LineEvent.Type.STOP) {
                        System.out.println("Playback completed.");
                        try {
                            playNextSong();
                        } catch (LineUnavailableException e) {
                            throw new RuntimeException(e);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            });

            GUI.setNowPlaying(song.getTitle() + "-" + song.getArtist());
            //GUI.setNowPlaying(song.toString());

            // play the clip
            play();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    // play the audio
    public void play() {
        // set clip volume
        musicGainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
        musicGainControl.setValue(currentMusicVolume);

        // get length of the clip
        clipLength = clip.getMicrosecondLength();

        // play clip
        clip.start();

        // start a timer to update progress via progressListener
        timer = new Timer(100, e -> updateProgress());
        timer.start();

        // set status to play
        status = "play";
    }

    // pause the audio
    public void pause() {
        if (status.equals("paused")) {
            System.out.println("audio is already paused");
            return;
        }
        clip.stop();
        status = "paused";
    }

    // restart the audio
    public void restart()
            throws IOException, LineUnavailableException, UnsupportedAudioFileException {
        clip.stop();
        clip.close();
        resetAudioStream();
        currentFrame = 0L;
        clip.setMicrosecondPosition(0);
        this.play();
    }

    // stop the audio
    public void stop()
            throws IOException, LineUnavailableException, UnsupportedAudioFileException {
        currentFrame = 0L;
        clip.stop();
        clip.close();
    }

    // jump to part
    public void jump(long choice)
            throws IOException, LineUnavailableException, UnsupportedAudioFileException {
        if (choice > 0 && choice < clip.getMicrosecondLength()) {
            clip.stop();
            clip.close();
            resetAudioStream();
            currentFrame = choice;
            clip.setMicrosecondPosition(choice);
            this.play();
        }
    }

    // change volume
    public void changeVolume(float newVolume) throws IllegalArgumentException {
        musicGainControl.setValue(newVolume);
        currentMusicVolume = newVolume;
        System.out.println("MP volume changed: " + newVolume);
    }

    public void updateProgress(){
        long currentPos = clip.getMicrosecondPosition();
        int progress = (int) ((currentPos * 100) / clipLength);

        GUI.progressBar.setValue(progress);
    }

    // reset audio stream
    public void resetAudioStream()
            throws IOException, LineUnavailableException, UnsupportedAudioFileException {
        audioInputStream = AudioSystem.getAudioInputStream(new File(filePath).getAbsoluteFile());
        clip = AudioSystem.getClip();
        clip.open(audioInputStream);
        status = "play";
    }
}