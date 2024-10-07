import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Queue;

public class gui extends JFrame {
    MusicPlayer myMusicPlayer;
    JLabel currentSongLabel;
    JProgressBar progressBar;

    DefaultListModel<String> queueListModel;
    JList<String> songQueueList;

    DefaultListModel<String> previousQueueListModel;
    JList<String> previousSongQueueList;
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

        // label to show the current song
        currentSongLabel = new JLabel("No song playing.", JLabel.CENTER);
        currentSongLabel.setForeground(Color.white);

        // song queue display
        queueListModel = new DefaultListModel<>();
        songQueueList = new JList<>(queueListModel);
        songQueueList.setVisibleRowCount(5);
        songQueueList.setFixedCellHeight(25);
        songQueueList.setFixedCellWidth(300);
        JScrollPane songQueueScrollPane = new JScrollPane(songQueueList);

        // previous song queue display
        previousQueueListModel = new DefaultListModel<>();
        previousSongQueueList = new JList<>(previousQueueListModel);
        previousSongQueueList.setVisibleRowCount(3);
        previousSongQueueList.setFixedCellHeight(25);
        previousSongQueueList.setFixedCellWidth(300);
        JScrollPane previousQueueScrollPane = new JScrollPane(previousSongQueueList);

        // button to load songs from a folder
        JButton loadSongsButton = new JButton("Load Songs from Folder");
        loadSongsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY); // only allow folder selection
                int result = fileChooser.showOpenDialog(middlePanel);

                if (result == JFileChooser.APPROVE_OPTION) {
                    File folder = fileChooser.getSelectedFile();
                    try {
                        myMusicPlayer.loadSongsFromFolder(folder);
                    } catch (LineUnavailableException ex) {
                        throw new RuntimeException(ex);
                    } catch (InterruptedException ex) {
                        throw new RuntimeException(ex);
                    }
                    //myMusicPlayer.playNextSong();
                }

            }
        });

        // button to play next song
        ImageIcon playNextIcon = new ImageIcon(getClass().getResource("/icons/skipForward.png"));

        JButton playNextButton = new JButton();
        playNextButton.setIcon(playNextIcon);
        playNextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    myMusicPlayer.playNextSong();
                } catch (LineUnavailableException ex) {
                    throw new RuntimeException(ex);
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        // create play/pause button
        ImageIcon playIcon = new ImageIcon(getClass().getResource("/icons/playButton.png"));
        ImageIcon pauseIcon = new ImageIcon(getClass().getResource("/icons/pauseButton.png"));

        JButton pausePlayButton = new JButton();
        pausePlayButton.setIcon(pauseIcon);
        pausePlayButton.addActionListener(e -> {
            if (myMusicPlayer.status == "play") {
                myMusicPlayer.pause(); // Pause the audio
                pausePlayButton.setIcon(playIcon);
            } else if (myMusicPlayer.status.equals("paused")){
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

        // create playPrevious song button
        ImageIcon previousIcon = new ImageIcon(getClass().getResource("/icons/skipBack.png"));

        final JButton playPreviousButton = new JButton();
        playPreviousButton.setIcon(previousIcon);

        // when user clicks button a choice is made
        playPreviousButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    myMusicPlayer.playPreviousSong();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                } catch (LineUnavailableException ex) {
                    throw new RuntimeException(ex);
                } catch (UnsupportedAudioFileException ex) {
                    throw new RuntimeException(ex);
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
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

        // create a progress bar with a min of 0 and max of 100
        progressBar = new JProgressBar(0, 100);
        progressBar.setValue(0);
        progressBar.setStringPainted(true);

        // add elements to panels
        topPanel.add(loadSongsButton, BorderLayout.NORTH);
        topPanel.add(previousQueueScrollPane, BorderLayout.CENTER);
        topPanel.add(songQueueScrollPane, BorderLayout.SOUTH);
        middlePanel.add(progressBar, BorderLayout.NORTH);
        middlePanel.add(currentSongLabel, BorderLayout.CENTER);
        middlePanel.add(volume, BorderLayout.SOUTH);
        bottomPanel.add(playPreviousButton);
        bottomPanel.add(pausePlayButton);
        bottomPanel.add(playNextButton);
        bottomPanel.add(restartButton);

        setVisible(true);
    }

    public void updateSongQueue(Queue<song> songQueue){
        queueListModel.clear();
        for(song s : songQueue){
            queueListModel.addElement(s.getTitle());
        }
    }

    public void updatePreviousSongQueue(Queue<song> previousSongsQueue){
        previousQueueListModel.clear();
        for(song s : previousSongsQueue){
            previousQueueListModel.addElement(s.getTitle());
        }
    }

    public void setNowPlaying(String myLabel){
        currentSongLabel.setText(myLabel);
    }

}