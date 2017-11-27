package mytunes.gui.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXSlider;
import com.jfoenix.controls.JFXToggleButton;
import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
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
    private JFXButton btnPlayPause;
    @FXML
    private JFXSlider volumeSlider;
    @FXML
    private JFXListView<String> listQueue;
    @FXML
    private JFXListView<?> listMetaData;
    @FXML
    private JFXToggleButton btnLoop;
    @FXML
    private JFXButton btnLoadMP3Multi;
    @FXML
    private JFXButton btnClearMP3;
    @FXML
    private JFXListView<?> playlistPanel;
    @FXML
    private Pane bottomPane;
    @FXML
    private Pane queuePanel;
    @FXML
    private Pane menuPanel;
    @FXML
    private FlowPane playbackPanel;
    @FXML
    private Label lblTimer;
    @FXML
    private Slider progressSlider;
    @FXML
    private MediaView mediaView;
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
    private MenuItem loadMP3;
    @FXML
    private Menu menuFile;
    @FXML
    private Menu menuSettings;
    @FXML
    private Menu menuAbout;
    // </editor-fold>

    // Instance variables
    private MediaPlayer mPlayer;
    private boolean isPlaying;
    private boolean isLooping;
    private Duration mpduration;

    // Model
    private MainWindowModel wm;

    /**
     * Constructor, for all intends and purposes
     *
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        // Nothing will play when the app first start
        isPlaying = false;

        //instantiates our Model class
        wm = new MainWindowModel();

        //mediaPlayerSetup();
        // Sets up and connects the various lists to the model
        setUpSongList();
        setUpQueueList();
        setUpPlaybackSettings();

        // Places the playback functionality at the very front of the application
        volumeSlider.getParent().getParent().toFront();
    }

    //<editor-fold defaultstate="collapsed" desc="Functionality Setups">
    /**
     * Sets up the table & list containing all the songs
     */
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

    /**
     * Sets the list with the queue
     */
    private void setUpQueueList()
    {
        listQueue.setItems(wm.getQueueList());
    }

    /**
     * Sets up the Media Player
     */
    private void setUpMediaPlayer()
    {
        String musicFile = wm.getQueueList().get(0);
        Media song = new Media(new File(musicFile.toLowerCase()).toURI().toString());

        mPlayer = new MediaPlayer(song);
        mediaView = new MediaView(mPlayer);
        progressSliderSetup(mPlayer);

        mpduration = mPlayer.getMedia().getDuration();

        //As soon as the media player is ready to play a song it'll get it's duration and set up the progress slider
        mPlayer.setOnReady(() ->
        {
            mpduration = mPlayer.getMedia().getDuration();
            updateProgressSlider();
        });
    }

    /**
     * Handle the settings for the playback
     */
    private void setUpPlaybackSettings()
    {
        //setting default value of the choicebox
        playbackSpeed.setValue("Play speed");
        //creating possible choices
        playbackSpeed.getItems().addAll("50% speed",
                                        "75% speed",
                                        "100% speed",
                                        "125% speed",
                                        "150% speed",
                                        "175% speed",
                                        "200% speed");
    }
    //</editor-fold>

    private void progressSliderSetup(MediaPlayer mPlayer)
    {
        //adds a listener to the value, allowing it to determine where to play from when the user drags.
        progressSlider.valueProperty().addListener((Observable ov) ->
        {
            //if the value of the slider is currently 'changing' referring to the listeners task it'll set the value to percentage from the song, where max length = song duration.
            if (progressSlider.isValueChanging())
                mPlayer.seek(mpduration.multiply(progressSlider.getValue() / 100.0));
        });
    }

    private void updateProgressSlider()
    {
        //If our timer label & slider is !null we create a task that controls the progressSlider & timer label
        if (lblTimer != null && progressSlider != null && volumeSlider != null)
            Platform.runLater(new Runnable()
            {
                @Override
                public void run()
                {
                    //grabs the current time of the mPlayer and adjusts the current duration to it so we can update it over time.
                    Duration currentTime = mPlayer.getCurrentTime();
                    //sets the label to the currenttime which we determined above.
                    lblTimer.setText(formatTime(currentTime, mpduration));
                    //Disable the slider until we run the "is this songs length unknown?" method
                    progressSlider.setDisable(mpduration.isUnknown());
                    //if the slider is not disabled and the duration is > 0 milliseconds and the value is not currently changing, then set the value to the current time
                    if (!progressSlider.isDisabled()
                        && mpduration.greaterThan(Duration.ZERO)
                        && !progressSlider.isValueChanging())
                        progressSlider.setValue(currentTime.divide(mpduration).toSeconds()* 100.0);
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
    
    //Sets up a random filler with one of x music files if our mediaplayer has no selected audio to play, thus never getting a nullpointer & also adding some fun (elevator music)
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

    //<editor-fold defaultstate="collapsed" desc="FXML Methods">
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
        if (listQueue.getItems().isEmpty() && isPlaying == false)
            randomFiller();

        else if (listQueue.getItems().isEmpty() && isPlaying == true)
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
    private void LoadMP3Files(ActionEvent event)
    {
        FileChooser fc = new FileChooser();
        fc.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("MP3 Files", "*.mp3"));

        List<File> chosenFiles = fc.showOpenMultipleDialog(null);
        if (chosenFiles != null)
            for (int i = 0; i < chosenFiles.size(); i++)
                listQueue.getItems().add(chosenFiles.get(i).getAbsolutePath());
        else
        {
            System.out.println("One or more invalid file(s) / None selected");
            return;
        }
        setUpMediaPlayer();
    }

    @FXML
    private void clearQueue(ActionEvent event)
    {
        // Checks if the queue is empty
        if (!wm.getQueueList().isEmpty())
            // If it's not empty, stop all songs from playing
            songStop(event);

        // Clears the queue list
        wm.clearQueueList();
    }
    //</editor-fold>
}
