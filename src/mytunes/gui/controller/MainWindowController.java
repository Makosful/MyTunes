package mytunes.gui.controller;

import com.jfoenix.controls.*;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.util.Duration;
import mytunes.be.Music;
import mytunes.be.Playlist;
import mytunes.bll.exception.BLLException;
import mytunes.gui.model.MainWindowModel;

/**
 *
 * @author Axl
 */
public class MainWindowController implements Initializable
{

    //<editor-fold defaultstate="collapsed" desc="FXML Variables">
    @FXML
    private Slider progressSlider;
    @FXML
    private JFXSlider volumeSlider;
    @FXML
    private JFXListView<Music> listQueue;
    @FXML
    private JFXToggleButton btnLoop;
    @FXML
    private JFXListView<Playlist> playlistPanel;
    @FXML
    private Label lblTimer;
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
    private FlowPane playbackPanel;
    @FXML
    private Label lblmPlayerStatus;
    @FXML
    private TextField txtTableSearch;
    @FXML
    private JFXCheckBox searchTagTitle;
    @FXML
    private JFXCheckBox searchTagArtist;
    @FXML
    private JFXCheckBox searchTagAlbum;
    @FXML
    private JFXCheckBox searchTagDesc;
    @FXML
    private JFXCheckBox searchTagYear;
    @FXML
    private JFXCheckBox searchTagGenre;
    @FXML
    private Label lblArtist;
    @FXML
    private Label lblTitle;
    @FXML
    private Label lblAlbum;
    @FXML
    private Label lblYear;
    @FXML
    private Label lblDescription;
    @FXML
    private Label lblGenre;
    @FXML
    private Label lblDuration;
    @FXML
    private HBox searchBar;
    @FXML
    private Label lblArtistCurrent;
    @FXML
    private Label lblTitleCurrent;
    @FXML
    private Label lblAlbumCurrent;
    @FXML
    private Label lblYearCurrent;
    @FXML
    private Label lblDescriptionCurrent;
    @FXML
    private Label lblGenreCurrent;
    @FXML
    private Label lblDurationCurrent;
    @FXML
    private JFXButton btnPlayPause;
    //</editor-fold>

    // Model
    private MainWindowModel wm;
    @FXML
    private AnchorPane anchorPane;

