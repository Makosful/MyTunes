package mytunes.gui.controller;

import com.jfoenix.controls.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import mytunes.be.Music;
import mytunes.gui.model.MainWindowModel;

/**
 *
 * @author Axl
 */
public class MainWindowController implements Initializable
{

    // <editor-fold defaultstate="collapsed" desc=" FXML & Variables">
    @FXML
    private JFXButton btnStop;
    @FXML
    private JFXButton btnClearMP3;
    @FXML
    private JFXButton btnLoadMP3Multi;
    @FXML
    private JFXListView<String> listLoadedMP3;
    @FXML
    private Tab tabLoadMP3;
    @FXML
    private Tab tabEqualizer;
    @FXML
    private Pane bottomPane;
    @FXML
    private JFXTabPane tabPane;
    @FXML
    private JFXButton btnPlayPause;
    @FXML
    private JFXSlider volumeSlider;
    @FXML
    private Label lblTimer;
    @FXML
    private Slider progressSlider;
    @FXML
    private JFXListView<?> listMetaData;
    @FXML
    private MediaView mediaView;
    @FXML
    private JFXToggleButton btnLoop;
    @FXML
    private ComboBox<String> playbackSpeed;
    @FXML
    private TableView<Music> tblSongList;
    @FXML
    private TableColumn<Music, String> clmNr;
    @FXML
    private TableColumn<Music, String> clmTitle;
    @FXML
    private TableColumn<Music, String> clmCover;
    @FXML
    private TableColumn<Music, String> clmArtist;
    @FXML
    private TableColumn<Music, String> clmYear;
    @FXML
    private MenuButton btnSettings;
    @FXML
    private JFXListView<?> listQueue;
    @FXML
    private Pane queuePanel;
    @FXML
    private Menu menuFile;
    @FXML
    private MenuItem loadMP3;
    @FXML
    private Menu menuSettings;
    @FXML
    private Menu menuAbout;

    private Pane mediaPane;
    private MediaPlayer mPlayer;
    private boolean isPlaying;
    private boolean isLooping;
    private Duration mpduration;

    private MainWindowModel wm;
    
    private List<File> pathNames;
    // </editor-fold>

    /**
     * Constructor, for all intends and purposes
     *
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        try 
        {
            wm = new MainWindowModel();
        } catch (IOException ex) 
        {
            System.out.println(ex.getMessage());
        }
        
        isPlaying = false;

        //mediaPlayerSetup();
        playbackSettings();

        setUpSongList();

        volumeSlider.getParent().getParent().toFront();
    }

    private void setUpSongList()
    {
        clmNr.setCellValueFactory(new PropertyValueFactory("id"));
        clmTitle.setCellValueFactory(new PropertyValueFactory("title"));
        clmArtist.setCellValueFactory(new PropertyValueFactory("artist"));
        clmCover.setCellValueFactory(new PropertyValueFactory("album"));
        clmYear.setCellValueFactory(new PropertyValueFactory("year"));

        tblSongList.setItems(wm.getSongList());

        tblSongList.getSortOrder().add(clmCover);
        tblSongList.getSortOrder().add(clmNr);

        wm.loadSongList();
    }

    private void mediaPlayerSetup()
    {
        //String musicFile = "src/mytunes/media/elevatormusic.mp3";

        String musicFile = listLoadedMP3.getItems().get(0);
        Media song = new Media(new File(musicFile.toLowerCase()).toURI().toString());

        mPlayer = new MediaPlayer(song);
        mediaView = new MediaView(mPlayer);
        progressSliderSetup(mPlayer);

        mpduration = mPlayer.getMedia().getDuration();

        mPlayer.setOnReady(new Runnable()
        {
            public void run()
            {
                mpduration = mPlayer.getMedia().getDuration();
                updateProgressSlider();
            }
        });

        //mediaPane.getChildren().add(mediaView);
    }

    /**
     * Tells the playback to stop entirely
     *
     * @param event
     */
    @FXML
    private void songStop(ActionEvent event)
    {
        mPlayer.stop();
        isPlaying = false;
        btnPlayPause.setText("Play");

        System.out.println("Music Stopped");
    }

