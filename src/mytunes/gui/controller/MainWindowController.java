package mytunes.gui.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXSlider;
import com.jfoenix.controls.JFXToggleButton;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import mytunes.be.Music;
import mytunes.be.Playlist;
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
    private JFXListView<Playlist> playlistPanel;
    @FXML
    private Pane bottomPane;
    @FXML
    private Pane queuePanel;
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
    private MenuBar menuBar;
    @FXML
    private Menu menuFile;
    @FXML
    private Menu menuSettings;
    @FXML
    private Menu menuAbout;
    @FXML
    private MenuItem clearQueueMenu;
    @FXML
    private JFXButton btnCreatePlaylist;
    @FXML
    private JFXButton btnDeletePlaylist;
    // </editor-fold>

    // Instance variables
    private MediaPlayer mPlayer;
    private boolean isPlaying;
    private boolean isLooping;
    private Duration mpduration;
    private Media song;
    private boolean pause;
    private List<File> pathNames;

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
        isPlaying = false;
        volumeSlider.setValue(50);
        volumeSlider.setDisable(true);

        //instantiates our Model class
        wm = new MainWindowModel();

        // Sets up and connects the various lists to the model
        setupSongList();
        setupQueueList();
        setupPlaybackSettings();
        setupPlaylistPanel();

        // Places the playback functionality at the very front of the application
        volumeSlider.getParent().getParent().toFront();
    }

    //<editor-fold defaultstate="collapsed" desc="SETUP Methods">
    /**
     * Sets up the table & list containing all the songs
     */
    private void setupSongList()
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

        // Defines the context menu for the table
        createTableContextMenu();
    }

    private void createTableContextMenu()
    {
        // Creates a new context menu
        ContextMenu cm = new ContextMenu();

        // Creates a new item for the menu and puts it in
        MenuItem play = new MenuItem("Play");
        cm.getItems().add(play);

        // Creates a new item for the menu and puts it in
        MenuItem addQueue = new MenuItem("Add to queue");
        cm.getItems().add(addQueue);

        // Creates a new item for the menu and puts it in
        MenuItem loadSong = new MenuItem("Load Song");
        cm.getItems().add(loadSong);

        // Creates a new item for the menu and puts it in
        MenuItem clearQueueContext = new MenuItem("Clear Queue");
        cm.getItems().add(clearQueueContext);

        MenuItem addToPlaylist1 = new MenuItem("Add to Playlist > add");
        cm.getItems().add(addToPlaylist1);
        MenuItem addToPlaylist2 = new MenuItem("Add to Playlist > too");
        cm.getItems().add(addToPlaylist2);

        tblSongList.setRowFactory(tv ->
        {
            TableRow<Music> row = new TableRow();
            row.setOnMouseClicked(event ->
            {

                if (event.getButton() == MouseButton.SECONDARY)
                {
                    cm.show(tblSongList, event.getScreenX(), event.getScreenY());
                }

                play.setOnAction(action ->
                {
                    ObservableList<Music> selectedItems = tblSongList
                            .getSelectionModel().getSelectedItems();

                    if (!selectedItems.isEmpty())
                    {
                        if (isPlaying)
                        {
                            this.songStop(action);
                        }
                        wm.setQueuePlay(selectedItems);
                        setupMediaPlayer();
                        musicPlayPause(action);
                    }
                });

                addQueue.setOnAction(action ->
                {
                    ObservableList<Music> selectedItems = tblSongList
                            .getSelectionModel().getSelectedItems();

                    if (!selectedItems.isEmpty())
                    {
                        wm.setQueueAdd(selectedItems);
                        setupMediaPlayer();
                    }
                });

                loadSong.setOnAction(action ->
                {
                    LoadMP3Files(action);
                });

                clearQueueContext.setOnAction(action ->
                {
                    clearQueue(action);
                });

            }); // END of mouseClick Event

            return row;

        }); // END of table row factory
    }

    /**
     * Sets the list with the queue
     */
    private void setupQueueList()
    {
        listQueue.setItems(wm.getQueueList());
    }

    /**
     * Sets up the Media Player
     */
    private void setupMediaPlayer()
    {
        File file;
        if (wm.getQueueList().isEmpty())
        {
            addElevatorMusic();
            file = new File(wm.getQueueList().get(0).toLowerCase());
        }
        else
        {
            file = new File(wm.getQueueList().get(0).toLowerCase());
        }

        song = new Media(file.toURI().toString());

        mPlayer = new MediaPlayer(song);
        mediaView = new MediaView(mPlayer);
        progressSliderSetup(mPlayer);

        //As soon as the media player is ready to play a song it'll get it's duration and set up the progress slider
        mPlayer.setOnReady(() ->
        {
            mpduration = mPlayer.getMedia().getDuration();
            progressSliderSetup(mPlayer);
            updateProgressSlider();
            progressSlider.maxProperty().bind(Bindings.createDoubleBinding(
                    () -> mpduration.toSeconds(),
                    mPlayer.totalDurationProperty()));
        });
    }

    /**
     * Handle the settings for the playback
     */
    private void setupPlaybackSettings()
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

    /**
     * Sets up the panel for the playlists
     */
    private void setupPlaylistPanel()
    {
        // Allows for multiple selections to be made
        playlistPanel.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        // Puts the playlists into the view
        playlistPanel.setItems(wm.getPlaylists());

        // Loads the stores playlists
        wm.loadPlaylists();
    }

    private void progressSliderSetup(MediaPlayer mPlayer)
    {
        //adds a listener to the value, allowing it to determine where to play from when the user drags.
        progressSlider.valueProperty().addListener((Observable ov) ->
        {
            //if the value of the slider is currently 'changing' referring to the listeners task it'll set the value to percentage from the song, where max length = song duration.
            if (progressSlider.isValueChanging())
            {
                mPlayer.seek(mpduration.multiply(progressSlider.getValue() / 100.0));
            }
        });
        //Above we determine if the user is dragging the progress slider, and here we determine what to do if the user clicks the progress bar
        progressSlider.setOnMouseClicked((MouseEvent mouseEvent) ->
        {
            mPlayer.seek(mpduration.multiply(progressSlider.getValue() / 100.0));
        });
    }
    //</editor-fold>

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
        progressSlider.setValue(0);
    }

    /**
     * Tells the playback to pause or play depending on a booleans value.
     *
     * @param event
     */
    @FXML
    private void musicPlayPause(ActionEvent event)
    {
        if (volumeSlider.isDisabled())
        {
            volumeSlider.setDisable(false);
        }

        if (wm.getQueueList().isEmpty() && !isPlaying)
        {
            setupMediaPlayer();
        }
        if (isPlaying == false)
        {
            pause = false;
            mPlayer.play();
            isPlaying = true;
            btnPlayPause.setText("Pause");
        } // if the boolean is true we shall stop playing, reverse the boolean and
        // edit the buttons text.
        else
        {
            mPlayer.pause();
            isPlaying = false;
            btnPlayPause.setText("Play");
            pause = true;
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
        //an int to see where we are in the combobox' index.
        int playbackIndex = playbackSpeed.getSelectionModel().getSelectedIndex();

        // Creating a list starting from 0 + 1 (convert index to number in list)
        System.out.println("the line is #: " + (playbackIndex + 1));

        /*
         * switch case for all the possible playback speeds MAYBE convert to a
         * slider in future instead (free choice and set the speed to the value
         * of the bar)
         */
        switch (playbackIndex)
        {
            //in the first case we set the text to 50% and set the play back rate to 0.5 (0 being 0% --> 2 being 200%)
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
        isLooping = !isLooping;
        //if our loop slide-button is enabled we change the text, set the cycle count to indefinite and reverse the boolean
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
        //Creates a new volume slider and sets the default value to 50%
        JFXSlider volSlide = volumeSlider;
        //volSlide.setValue(50);
        //It was necessary to time it with 100 to be able to receive 100 possible positions for the mixer. For each number is a %, so 0 is 0%, 1 is 1% --> 100 is 100%
        volSlide.setValue(mPlayer.getVolume() * 100);

        //Adds a listener on an observable in the volume slider, which allows users to tweak the volume of the player.
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
        /*
         * Firstly we create a new FileChooser and add an mp3 filter to disable
         * all other file formats (saves a lot of time troubleshooting what went
         * wrong) then followingly we create a LIST of files rather than just a
         * file, so we can load in multiple mp3 files. If the list contains
         * items then we will determine their path and put them in the queue.
         * Otherwise the list of files is empty and we determine that there was
         * an error or that none were selected. Lastly we setup the mediaplayer
         * so that we can play the now selected song(s)
         */
        FileChooser fc = new FileChooser();
        fc.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("MP3 Files", "*.mp3"));

        List<File> chosenFiles = fc.showOpenMultipleDialog(null);

        try
        {
            wm.setPathAndName(chosenFiles);
        }
        catch (IOException ex)
        {
            System.out.println(ex.getMessage());
        }

        if (chosenFiles != null)
        {
            for (int i = 0; i < chosenFiles.size(); i++)
            {
                wm.getQueueList().add(chosenFiles.get(i).getAbsolutePath());
            }
        }
        else
        {
            System.out.println("One or more invalid file(s) / None selected");
            return;
        }

        if (!wm.getQueueList().isEmpty())
        {
            String source = wm.getQueueList().get(0);
            setupMediaPlayer();

        }
        pathNames = chosenFiles;

        try
        {
            savePath();
        }
        catch (SQLException ex)
        {
            System.out.println(ex.getMessage());
        }

    }

    /**
     * Clears the queue from all songs
     *
     * @param event
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

        isPlaying = false;
        btnPlayPause.setText("Play");
    }

    /**
     * Creates a new playlist and adds it to the list of playlists
     *
     * @param event
     */
    @FXML
    private void createPlaylist(ActionEvent event)
    {
        wm.createPlaylistWindow();
    }

    /**
     * Removes a playlist
     *
     * @param event
     */
    @FXML
    private void deletePlaylist(ActionEvent event)
    {
        ObservableList<Playlist> selectedItems = playlistPanel
                .getSelectionModel().getSelectedItems();
        wm.deletePlaylists(selectedItems);
    }
    //</editor-fold>

    private void updateProgressSlider()
    {
        double currentTime = mPlayer.getCurrentTime().toSeconds();
        double totalTime = mPlayer.getTotalDuration().toSeconds();

        double cTMinutes = (currentTime / 60);
        double cTSeconds = currentTime;

        String cTimeFormat = String.format("%.02f:%.02f", cTMinutes, cTSeconds);

        double tTMinutes = (totalTime / 60);
        double tTSeconds = totalTime;
        String tTimeFormat = String.format("%.02f:%.02f", tTMinutes, tTSeconds);

        lblTimer.setText(cTimeFormat + " / " + tTimeFormat);
        //FOR TESTING
        System.out.println("duration in minutes:" + mpduration.toMinutes());
        System.out.println("duration in seconds:" + mpduration.toSeconds());
        System.out.println("duration in milliseconds:" + mpduration.toMillis());
    }

    /**
     * Adds a random song to the playlist
     */
    private void addElevatorMusic()
    {
        String music;

        Random rnd = new Random();

        int r = rnd.nextInt(2) + 2;
        System.out.println(r);
        if (r > 2)
        {
            music = "./src/myTunes/media/Elevator (Control).mp3";
        }
        else if (r > 3)
        {
            music = "./src/myTunes/media/Elevator (Caverns).mp3";
        }
        else
        {
            music = "./src/myTunes/media/elevatormusic.mp3";
        }

        File file = new File(music);
        wm.getQueueList().add(file.getAbsolutePath());
    }

    /**
     * Plays the next song.
     */
    private void changeSongInQue()
    {
        if (!wm.getQueueList().isEmpty() && pause == false)
        {
            getNewSongInQue();
        }
    }

    /**
     * Gets ahold of the new song in que.
     */
    private void getNewSongInQue()
    {
        mPlayer.stop();
        File file = new File(wm.getQueueList().get(0));
        song = new Media(file.toURI().toString());

        mPlayer = new MediaPlayer(song);
        mediaView = new MediaView(mPlayer);
        progressSliderSetup(mPlayer);

        //As soon as the media player is ready to play a song it'll get it's duration and set up the progress slider
        mPlayer.setOnReady(() ->
        {
            progressSliderSetup(mPlayer);
            mpduration = mPlayer.getMedia().getDuration();
            updateProgressSlider();
        });
        mPlayer.play();
    }

    /**
     * Saves a song name to the database
     *
     * @throws SQLException
     */
    private void savePath() throws SQLException
    {
        List<String> dataBaseSongNames = wm.checkIfIsInDatabase();

        List<String> songNamePaths = wm.getPath(pathNames);

        if (!dataBaseSongNames.isEmpty())
        {
            for (String pathString : songNamePaths)
            {
                if (!dataBaseSongNames.contains(pathString))
                {
                    wm.createSongPath(pathString);
                }
            }
        }
        else
        {
            for (int i = 0; i < songNamePaths.size(); i++)
            {
                wm.createSongPath(songNamePaths.get(i));
            }
        }
    }
}
