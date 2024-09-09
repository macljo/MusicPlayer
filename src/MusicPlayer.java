import java.io.File;
import java.io.IOException;

import javax.sound.sampled.*;
import javax.swing.event.ChangeEvent;

public class MusicPlayer {
    // current position
    Long currentFrame;
    Clip clip;
    FloatControl musicGainControl;
    float currentMusicVolume;

    // status of clip
    String status;

    AudioInputStream audioInputStream;

    private String filePath;

    // initialize streams and clip
    public MusicPlayer()
        throws UnsupportedAudioFileException,
            IOException, LineUnavailableException
    {
        // initialize filePath
        filePath = "Error";
    }

    public void setFilePath(String passedFilePath) throws UnsupportedAudioFileException, IOException, LineUnavailableException {
        filePath = passedFilePath;
        resetAudioStream();
        musicGainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
        float currentMusicVolume = 1;
    }

    // play the audio
    public void play(){
        // start clip
        clip.start();

        status = "play";
    }

    // play the audio
    public void play(Long startingPosition){
        // start clip
        clip.setMicrosecondPosition(startingPosition);
        clip.start();

        status = "play";
    }

    // pause the audio
    public void pause(){
        if(status.equals("paused")){
            System.out.println("audio is already paused");
            return;
        }
        clip.stop();
        status = "paused";
    }

    // resume the audio
    public void resumeAudio()
        throws IOException, LineUnavailableException, UnsupportedAudioFileException
    {
        if(status.equals("play"))
        {
            System.out.println("Audio is already being played");
            return;
        }
        clip.start();
    }

    // restart the audio
    public void restart()
        throws IOException, LineUnavailableException, UnsupportedAudioFileException
    {
        clip.stop();
        clip.close();
        resetAudioStream();
        currentFrame = 0L;
        clip.setMicrosecondPosition(0);
        this.play();
    }

    // stop the audio
    public void stop()
        throws IOException, LineUnavailableException, UnsupportedAudioFileException
    {
        currentFrame = 0L;
        clip.stop();
        clip.close();
    }

    // jump to part
    public void jump(long choice)
        throws IOException, LineUnavailableException, UnsupportedAudioFileException
    {
        if (choice > 0 && choice < clip.getMicrosecondLength())
        {
            clip.stop();
            clip.close();
            resetAudioStream();
            currentFrame = choice;
            clip.setMicrosecondPosition(choice);
            this.play();
        }
    }

    public void changeVolume(float newVolume) throws IllegalArgumentException{
        musicGainControl.setValue(newVolume);
        currentMusicVolume = newVolume;
        System.out.println("MP volume changed: "+ newVolume);
    }

    // reset audio stream
    public void resetAudioStream()
        throws IOException, LineUnavailableException, UnsupportedAudioFileException
    {
        audioInputStream = AudioSystem.getAudioInputStream(new File(filePath).getAbsoluteFile());
        clip = AudioSystem.getClip();
        clip.open(audioInputStream);
        clip.loop(Clip.LOOP_CONTINUOUSLY);
        status = "play";
    }
}