    /**
     * Tells the playback to pause
     *
     * @param event
     */
    @FXML
    private void musicPlayPause(ActionEvent event)
    {
        //Status status = mPlayer.getStatus();

        if (listLoadedMP3.getItems().isEmpty() && isPlaying == false) //System.out.println("List of Loaded MP3's is empty.");

            randomFiller();
        else if (listLoadedMP3.getItems().isEmpty() && isPlaying == true)
        {
            //Do nothing
        }
        else if (isPlaying == false)
        {
            mPlayer.play();
            System.out.println("Music Playing");
            isPlaying = true;
            btnPlayPause.setText("Pause");
        }
        else
        {
            mPlayer.pause();
            System.out.println("Music Paused");
            isPlaying = false;
            btnPlayPause.setText("Play");
        }
    }

    /**
     * Handle the settings for the playback
     */
    private void playbackSettings()
    {
        //setting default value of the choicebox
        playbackSpeed.setValue("Play speed");
        //creating possible choices
        playbackSpeed.getItems().addAll("50% speed", "75% speed", "100% speed", "125% speed", "150% speed", "175% speed", "200% speed");
    }

    /**
     * Handles the playback itself
     *
     * @param event
     */
    @FXML
    private void playbackAction(ActionEvent event)
    {
        int playbackIndex = playbackSpeed.getSelectionModel().getSelectedIndex();

        // Creating a list starting from 0+1 (convert index to number in list)
        System.out.println("the line is #: " + (playbackIndex + 1));

        switch (playbackIndex)
        {
            case 0:
                System.out.println("50%");
                mPlayer.setRate(0.5);
                break;
            case 1:
                System.out.println("75%");
                mPlayer.setRate(0.75);
                break;
            case 2:
                System.out.println("100%");
                mPlayer.setRate(1.0);
                break;
            case 3:
                System.out.println("125%");
                mPlayer.setRate(1.25);
                break;
            case 4:
                System.out.println("150%");
                mPlayer.setRate(1.5);
                break;
            case 5:
                System.out.println("175%");
                mPlayer.setRate(1.75);
                break;
            case 6:
                System.out.println("200%");
                mPlayer.setRate(2.0);
                break;
            default:
                break;
        }
    }

    /**
     * Sets the current song to loop
     *
     * @param event
     */
    @FXML
    private void LoopAction(ActionEvent event)
    {
        if (btnLoop.isSelected() == true)
        {
            btnLoop.setText("Loop: ON");
            mPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            isLooping = false;
            System.out.println("Looping on");
        }
        else if (btnLoop.isSelected() != true)
        {
            btnLoop.setText("Loop: OFF");
            isLooping = true;
            System.out.println("Looping off");
        }
    }

    /**
     * Handles the volume
     *
     * @param event
     */
    @FXML
    private void volumeMixer(MouseEvent event)
    {
        JFXSlider volSlide = volumeSlider;
        volSlide.setValue(50);
        //It was necessary to time it with 100 to be able to receive 100 possible positions for the mixer. For each number is a %, so 0 is 0%, 1 is 1% --> 100 is 100%
        volSlide.setValue(mPlayer.getVolume() * 100);

        volSlide.valueProperty().addListener((javafx.beans.Observable observable)
                ->
        {
            mPlayer.setVolume(volSlide.getValue() / 100);
        });
    }

    /**
     * Will handle the playback duration
     *
     * @param event
     */
    @FXML
    private void progressDrag(MouseEvent event)
    {
        //
    }

    /**
     * Loads multiple MP3 files
     *
     * @param event
     */
    @FXML
    private void LoadMP3Multi(ActionEvent event) throws IOException, SQLException
    {
        FileChooser fc = new FileChooser();
        fc.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("MP3 Files", "*.mp3"));

        List<File> chosenFiles = fc.showOpenMultipleDialog(null);
        

