import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class UserSettings {
    private Properties properties;
    private final String settingsFilePath = "user_settings.properties";
    private float lastVolume;

    public UserSettings(){
        properties = new Properties();
        loadSettings();
    }

    // load settings from properties file
    private void loadSettings(){
        try(FileInputStream input = new FileInputStream(settingsFilePath)){
            properties.load(input);
        } catch (IOException e){
            System.out.println("No settings file found, starting with default settings");
        }
    }

    // save settings to properties file
    public void saveSettings(){
        try(FileOutputStream output = new FileOutputStream(settingsFilePath)){
            properties.store(output, "User Settings");
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    // get the last selected folder path
    public String getLastFolder(){
        return properties.getProperty("lastFolder", "");
    }

    // set last folder
    public void setLastFolder(String folder) {
        properties.setProperty("lastFolder", folder);
        saveSettings();
    }

    // get the last volume setting
    public float getLastVolume(){
        return Float.parseFloat(properties.getProperty("lastVolume", "0.0f"));
    }

    // set last volume
    public void setLastVolume(float volume) {
        properties.setProperty("lastVolume", String.valueOf(volume));
        saveSettings();
    }
}
