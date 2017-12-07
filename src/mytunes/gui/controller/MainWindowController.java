package mytunes.gui.controller;

import com.jfoenix.controls.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicLong;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.media.MediaPlayer.Status;
import javafx.stage.FileChooser;
import mytunes.be.Music;
import mytunes.be.Playlist;
import mytunes.gui.model.MainWindowModel;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.TagException;

/**
 *
 * @author Axl
 */
public class MainWindowController implements Initializable
{

    // Model
    private MainWindowModel wm;

    private Music musicInfo;

    //<editor-fold defaultstate="collapsed" desc="FXML Variables">
    @FXML
    private Slider progressSlider;
    @FXML
    private JFXButton btnPlayPause;
    @FXML
    private JFXSlider volumeSlider;
    @FXML
    private JFXListView<Music> listQueue;
    @FXML
    private JFXListView<String> listMetaData;
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
    private JFXButton btnStop;
    @FXML
    private MenuButton btnSettings;
    @FXML
    private MenuBar menuBar;
    @FXML
    private Menu menuFile;
    @FXML
    private MenuItem loadMP3;
    @FXML
    private MenuItem clearQueueMenu;
    @FXML
    private Menu menuSettings;
    @FXML
    private Menu menuAbout;
    @FXML
    private JFXButton btnCreatePlaylist;
    @FXML
    private JFXButton btnDeletePlaylist;
    @FXML
    private Pane queuePanel;
    @FXML
    private JFXButton btnLoadMP3Multi;
    @FXML
    private JFXButton btnClearMP3;
    @FXML
    private AnchorPane paneEqualizer;
    @FXML
    private GridPane gridEqualizer;
    @FXML
    private Label lblmPlayerStatus;
    @FXML
    private JFXButton btnPrev;
    @FXML
    private JFXButton btnForward;
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
    private AnchorPane anchorPane;
    //</editor-fold>

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

        wm.setPlaying(false);
        volumeSlider.setValue(100);
        volumeSlider.setDisable(true);
        btnLoop.setDisable(true);
        playbackSpeed.setDisable(true);
        progressSlider.setDisable(true);
        lblTimer.setDisable(true);
        //Forces the table to adapt its height to the list (no empty rows) - TODO understand why
        tblSongList.setFixedCellSize(30);
        //tblSongList.prefHeightProperty().bind(Bindings.size(tblSongList.getItems()).multiply(tblSongList.getFixedCellSize()).add(30));

        try
        {
            // Sets up and connects the various lists to the model
            setupSongList();
        }
        catch (SQLException ex)
        {
            System.out.println(ex.getMessage());
        }

        bindFxml();
        setupQueueList();
        setupPlaybackSpeedSettings();
        setupPlaylistPanel();

        // Places the playback functionality at the very front of the application
        volumeSlider.getParent().getParent().toFront();

        // Sets the progress sliders width to be dynamic relative to the width
        progressSlider.prefWidthProperty().bind(
                playbackPanel.widthProperty().subtract(730));