        try {
            wm.setPathAndName(chosenFiles);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        
        if (chosenFiles != null)
            for (int i = 0; i < chosenFiles.size(); i++){
               
              
        
              
                listLoadedMP3.getItems().add(chosenFiles.get(i).getAbsolutePath());
                
        }else
        {
            System.out.println("One or more invalid file(s) / None selected");
            return;
        }
        mediaPlayerSetup();
        pathNames = chosenFiles;
        savePath();
    }

    @FXML
    private void clearLoadedMP3(ActionEvent event)
    {
        listLoadedMP3.getItems().clear();
    }

    private void progressSliderSetup(MediaPlayer mPlayer)
    {
        progressSlider.valueProperty().addListener((Observable ov) ->
        {
            if (progressSlider.isValueChanging())
                mPlayer.seek(mpduration.multiply(progressSlider.getValue() / 100.0));
        });
    }

    private void updateProgressSlider()
    {
        if (lblTimer != null && progressSlider != null && volumeSlider != null)
            Platform.runLater(new Runnable()
            {
                @Override
                public void run()
                {
                    Duration currentTime = mPlayer.getCurrentTime();
                    lblTimer.setText(formatTime(currentTime, mpduration));
                    progressSlider.setDisable(mpduration.isUnknown());
                    if (!progressSlider.isDisabled()
                        && mpduration.greaterThan(Duration.ZERO)
                        && !progressSlider.isValueChanging())
                        progressSlider.setValue(currentTime.divide(mpduration).toMillis()
                                                * 100.0);
                }

                //COPY PASTED CODE FROM https://docs.oracle.com/javase/8/javafx/media-tutorial/playercontrol.htm
                private String formatTime(Duration elapsed, Duration mpduration)
                {
                    int intElapsed = (int) Math.floor(elapsed.toSeconds());
                    int elapsedHours = intElapsed / (60 * 60);
                    if (elapsedHours > 0)
                        intElapsed -= elapsedHours * 60 * 60;
                    int elapsedMinutes = intElapsed / 60;
                    int elapsedSeconds = intElapsed - elapsedHours * 60 * 60
                                         - elapsedMinutes * 60;

                    if (mpduration.greaterThan(Duration.ZERO))
                    {
                        int intDuration = (int) Math.floor(mpduration.toSeconds());
                        int durationHours = intDuration / (60 * 60);
                        if (durationHours > 0)
                            intDuration -= durationHours * 60 * 60;
                        int durationMinutes = intDuration / 60;
                        int durationSeconds = intDuration - durationHours * 60 * 60
                                              - durationMinutes * 60;
                        if (durationHours > 0)
                            return String.format("%d:%02d:%02d/%d:%02d:%02d",
                                                 elapsedHours, elapsedMinutes, elapsedSeconds,
                                                 durationHours, durationMinutes, durationSeconds);
                        else
                            return String.format("%02d:%02d/%02d:%02d",
                                                 elapsedMinutes, elapsedSeconds, durationMinutes,
                                                 durationSeconds);
                    }
                    else if (elapsedHours > 0)
                        return String.format("%d:%02d:%02d", elapsedHours,
                                             elapsedMinutes, elapsedSeconds);
                    else
                        return String.format("%02d:%02d", elapsedMinutes,
                                             elapsedSeconds);
                }
            });
    }

    private void randomFiller()
    {
        String music;

        double rand = Math.random();
        if (rand > 0.66)
            music = "./src/myTunes/media/Elevator (Control).mp3";
        else if (rand > 0.33)
            music = "./src/myTunes/media/Elevator (Caverns).mp3";
        else
            music = "./src/myTunes/media/elevatormusic.mp3";

        Media song = new Media(new File(music.toLowerCase()).toURI().toString());

        mPlayer = new MediaPlayer(song);

        mPlayer.play();
        mediaView = new MediaView(mPlayer);
        isPlaying = true;
        isLooping = true;
    }

     // Saves songs name to database.
    
    private void savePath() throws SQLException 
    {
        List<String> songNamePaths = wm.getPath(pathNames);
        
        for (int i = 0;i<songNamePaths.size();i++)
        {
            wm.createSongPath(songNamePaths.get(i));
        }
    }
}
