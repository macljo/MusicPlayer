import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;

public class app {
    static MusicPlayer myMusicPlayer;

    public static void main(String[] args) throws UnsupportedAudioFileException, LineUnavailableException, IOException {
        myMusicPlayer = new MusicPlayer();
        gui myUi = new gui(myMusicPlayer);
        myMusicPlayer.addGui(myUi);
    }
}