        txtTableSearch.prefWidthProperty().bind(
                searchBar.widthProperty().subtract(40));
    }

    //<editor-fold defaultstate="collapsed" desc="Bindings">
    private void bindFxml()
    {
        // Buttons
        btnPlayPause.textProperty().bind(wm.getPlayButtonTextProperty());
        btnLoop.disableProperty().bind(wm.getLoopDisableProperty());

        // ComboBox
        playbackSpeed.disableProperty().bind(wm.getPlaybackSpeedDisabledProperty());

        // Sliders
        progressSlider.valueProperty().bind(wm.getProgressSliderValueProperty());
        volumeSlider.disableProperty().bind(wm.getVolumeDisableProperty());

        // Labels
        lblmPlayerStatus.textProperty().bind(wm.getMediaplayerLabelTextProperty());
        lblTimer.disableProperty().bind(wm.getTimerDisableProperty());

        // Checkboxes
        searchTagTitle.selectedProperty().bind(wm.getSearchTagArtistSelectProperty());
        searchTagArtist.selectedProperty().bind(wm.getSearchTagArtistSelectProperty());
        searchTagAlbum.selectedProperty().bind(wm.getSearchTagAlbumSelectProperty());
        searchTagGenre.selectedProperty().bind(wm.getSearchTagGenreSelectProperty());
        searchTagDesc.selectedProperty().bind(wm.getSearchTagGenreSelectProperty());
        searchTagYear.selectedProperty().bind(wm.getSearchTagYearSelectProperty());
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Table View Fold">
    /**
     * Sets up the table & list containing all the songs
     */
    private void setupSongList() throws SQLException
    {
        // Sets the table colums ids
        clmNr.setCellValueFactory(new PropertyValueFactory("id"));
        clmTitle.setCellValueFactory(new PropertyValueFactory("title"));
        clmArtist.setCellValueFactory(new PropertyValueFactory("artist"));
        clmCover.setCellValueFactory(new PropertyValueFactory("album"));
        clmYear.setCellValueFactory(new PropertyValueFactory("year"));

        // Loads the list of saved songs from the storage
        wm.loadSongList();

        // Fills the table with all loaded lists
        tblSongList.setItems(wm.getSongList());

        // Defines the defautl sorted order
        tblSongList.getSortOrder().add(clmCover);
        tblSongList.getSortOrder().add(clmNr);

        // Allows for multiple entries to be selected at once
        tblSongList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        /**
         * Sets up the auto adjusting width of the table to adapt to the content
         * of the cell
         * Currently disabled, enable to automatically set the width of the
         * columns to fit our tables width
         */
        //tblSongList.setColumnResizePolicy((param) -> true);
        //Platform.runLater(() -> resizeCellWidth(tblSongList));
        // Sets up the mouse listener for the tableview
        setupTableMouseListner();

        setupTableSearchListener();
    }

    /**
     * Sets a mouse listener on the song table
     *
     * This method sets up a listener, looking for double clicks and right
     * clicks
     */
    private void setupTableMouseListner()
    {
        // Creates a new context menu
        ContextMenu cm = wm.tableContextMenu(tblSongList);

        // Adds the actual listener to the table
        {
            tblSongList.setOnMouseClicked((MouseEvent event) ->
            {
                if (!tblSongList.getSelectionModel().getSelectedItems().isEmpty())
                {
                    System.out.println("Not Empty");
                    // Double click - Single action
                    if (event.getClickCount() == 2)
                    {
                        // Extracts the item that's been clicked on
                        Music item = tblSongList.getSelectionModel()
                                .getSelectedItem();

                        // Adds the selected item to the queue
                        wm.setQueuePlay(item);

                        // Plays the queue
                        wm.prepareAndPlay();
                    }

                    if (event.getClickCount() == 1)
                    {
                        Music item = tblSongList
                                .getSelectionModel().getSelectedItem();

                        lblArtist.setText(item.getArtist());
                        lblTitle.setText(item.getTitle());
                        lblAlbum.setText(item.getAlbum());
                        lblDescription.setText(item.getDescription());
                        lblGenre.setText(item.getGenre());
                        lblYear.setText(String.valueOf(
                                item.getYear()));
                        int[] minSec = wm.getSecondsToMinAndHour(item.getDuration());
                        lblDuration.setText(String.valueOf(minSec[2]
                                                           + ":"
                                                           + minSec[1]
                                                           + ":"
                                                           + minSec[0]));
                    }

                    // Right click - Context Menu
                    if (event.getButton() == MouseButton.SECONDARY)
                    {
                        // Opens the context menu with the top left corner being at the
                        // mouse's position
                        cm.show(tblSongList, event.getScreenX(), event.getScreenY());
                    }
                }
                else
                {
                    System.out.println("Empty");
                }
            });
        }
    }

    /**
     * Creates a listener for the table to search
     *
     * This method sets up a litener for the search field above the song
     * table,
     * which listen for changes to the input. Whenever a change has occured,
     * it'll search the database for results matching the current search
     * criteria (Entered tex and the filters)
     */
    private void setupTableSearchListener()
    {
        txtTableSearch.textProperty().addListener(
                (ObservableValue<? extends String> observable,
                 String oldText,
                 String newText) ->
        {
            try
            {
                wm.songSearch(txtTableSearch.getText(), wm.getFilters());
            }
            catch (SQLException ex)
            {
                System.out.println(ex.getMessage());
            }
        });
    }

    /**
     * Search the song table
     *
     * This method will search the song table whenever a checkbox has been acted
     * on
     *
     * @param event
     */
    @FXML
    private void searchTable(ActionEvent event)
    {
        try
        {
            wm.songSearch(txtTableSearch.getText(), wm.getFilters());
        }
        catch (SQLException ex)
        {
            System.out.println(ex.getMessage());
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Playlist Fold">
    /**
     * Sets up the panel for the playlists
     */
    private void setupPlaylistPanel()
    {
        // Allows for multiple selections to be made
        playlistPanel.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        // Puts the playlists into the view
        playlistPanel.setItems(wm.getPlaylists());

        try
        {
            // Loads the stores playlists
            wm.loadPlaylists();
        }
        catch (SQLException ex)
        {
            System.out.println(ex.getMessage());
        }

        // Sets up am mouse listener for the playlist
        setupPlaylistMouseListener();
    }

    /**
     * Sets up a mouse listener for the playlist list
     *
     * This method sets up and defines a listener for the playlist list. It'll
     * listen for double clicks and right clicks.
     * This method is also responsible for seetting up the actions when these
     * commands are executed
     */
    private void setupPlaylistMouseListener()
    {
        // Creates a new context menu
        ContextMenu cm = wm.playlistContextMenu(playlistPanel);

        // Adds the actual listener to the playlist
        playlistPanel.setOnMouseClicked((MouseEvent event) ->
        {
            // Double click - Single action
            if (event.getClickCount() == 2)
            {
                ObservableList<Music> playlist = playlistPanel
                        .getSelectionModel()
                        .getSelectedItem()
                        .getPlaylist();

                wm.setQueuePlay(playlist);
                wm.prepareAndPlay();
            }

            // Right click - Context Menu
            if (event.getButton() == MouseButton.SECONDARY)
            {
                cm.show(playlistPanel, event.getScreenX(), event.getScreenY());
            }
        });
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Queue List">
    /**
     * Sets the list with the queue
     */
    private void setupQueueList()
    {
        // Loads the queue list
        listQueue.setItems(wm.getQueueList());

        // Sets up a mouse listener for the queue
        setupQueueMouseListener();

        // Sets up change listener for the queue list
        wm.setupQueueListener();
    }

    /**
     * Sets up a mouse listener for the queue list
     *
     * This method will set up a mosue listener for the queue list, which will
     * listen for double clicks and right clicks
     */
    private void setupQueueMouseListener()
    {
        ContextMenu cm = wm.queueContextMenu(listQueue);

        listQueue.setOnMouseClicked((MouseEvent event) ->
        {
            if (event.getButton() == MouseButton.SECONDARY)
            {
                cm.show(listQueue, event.getScreenX(), event.getScreenY());
            }

            if (event.getClickCount() == 2)
            {
                wm.stopMediaPlayer();
                Music selectedItem = listQueue.getSelectionModel().getSelectedItem();
                wm.skipToSong(selectedItem);
                wm.prepareAndPlay();
            }
        });
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Playback Speed Fold">
    /**
     * Handle the settings for the playback
     * In here we simply display the possible playback speeds for the user. May
     * be changed to a slider in the future for full customization
     * We also do a check for our volume sliders disable property and change it
     * to enabled if it hasn't already been.
     */
    private void setupPlaybackSpeedSettings()
    {
        //setting default value of the choicebox
        playbackSpeed.setValue("Default speed");
        //creating possible choices
        playbackSpeed.getItems().addAll("50% speed",
                                        "75% speed",
                                        "Default speed",
                                        "125% speed",
                                        "150% speed",
                                        "175% speed",
                                        "200% speed");
    }

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
        //an int to see where we are in the combobox' index.
        int playbackIndex = playbackSpeed.getSelectionModel().getSelectedIndex();

        // Creating a list starting from 0 + 1 (convert index to number in list)
        System.out.println("the line is #: " + (playbackIndex + 1));

        /*
         * switch case for all the possible playback speeds MAYBE convert to a
         * slider in future instead (free choice and set the speed to the value
         * of the bar)
         */
        wm.setPlayckSpeed(playbackIndex);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Stop | Play/Pause | Loop | Volume Fold">
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
        // Updates the status
        wm.updateStatus();

        // Stores the status as a local variable
        Status status = wm.getMediaStatus();

        // Check if the status is actuall exist
        if (null != status)
        {
            switch (status)
            {
                case PLAYING:
                    System.out.println("Status is: " + status);
                    wm.stopMediaPlayer();
                    wm.setPlaying(false);
                    btnPlayPause.setText("Play");
                    progressSlider.setValue(0.0);
                    break;
                case STOPPED:
                    System.out.println("Status is: " + status);
                    break;
                case PAUSED:
                    wm.updateDuration();
                    progressSlider.setValue(0.0);
                    progressSlider.setMax(wm.getMediaPlayer().getTotalDuration().toSeconds());
                    wm.getMediaPlayerStatus();
                    break;
                default:
                    break;
            }
        }
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
        if (wm.getQueueList().isEmpty() && !wm.isPlaying())
        {
            wm.enableSettings();
            wm.addElevatorMusic();
            wm.prepareSetup();
            wm.startMediaPlayer();
        }
        else if (!wm.isPlaying())
        {
            //Needs to set the BEFORE media is played (apparently?)
            wm.timeChangeListener();
            wm.startMediaPlayer();
            wm.setPlaying(true);
            btnPlayPause.setText("Pause");
            wm.enableSettings();
        }
        // if the boolean is true we shall stop playing, reverse the boolean and edit the buttons text.
        else
        {
            wm.pauseMediaPlayer();
            wm.setPlaying(false);
            btnPlayPause.setText("Play");
        }
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
        wm.reverseLooping();

        // If our loop slide-button is enabled we change the text, set the cycle
        // count to indefinite and reverse the boolean
        if (btnLoop.isSelected() == true)
        {
            btnLoop.setText("Loop: ON");
            wm.setLooping();
            System.out.println("Looping on");
        }
        else if (btnLoop.isSelected() != true)
        {
            btnLoop.setText("Loop: OFF");
            wm.reverseLooping();
            System.out.println("Looping off");
        }
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
        //Creates a new volume slider and sets the default value to 50%
        JFXSlider volSlide = volumeSlider;

        // It was necessary to time it with 100 to be able to receive 100
        // possible positions for the mixer. For each number is a %, so 0 is 0%,
        // 1 is 1% --> 100 is 100%
        volSlide.setValue(wm.getVolume());

        //Adds a listener on an observable in the volume slider, which allows
        //users to tweak the volume of the player.
        volSlide.valueProperty().addListener(
                (javafx.beans.Observable observable) ->
        {
            wm.setVolume(volSlide.getValue() / 100);
        });
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Create Playlist | Delete Playlist | Random Song Fold">
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
        ObservableList<Playlist> selectedItems = playlistPanel
                .getSelectionModel().getSelectedItems();
        wm.deletePlaylists(selectedItems);
    }
    //</editor-fold>

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
        FileChooser fc = new FileChooser();

        FileChooser.ExtensionFilter mp3Filter = new FileChooser.ExtensionFilter("MP3 Files", "*.mp3");
        FileChooser.ExtensionFilter fxmFilter = new FileChooser.ExtensionFilter("FXM Files", "*.fxm");
        FileChooser.ExtensionFilter flvFilter = new FileChooser.ExtensionFilter("FXL Files", "*.flv");
        FileChooser.ExtensionFilter mp4Filter = new FileChooser.ExtensionFilter("MP4 Files", "*.mp4");
        FileChooser.ExtensionFilter wavFilter = new FileChooser.ExtensionFilter("WAV Files", "*.wav");
        FileChooser.ExtensionFilter hlsFilter = new FileChooser.ExtensionFilter("HLS Files", "*.hls");
        FileChooser.ExtensionFilter aiffFilter = new FileChooser.ExtensionFilter("AIF(F) Files", "*.aif", "*.aiff");

        fc.getExtensionFilters().addAll(mp3Filter, fxmFilter, flvFilter, mp4Filter, wavFilter, hlsFilter, aiffFilter);

        List<File> chosenFiles = fc.showOpenMultipleDialog(null);

        if (chosenFiles != null)
        {

            try
            {
                List<Music> addedMusic;
                addedMusic = wm.setMetaData(chosenFiles);
                wm.loadSongList();
                tblSongList.setItems(wm.getSongList());
                wm.getQueueList().addAll(addedMusic);
                wm.prepareSetup();
            }
            catch (InvalidAudioFrameException
                   | IOException
                   | CannotReadException
                   | ReadOnlyFileException
                   | TagException
                   | SQLException ex)
            {
                System.out.println(ex.getMessage());
            }
        }
        else
        {
            System.out.println("One or more invalid file(s) / None selected");
            return;
        }

        if (!wm.getQueueList().isEmpty())
        {
            wm.prepareSetup();
        }
    }

    /**
     * Clears the queue
     * We check if queue is empty, if it is not we force all songs to stop,
     * followed by a method to clear our queue and
     * finally set the isPlaying boolean {
     * }
     *
     * @FXML
     * private void prevSong(ActionEvent event)
     * {
     * }
     *
     * @FXML
     * private void nextSong(ActionEvent event)
     * accordingly and the text of the
     * Play/Pause button
     */
    @FXML
    private void clearQueue(ActionEvent event)
    {
        // Checks if the queue is empty
        if (!wm.getQueueList().isEmpty())
        // If it's not empty, stop all songs from playing
        {
            songStop(event);
        }

        // Clears the queue list
        wm.clearQueueList();

        wm.setPlaying(false);
        btnPlayPause.setText("Play");
    }

    @FXML
    private void progressDrag(MouseEvent event)
    {
    }

    @FXML
    private void prevSong(ActionEvent event)
    {
        wm.stopMediaPlayer();
        wm.skipToPrevSong();
        wm.prepareAndPlay();
    }

    @FXML
    private void nextSong(ActionEvent event)
    {
        wm.stopMediaPlayer();
        wm.skipToNextSong();
        wm.prepareAndPlay();
    }

    @FXML
    private void searchClear(ActionEvent event)
    {
        txtTableSearch.clear();
    }

    private void resizeCellWidth(TableView<Music> tblSongList)
    {
        double cellWidth;

        AtomicLong width = new AtomicLong();
        tblSongList.getColumns().forEach(col ->
        {
            width.addAndGet((long) col.getWidth());
        });

        cellWidth = tblSongList.getWidth();

        if (cellWidth > width.get())
        {
            tblSongList.getColumns().forEach((TableColumn<Music, ?> col) ->
            {
                col.setPrefWidth(col.getWidth() + ((cellWidth - width.get()) / tblSongList.getColumns().size()));
            });
        }
    }

}