    /**
     * Constructor, for all intends and purposes
     *
     * In the initialize we set all the methods we wish to initialize as soon as
     * the program launches - that is - before the user has had any chance to
     * tweak it.
     * In this case we simply disable the ability to manipulate the Media Player
     * (as it hasn't been loaded yet) and instantiates our Model as well as all
     * our Setup methods, which
     * we split up into different categories. Finally we make sure the volume
     * slider is at the front so it goes over panes and such (will provide
     * difference as a video).
     *
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        //instantiates our Model class
        wm = new MainWindowModel();

        //Sets the default state of the GUI elements
        wm.setPlaying(false);
        volumeSlider.setValue(100);
        volumeSlider.setDisable(true);
        btnLoop.setDisable(true);
        playbackSpeed.setDisable(true);
        progressSlider.setDisable(true);
        lblTimer.setDisable(true);
        //Forces the table to adapt its height to the list (no empty rows)
        tblSongList.setFixedCellSize(30);
        
        wm.getAnchorPaneController(anchorPane);

        //<editor-fold defaultstate="collapsed" desc="Bindinggs">
        // Buttons
        btnPlayPause.textProperty().bind(wm.getPlayPauseButton());
        btnLoop.disableProperty().bind(wm.getLoopDisableProperty());

        // ComboBox
        playbackSpeed.disableProperty().bind(wm.getPlaybackSpeedDisabledProperty());

        // Sliders
        progressSlider.valueProperty().bind(wm.getProgressSliderValueProperty());
        progressSlider.disableProperty().bind(wm.getProgressSliderDisableProperty());
        progressSlider.maxProperty().bind(wm.getProgressSliderMaxProperty());
        volumeSlider.disableProperty().bind(wm.getVolumeDisableProperty());

        // Labels
        lblmPlayerStatus.textProperty().bind(wm.getMediaplayerLabelTextProperty());
        lblTimer.disableProperty().bind(wm.getTimerDisableProperty());
        lblTimer.textProperty().bind(wm.getTimerTextProperty());
        lblAlbum.textProperty().bind(wm.getAlbumProperty());
        lblAlbumCurrent.textProperty().bind(wm.getCurrentAlbumProperty());
        lblArtist.textProperty().bind(wm.getArtistProperty());
        lblArtistCurrent.textProperty().bind(wm.getCurrentArtistProperty());
        lblDescription.textProperty().bind(wm.getDescProperty());
        lblDescriptionCurrent.textProperty().bind(wm.getCurrentDescProperty());
        lblDuration.textProperty().bind(wm.getDurationProperty());
        lblDurationCurrent.textProperty().bind(wm.getCurrentDurationProperty());
        lblGenre.textProperty().bind(wm.getGenreProperty());
        lblGenreCurrent.textProperty().bind(wm.getCurrentGenreProperty());
        lblTitle.textProperty().bind(wm.getTitleProperty());
        lblTitleCurrent.textProperty().bind(wm.getCurrentTitleProperty());
        lblYear.textProperty().bind(wm.getYearProperty());
        lblYearCurrent.textProperty().bind(wm.getCurrentYearProperty());
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Table Setup">
        // Sets the Table colum IDs
        clmNr.setCellValueFactory(new PropertyValueFactory("number"));
        clmTitle.setCellValueFactory(new PropertyValueFactory("title"));
        clmArtist.setCellValueFactory(new PropertyValueFactory("artist"));
        clmCover.setCellValueFactory(new PropertyValueFactory("album"));
        clmYear.setCellValueFactory(new PropertyValueFactory("year"));

        try
        {
            // Attempts to laod the list of songs
            wm.loadSongList();
            tblSongList.setItems(wm.getSongList());
        }
        catch (BLLException ex)
        {
            System.out.println(ex.getMessage());
        }

        // Defines the defautl sorted order
        tblSongList.getSortOrder().add(clmCover);
        tblSongList.getSortOrder().add(clmNr);

        // Allows for multiple entries to be selected at once
        tblSongList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        wm.setTableMouseListener(tblSongList);

        wm.setTableSearchListener(txtTableSearch, wm.getFilters(searchTagTitle,
                                                                searchTagAlbum,
                                                                searchTagArtist,
                                                                searchTagGenre,
                                                                searchTagDesc,
                                                                searchTagYear));
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Queue Setup">
        wm.setQueueMouseListener(listQueue);

        // Sets up change listener for the queue list
        wm.setupQueueListener();
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Playlist Setup">
        // Allows for multiple selections to be made
        playlistPanel.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        // Loads the playlist
        wm.loadPlaylists();
        playlistPanel.setItems(wm.getPlaylists());

        wm.setPlaylistMouseListener(playlistPanel);
        //</editor-fold>

        //setting default value of the choicebox
        playbackSpeed.setValue("Default speed");
        //creating possible choices
        playbackSpeed.getItems().addAll(wm.getPlaybackSpeed());

        // Places the playback functionality at the very front of the application
        volumeSlider.getParent().getParent().toFront();

        // Sets the progress sliders width to be dynamic relative to the width
        progressSlider.prefWidthProperty().bind(
                playbackPanel.widthProperty().subtract(730));

        txtTableSearch.prefWidthProperty().bind(
                searchBar.widthProperty().subtract(40));

        dragListener();
    }

    /**
     * Adds a listner to the ProgressSlider
     *
     * Whenever the ProgressSlider has been clicked, it'll unbind the value to
     * allow it to be dragged.
     * When the mouse has been released, it takes the value of where it's at and
     * seeks it in the song, then rebinds itself with the Model's value
     */
    private void dragListener()
    {
        progressSlider.addEventHandler(MouseEvent.MOUSE_PRESSED,
                                       (event) ->
                               {
                                   progressSlider.valueProperty().unbind();
                               });

        progressSlider
                .addEventHandler(MouseEvent.MOUSE_RELEASED,
                                 (event) ->
                         {
                             wm.seek(Duration.seconds(progressSlider.getValue()));
                             progressSlider
                                     .valueProperty()
                                     .bind(wm.
                                             getProgressSliderValueProperty());
                         });
    }

