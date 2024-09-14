import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class gui extends JFrame {
    MusicPlayer myMusicPlayer;
    public gui(MusicPlayer player) throws UnsupportedAudioFileException, LineUnavailableException, IOException {
        myMusicPlayer = player;

        // create window
        setSize(600, 1000);
        setLocation(750, 0);
        setTitle("Music Player");
        getContentPane().setBackground(Color.darkGray);
        JPanel topPanel = new JPanel(new BorderLayout());
        JPanel middlePanel = new JPanel(new BorderLayout());
        JPanel bottomPanel = new JPanel(new FlowLayout());

        topPanel.setBackground(Color.darkGray);
        middlePanel.setBackground(Color.darkGray);
        bottomPanel.setBackground(Color.darkGray);

        // add different panels to window
        add(topPanel, BorderLayout.NORTH);
        add(middlePanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        // choose song to play
        JFileChooser fileChoice = new JFileChooser(FileSystemView.getFileSystemView());
        int r = fileChoice.showSaveDialog(null);

        if(r == JFileChooser.APPROVE_OPTION){
            myMusicPlayer.setFilePath(fileChoice.getSelectedFile().getAbsolutePath());
        }

        // create play/pause button
        //ImageIcon playIcon = new ImageIcon("C:\\Users\\haddo_meecmhc\\Pictures\\musicPlayer\\playButton.png");
        //ImageIcon pauseIcon = new ImageIcon("C:\\Users\\haddo_meecmhc\\Pictures\\musicPlayer\\pauseButton.png");

        ImageIcon playIcon = new ImageIcon(getClass().getResource("/icons/playButton.png"));
        ImageIcon pauseIcon = new ImageIcon(getClass().getResource("/icons/pauseButton.png"));

        JButton pausePlayButton = new JButton();
        pausePlayButton.setIcon(pauseIcon);
        pausePlayButton.addActionListener(e -> {
            if (myMusicPlayer.status == "play") {
                myMusicPlayer.pause(); // Pause the audio
                pausePlayButton.setIcon(playIcon);
            } else {
                myMusicPlayer.play(); // Play or resume the audio
                pausePlayButton.setIcon(pauseIcon);
            }
        });


        // create restart button
        final JButton restartButton = new JButton("Restart Song");

        // when user clicks button a choice is made
        restartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    myMusicPlayer.restart();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                } catch (LineUnavailableException ex) {
                    throw new RuntimeException(ex);
                } catch (UnsupportedAudioFileException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        // create stop button
        final JButton stopButton = new JButton("Stop Song");

        // when user clicks button a choice is made
        stopButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    myMusicPlayer.stop();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                } catch (LineUnavailableException ex) {
                    throw new RuntimeException(ex);
                } catch (UnsupportedAudioFileException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        // create jump button
        final JTextField jumpButton = new JTextField(20);
        //getContentPane().add(jumpButton);

        //  when user enters text choice is made
        jumpButton.getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) {
                try {
                    onTextChanged();
                } catch (UnsupportedAudioFileException ex) {
                    throw new RuntimeException(ex);
                } catch (LineUnavailableException ex) {
                    throw new RuntimeException(ex);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                try {
                    onTextChanged();
                } catch (UnsupportedAudioFileException ex) {
                    throw new RuntimeException(ex);
                } catch (LineUnavailableException ex) {
                    throw new RuntimeException(ex);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }

            @Override
            public void changedUpdate(DocumentEvent e) {

            }

            public void onTextChanged() throws UnsupportedAudioFileException, LineUnavailableException, IOException {
                System.out.println("");
                myMusicPlayer.jump(Long.parseLong(jumpButton.getText()));
            }
        });

        // create volume slider settings
        final int volumeMin = -200;
        final int volumeMax = 0;
        final int volumeInit = 0;

        // create volume slider
        JSlider volume = new JSlider(JSlider.HORIZONTAL,volumeMin, volumeMax, volumeInit);

        volume.setPaintTrack(true);
        volume.setPaintTicks(true);

        volume.setMajorTickSpacing(10);
        volume.setMinorTickSpacing(5);

        volume.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if (volume.getValue() != -200) {
                    System.out.println("Volume Changed: " + volume.getValue()/10f);
                    myMusicPlayer.changeVolume(volume.getValue()/10f);
                }
                else
                {
                    myMusicPlayer.changeVolume(-80);
                }
            }
        });

        topPanel.add(stopButton, BorderLayout.CENTER);
        middlePanel.add(volume, BorderLayout.SOUTH);
        bottomPanel.add(pausePlayButton);
        bottomPanel.add(restartButton);

        setVisible(true);
    }

}