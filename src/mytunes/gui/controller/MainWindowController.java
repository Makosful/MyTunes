package mytunes.gui.controller;

import com.jfoenix.controls.*;
import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
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

    private Pane mediaPane;
    private MediaPlayer mPlayer;
    private boolean isPlaying;
    private boolean isLooping;

    private final MainWindowModel wm = new MainWindowModel();
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

        if (listLoadedMP3.getItems().isEmpty())
            System.out.println("List of Loaded MP3's is empty.");
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
        volSlide.setValue(mPlayer.getVolume() * 100);

        volSlide.valueProperty().addListener((javafx.beans.Observable observable) ->
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
    private void LoadMP3Multi(ActionEvent event)
    {
        FileChooser fc = new FileChooser();
        fc.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("MP3 Files", "*.mp3"));

        List<File> chosenFiles = fc.showOpenMultipleDialog(null);
        if (chosenFiles != null)
            for (int i = 0; i < chosenFiles.size(); i++)
                listLoadedMP3.getItems().add(chosenFiles.get(i).getAbsolutePath());
        else
            System.out.println("One or more invalid file(s) / None selected");
        mediaPlayerSetup();
    }

    @FXML
    private void clearLoadedMP3(ActionEvent event)
    {
        listLoadedMP3.getItems().clear();
    }
}