    //<editor-fold defaultstate="collapsed" desc="FXML Callbackls">
    /**
     * Manages the playback speed
     * Associated with the methods setuPlaybackSpeedSettings() listed above
     * where we now manage the selection of the list and adjust the MediaPlayer
     * playback speed accordingly.
     *
     * @param event
     */
    @FXML
    private void playbackSpeed(ActionEvent event)
    {
        wm.fxmlPlaybackSpeed();
    }

    /**
     * Allows the user to stop the media
     * Grabs the current status of the MediaPlayer and then performs actions
     * according to the response.
     * If the Media is playing we stop the player and revert the isPlaying
     * boolean followed by resetting the progress slider.
     *
     * If the status is anything else then nothing will occur...
     */
    @FXML
    private void songStop(ActionEvent event)
    {
        wm.fxmlSongStop();
    }

    /**
     * Allows the user to Play/Pause a media
     * A check on whether to play or pause the music (same button).
     * Initially we check if the queue is empty and if we aren't already playing
     * something, where we will then run some methods to prepare the media to be
     * played and then finally playing it.
     * Otherwise, if we are not playing and the queue is supposedly not empty
     * we will add a listener which will update the sliders which allows for
     * automatic time tracking and allowing the user to manipulate the play time
     * (rewind, drag & drop, fast forward).
     * On the end of the media we stop the player, fetch the next song in queue
     * and count up on a parameter, sets our new mediaplayer to the next song
     * and then finally play the next song
     */
    @FXML
    private void musicPlayPause(ActionEvent event)
    {
        wm.fxmlMusicPlayPause();
    }

    /**
     * Allows for looping a media
     * Allows the user to loop a current song, where we will then continually
     * loop a song, denying it to reach the "on end of media" stage. If the loop
     * is disabled nothing will occur.
     * This is possibly by reverting the boolean in charge of the logic.
     */
    @FXML
    private void LoopAction(ActionEvent event)
    {
        wm.fxmlLoopAction(btnLoop);
    }

    /**
     * Mixes the VolumeSlider
     * Allows the user to manipulate the volume of the MediaPlayer. Works by
     * checking what value the slider currently has, turning it into 100th parts
     * so we can detect a range from 1 --> 100
     * and finally sets the volume of the MediaPlayer to be the exact number of
     * the volume slider.
     */
    @FXML
    private void volumeMixer(MouseEvent event)
    {
        wm.fxmlVolumeMixer();
    }

    /**
     * Creates a new playlist
     * and adds it to the list of playlists
     */
    @FXML
    private void createPlaylist(ActionEvent event) throws SQLException
    {
        wm.createPlaylistWindow();
    }

    /**
     * Removes a playlist
     * and removes it to the list of playlists
     */
    @FXML
    private void deletePlaylist(ActionEvent event)
    {
        wm.fxmlDeletePlaylist(playlistPanel);
    }

    /**
     * Loads multiple MP3 files
     * Firstly we create a new FileChooser and add an mp3 filter to disable
     * all other file formats (saves a lot of time troubleshooting what went
     * wrong) then followingly we create a LIST of files rather than just a
     * file, so we can load in multiple mp3 files. If the list contains
     * items then we will determine their path and put them in the queue.
     * Otherwise the list of files is empty and we determine that there was
     * an error or that none were selected. Lastly we setup the mediaplayer
     * so that we can play the now selected song(s)
     */
    @FXML
    private void LoadMediaFiles(ActionEvent event)
    {
        wm.fxmlLoadMediaFiles();
    }

    /**
     * Clears the queue
     */
    @FXML
    private void clearQueue(ActionEvent event)
    {
        wm.fxmlClearQueue();
    }

    /**
     * Switches to the previous song in the queue
     *
     * @param event
     */
    @FXML
    private void prevSong(ActionEvent event)
    {
        wm.fxmlPrevSong();
    }

    /**
     * Switches to the next song in the queue
     *
     * @param event
     */
    @FXML
    private void nextSong(ActionEvent event)
    {
        wm.fxmlNextSong();
    }

    /**
     * Clears the search bar
     *
     * @param event
     */
    @FXML
    private void searchClear(ActionEvent event)
    {
        txtTableSearch.clear();
    }
    //</editor-fold>
}
