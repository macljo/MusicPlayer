import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class app {
    public static String filePath;
    static MusicPlayer myMusicPlayer;

    public static void main(String[] args) throws UnsupportedAudioFileException, LineUnavailableException, IOException {
        myMusicPlayer = new MusicPlayer();
        gui screen = new gui(myMusicPlayer);
        Scanner scan = new Scanner(System.in);
        // get folder directory
        String folderPath = "C:\\Users\\haddo_meecmhc\\Music\\musicPlayer";
        
        // create object for directory
        File directory = new File(folderPath);

        // get files in directory
        File[] files = directory.listFiles();

        if(files != null){
            for(File file : files){
                System.out.println(file.getName());
            }
        }

        //choose audio to play
        System.out.println("Choose which song to play based on number (0 is the first song, etc.)");
        int song = scan.nextInt();

        try{
            // chose the song in the folder
            filePath = String.valueOf(files[song]);
            myMusicPlayer.setFilePath(filePath);

            myMusicPlayer.play();

            scan.close();
        }
        catch (Exception ex){
            System.out.println("Error with playing the audio");
            ex.printStackTrace();
        }
    }
}
