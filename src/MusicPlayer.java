import java.io.File;
import java.io.IOException;
import javax.sound.sampled.*;

public class MusicPlayer {
    // current position
    Long currentFrame;
    Clip clip;

    // volume
    FloatControl musicGainControl;
    float currentMusicVolume;

    // status of clip
    public String status;

    // create audio input stream
    AudioInputStream audioInputStream;

    // create filePath
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

    // change volume
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