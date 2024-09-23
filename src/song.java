import java.io.File;

public class song {
    private String title;
    private String artist;
    private int duration;
    private File audioFile;

    public song(String title, String artist, int duration, String filePath){
        this.title = title;
        this.artist = artist;
        this.duration = duration;
        this.audioFile = new File(filePath);
    }

    public String getTitle(){
        return title;
    }

    public String getArtist(){
        return artist;
    }

    public File getAudioFile(){
        return audioFile;
    }

    public String toString(){
        return title + " - " + artist + " (" + duration + " sec)";
    }
}
