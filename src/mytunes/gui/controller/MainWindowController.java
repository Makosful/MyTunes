package mytunes.gui.controller;

import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXSlider;
import com.jfoenix.controls.JFXToggleButton;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import mytunes.be.Music;
import mytunes.be.Playlist;
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
    //</editor-fold>

    // Model
    private MainWindowModel wm;

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
             * Sets up the auto adjusting width of the table to adapt to the
             * content
             * of the cell
             * Currently disabled, enable to automatically set the width of the
             * columns to fit our tables width
             */
            //tblSongList.setColumnResizePolicy((param) -> true);
            //Platform.runLater(() -> resizeCellWidth(tblSongList));
            // Sets up the mouse listener for the tableview
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

            txtTableSearch.textProperty().addListener(
                    (ObservableValue<? extends String> observable,
                     String oldText,
                     String newText) ->
            {
                try
                {
                    wm.songSearch(txtTableSearch.getText(), wm.getFilters(searchTagTitle,
                                                                          searchTagAlbum,
                                                                          searchTagArtist,
                                                                          searchTagGenre,
                                                                          searchTagDesc,
                                                                          searchTagYear));
                }
                catch (SQLException ex)
                {
                    System.out.println(ex.getMessage());
                }
            });
        }
        catch (SQLException ex)
        {
            System.out.println(ex.getMessage());
        }

        // Buttons
//        btnPlayPause.textProperty().bind(wm.getPlayButtonTextProperty());
        btnLoop.disableProperty().bind(wm.getLoopDisableProperty());

        // ComboBox
        playbackSpeed.disableProperty().bind(wm.getPlaybackSpeedDisabledProperty());

        // Sliders
        progressSlider.valueProperty().bind(wm.getProgressSliderValueProperty());
        volumeSlider.disableProperty().bind(wm.getVolumeDisableProperty());

        // Labels
        lblmPlayerStatus.textProperty().bind(wm.getMediaplayerLabelTextProperty());
        lblTimer.disableProperty().bind(wm.getTimerDisableProperty());
        this.lblAlbumCurrent.textProperty().bind(wm.getCurrentAlbumProperty());
        this.lblArtistCurrent.textProperty().bind(wm.getCurrentArtistProperty());
        this.lblDescriptionCurrent.textProperty().bind(wm.getCurrentDescProperty());
        this.lblDurationCurrent.textProperty().bind(wm.getCurrentDurationProperty());
        this.lblGenreCurrent.textProperty().bind(wm.getCurrentGenreProperty());
        this.lblTitleCurrent.textProperty().bind(wm.getCurrentTitleProperty());
        this.lblYearCurrent.textProperty().bind(wm.getCurrentYearProperty());

        // Loads the queue list
        listQueue.setItems(wm.getQueueList());

        // Sets up a mouse listener for the queue
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

        // Sets up change listener for the queue list
        wm.setupQueueListener();

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
        // Creates a new context menu
        ContextMenu plcm = wm.playlistContextMenu(playlistPanel);

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
                plcm.show(playlistPanel, event.getScreenX(), event.getScreenY());
            }
        });

        // Places the playback functionality at the very front of the application
        volumeSlider.getParent().getParent().toFront();

        // Sets the progress sliders width to be dynamic relative to the width
        progressSlider.prefWidthProperty().bind(
                playbackPanel.widthProperty().subtract(730));

        txtTableSearch.prefWidthProperty().bind(
                searchBar.widthProperty().subtract(40));
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
        wm.fxmlLoopAction();
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
        wm.fxmlLoadMediaFiles(tblSongList);
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
        wm.fxmlClearQueue();
    }

    @FXML
    private void prevSong(ActionEvent event)
    {
        wm.fxmlPrevSong();
    }

    @FXML
    private void nextSong(ActionEvent event)
    {
        wm.fxmlNextSong();
    }

    @FXML
    private void searchClear(ActionEvent event)
    {
        txtTableSearch.clear();
    }
    //</editor-fold>
}
