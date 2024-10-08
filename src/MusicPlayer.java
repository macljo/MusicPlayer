import java.io.File;
import java.io.IOException;
import javax.sound.sampled.*;
import javax.swing.*;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Queue;

public class MusicPlayer {
    // create songQueue
    private static Queue<song> songQueue;

    private static UserSettings userSettings;

    // create previousSongs and limit the number of songs in previousSongs
    private LinkedList<song> previousSongs;
    private static final int maxSongs = 20;

    // create currentSong to track the current song
    private song currentSong;

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
    private static gui GUI;

    // create LineListener
    private LineListener lineListener = event -> {
        if(event.getType() == LineEvent.Type.STOP && status.equals("play")){
            System.out.println("Playback completed");
            try{
                playNextSong();
            } catch (LineUnavailableException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    };

    // initialize streams and clip
    public MusicPlayer()
            throws UnsupportedAudioFileException,
            IOException, LineUnavailableException {
        // initialize filePath
        filePath = "Error";

        // initialize sonqQueue
        songQueue = new LinkedList<>();
        // initialize previousSongs
        previousSongs = new LinkedList<>();

        // initialize status
        status = "paused";

        // set default volume
        userSettings = new UserSettings();
        currentMusicVolume = userSettings.getLastVolume();
    }

    public void addGui(gui addedGui) {
        GUI = addedGui;
    }

    // load previous settings
    public void loadPreviousSettings(String lastFolder, float lastVolume) throws LineUnavailableException, InterruptedException {
        if(!lastFolder.isEmpty()){
            File folder = new File(lastFolder);
            if(folder.exists() && folder.isDirectory()){
                loadSongsFromFolder(folder);
            }
        }
        changeVolume(lastVolume);
    }

    // add a song to the queue
    public static void addSong(song song) {
        songQueue.offer(song);
        System.out.println(song.getTitle() + " added to the queue.");
        if(GUI != null){
            GUI.updateSongQueue(songQueue);
        }
    }

    // update previousQueue
    public void updatePreviousQueueDisplay(){
        if(GUI != null){
            GUI.updatePreviousSongQueue(previousSongs);
        }
    }

    // play next song in queue
    public void playNextSong() throws LineUnavailableException, InterruptedException {
        // stop and close current clip if song is playing
        if(clip != null && clip.isRunning()){
            // before playing next song, add current song to previously played queue
            if(currentSong != null){
                addToPreviouslyPlayed(currentSong);
                updatePreviousQueueDisplay();
            }
            // temporarily disable the listener
            clip.removeLineListener(lineListener);
            clip.stop();
            clip.close();
        }

        song nextSong = songQueue.poll();
        if (nextSong != null) {
            System.out.println("Now playing: " + nextSong.getTitle());
            filePath = nextSong.getAudioFile().getAbsolutePath();
            currentSong = nextSong;
            playAudio(nextSong);
        } else {
            System.out.println("No more songs in the queue.");
        }

        if(GUI != null){
            GUI.updateSongQueue(songQueue);
        }
    }

    // play previous song
    public void playPreviousSong() throws LineUnavailableException, InterruptedException, UnsupportedAudioFileException, IOException {
        // stop current song before switching
        if(clip != null && clip.isRunning()){
            clip.stop();
            clip.close();
        }

        // check if there are previously played songs
        if(!previousSongs.isEmpty()){
            // get last song played from previous queue
            song previousSong = previousSongs.removeLast();

            // add current song back to the main queue (if there is a song playing)
            if(currentSong != null){
                ((LinkedList<song>) songQueue).addFirst(currentSong);
            }

            // update current song to play the previous one and play it
            currentSong = previousSong;
            System.out.println("Now playing previous song: " + currentSong.getTitle());
            filePath = currentSong.getAudioFile().getAbsolutePath();
            playAudio(currentSong);
        }
        else{
            System.out.println("No previous songs available.");
        }

        if(GUI != null){
            GUI.updateSongQueue(songQueue);
            GUI.updatePreviousSongQueue(previousSongs);
        }
    }

    // manage the previously played song queue
    private void addToPreviouslyPlayed(song playedSong){
        if(playedSong != null){
            if(previousSongs.size() >= maxSongs){
                previousSongs.removeFirst();
            }
            previousSongs.addLast(playedSong);
        }
    }

    // get list of previously playedSongs
    public LinkedList<song> getPreviouslyPlayedSongs() {
        return previousSongs;
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

            // initialize the volume control
            musicGainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            // set initial volume
            musicGainControl.setValue(currentMusicVolume);

            // Add a listener to detect when the clip reaches the end
            clip.addLineListener(lineListener);

            // Ensure clip length is initialized properly
            clipLength = clip.getMicrosecondLength();

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
        if(status.equals("paused")){
            clip.addLineListener(lineListener);
            clip.setMicrosecondPosition(clip.getMicrosecondPosition());
            clip.start();
        }
        else{
            // set clip volume
            musicGainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            musicGainControl.setValue(currentMusicVolume);

            // get length of the clip
            clipLength = clip.getMicrosecondLength();

            // play clip
            clip.start();
        }

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
        // Temporarily remove the listener to prevent it from catching STOP event
        clip.removeLineListener(lineListener);
        clip.stop();
        status = "paused";
    }

    // restart the audio
    public void restart()
            throws IOException, LineUnavailableException, UnsupportedAudioFileException {
        // Temporarily remove the listener to prevent STOP event
        clip.removeLineListener(lineListener);

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
        if(musicGainControl != null){
            musicGainControl.setValue(newVolume);
            currentMusicVolume = newVolume;
            System.out.println("MP volume changed: " + newVolume);
        }
        else{
            System.out.println("Volume control not available");
        }

        // save volume settings when it is changed
        userSettings.setLastVolume(newVolume);
    }

    public void updateProgress(){
        if(clipLength > 0){
            long currentPos = clip.getMicrosecondPosition();
            int progress = (int) ((currentPos * 100) / clipLength);

            GUI.progressBar.setValue(progress);
        } else{
            GUI.progressBar.setValue(0);
        }

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