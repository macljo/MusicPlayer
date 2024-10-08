import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;

public class app {
    static MusicPlayer myMusicPlayer;
    static UserSettings userSettings;

    public static void main(String[] args) throws UnsupportedAudioFileException, LineUnavailableException, IOException, InterruptedException {
        userSettings = new UserSettings();
        myMusicPlayer = new MusicPlayer();
        gui myUi = new gui(myMusicPlayer);
        myMusicPlayer.addGui(myUi);
        myMusicPlayer.loadPreviousSettings(userSettings.getLastFolder(), userSettings.getLastVolume());
    }
}
