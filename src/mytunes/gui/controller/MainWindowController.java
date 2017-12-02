package mytunes.gui.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXSlider;
import com.jfoenix.controls.JFXToggleButton;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.Observable;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;
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
    // Model
    private MainWindowModel wm;

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
    private JFXListView<?> listMetaData;
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
    //</editor-fold>

    private int i;

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

        // Sets up and connects the various lists to the model
        setupSongList();
        setupQueueList();
        setupPlaybackSpeedSettings();
        setupPlaylistPanel();

        // Places the playback functionality at the very front of the application
        volumeSlider.getParent().getParent().toFront();
    }

    //<editor-fold defaultstate="collapsed" desc="Table View Fold">
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

        // Sets up the mouse listener for the tableview
        setupTableMouseListner();
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
        ContextMenu cm = new ContextMenu();

        // Fills that context menu with items and actions
        setupTableContextMenu(cm);

        // Adds the actual listener to the table
        tblSongList.setOnMouseClicked((MouseEvent event) ->
        {
            // Double click - Single action
            if (event.getClickCount() == 2)
            {
                // Extracts the item that's been clicked on
                Music selectedItem = tblSongList.getSelectionModel().getSelectedItem();

                // Adds the selected item to the queue
                wm.setQueuePlay(selectedItem);

                // Plays the queue
                prepareAndPlay();
            }

            // Right click - Context Menu
            if (event.getButton() == MouseButton.SECONDARY)
            {
                // Opens the context menu with the top left corner being at the
                // mouse's position
                cm.show(tblSongList, event.getScreenX(), event.getScreenY());
            }
        });
    }

    /**
     * Sets up the context menu for the tableview
     *
     * @param cm The context menu to work with
     */
    private void setupTableContextMenu(ContextMenu cm)
    {
        // Creates a new item for the menu and puts it in
        MenuItem play = new MenuItem("Play");
        cm.getItems().add(play);
        play.setOnAction(action ->
        {
            ObservableList<Music> selectedItems = tblSongList
                    .getSelectionModel().getSelectedItems();

            if (!selectedItems.isEmpty())
            {
                if (wm.isPlaying())
                {
                    this.songStop(action);
                }
                wm.setQueuePlay(selectedItems);
                prepareSetup();
            }
        });

        // Creates a new item for the menu and puts it in
        MenuItem addQueue = new MenuItem("Add to queue");
        cm.getItems().add(addQueue);
        addQueue.setOnAction(action ->
        {
            ObservableList<Music> selectedItems = tblSongList
                    .getSelectionModel().getSelectedItems();

            if (!selectedItems.isEmpty())
            {
                wm.setQueueAdd(selectedItems);
                prepareSetup();
            }
        });

        // Creates a new item for the menu and puts it in
        MenuItem loadSong = new MenuItem("Load Song");
        cm.getItems().add(loadSong);
        loadSong.setOnAction(action ->
        {
            LoadMediaFiles(action);
        });

        // Creates a new item for the menu and puts it in
        MenuItem clearQueueContext = new MenuItem("Clear Queue");
        cm.getItems().add(clearQueueContext);
        clearQueueContext.setOnAction(action ->
        {
            clearQueue(action);
        });
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

        // Loads the stores playlists
        wm.loadPlaylists();

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
        ContextMenu cm = new ContextMenu();

        // Defines the content of the context menu and the actions of the items
        setupPlaylistContextMenu(cm);

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

                prepareAndPlay();
            }

            // Right click - Context Menu
            if (event.getButton() == MouseButton.SECONDARY)
            {
                cm.show(playlistPanel, event.getScreenX(), event.getScreenY());
            }
        });
    }

    /**
     * Setsup the context menu, their options and the action of those options,
     * for the playlist
     *
     * @param cm The context to work with
     */
    private void setupPlaylistContextMenu(ContextMenu cm)
    {
        // Creates the option to replace the queue with the playlist
        MenuItem playPlaylist = new MenuItem("Play List");
        cm.getItems().add(playPlaylist);
        playPlaylist.setOnAction((Action) ->
        {
            wm.setQueuePlay(playlistPanel
                    .getSelectionModel()
                    .getSelectedItem()
                    .getPlaylist());
        });

        // Creates the option to add the playlist to the queue
        MenuItem addPlaylist = new MenuItem("Add to Queue");
        cm.getItems().add(addPlaylist);
        addPlaylist.setOnAction((action) ->
        {
            wm.setQueueAdd(playlistPanel
                    .getSelectionModel()
                    .getSelectedItem()
                    .getPlaylist());
        });

        // Creates the option to edit playlists
        MenuItem editPlaylist = new MenuItem("Edit Playlist");
        cm.getItems().add(editPlaylist);
        editPlaylist.setOnAction((event) ->
        {
            System.out.println("Thoust be changing thee order of musical arts.");
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

        setupQueueMouseListener();
    }

    /**
     * Sets up a mouse listener for the queue list
     *
     * This method will set up a mosue listener for the queue list, which will
     * listen for double clicks and right clicks
     */
    private void setupQueueMouseListener()
    {
        ContextMenu cm = new ContextMenu();
        setupQueueContextMenu(cm);

        listQueue.setOnMouseClicked((MouseEvent event) ->
        {
            if (event.getButton() == MouseButton.SECONDARY)
            {
                cm.show(listQueue, event.getScreenX(), event.getScreenY());
            }

            if (event.getClickCount() == 2)
            {
                // Play the selected song
            }
        });
    }

    /**
     * Sets up the context menu for the queue list
     */
    private void setupQueueContextMenu(ContextMenu cm)
    {
        // Creates a test menu item
        MenuItem test = new MenuItem("Testing");
        cm.getItems().add(test);
        test.setOnAction((event) ->
        {
            System.out.println("Clicked");
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
        if (volumeSlider.isDisabled())
        {
            volumeSlider.setDisable(false);
        }
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
                    GetMediaPlayerStatus();
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * A collection of things we execute when we prepare the setups
     */
    private void prepareSetup()
    {
        setupMediaPlayer();
        enableSettings();
        TimeChangeListener();
    }

    /**
     * A preperation of our setup, followed by the play function
     */
    private void prepareAndPlay()
    {
        if (wm.isPlaying())
        {
            wm.stopMediaPlayer();
        }
        prepareSetup();
        GetMediaPlayerStatus();
        wm.startMediaPlayer();
        wm.setPlaying(true);
        btnPlayPause.setText("Pause");
    }

    /**
     * Under the initialize we disabled all the following objects - here we
     * enable them again, which we will run under the prepareSetup method
     */
    private void enableSettings()
    {
        volumeSlider.setDisable(false);
        btnLoop.setDisable(false);
        playbackSpeed.setDisable(false);
        progressSlider.setDisable(false);
        lblTimer.setDisable(false);
        progressSlider.setStyle("-fx-control-inner-background: #0E9654;");
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
            enableSettings();
            wm.addElevatorMusic();
            prepareSetup();
            wm.startMediaPlayer();
        }
        else if (!wm.isPlaying())
        {
            //Needs to set the BEFORE media is played (apparently?)
            TimeChangeListener();

            wm.startMediaPlayer();
            wm.setPlaying(true);
            btnPlayPause.setText("Pause");
            enableSettings();
            wm.getMediaPlayer().setOnEndOfMedia(() ->
            {
                if (!wm.getQueueList().isEmpty())
                {
                    List<Media> medias = wm.getMedias();

                    wm.stopMediaPlayer();
                    getNewSongInQue();
                    i++;
                    System.out.println(medias.get(i));
                    wm.setMediaPlayer(new MediaPlayer(medias.get(i)));
                    wm.startMediaPlayer();
                }
            });
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

    //<editor-fold defaultstate="collapsed" desc="Progress Slider Fold">
    /**
     * Allows for setting up listeners for the change in the progress slider
     */
    private void TimeChangeListener()
    {
        wm.getMediaPlayer().currentTimeProperty().addListener((Observable ov) ->
        {
            updateSliderAndTimer();
        });
    }

    /**
     * Updates ProgressSlider + Timer
     * A method which first takes the duration from our media player and then
     * sets the Timer label to be a formated version of: [how far we are / how
     * long the song is]
     * The slider is set up to manually check for how far we have gone and
     * adjust to that (automatic sliding on progress) Followed by 2 slider
     * listeners which allows the user
     * to either drag and drop the slider knob or simply just click the progress
     * slider and the slider will adjust to the user input.
     */
    private void updateSliderAndTimer()
    {
        Duration currentTime = wm.getCurrentTime();
        Duration mpduration = wm.getduration();

        lblTimer.setText(MainWindowModel.formatTime(currentTime, mpduration));

        // Adds a listener to the value, allowing it to automatically adjust to
        // where it is - displaying the progress to the user.
        wm.getMediaPlayer().currentTimeProperty().addListener(
                (ObservableValue<? extends Duration> observable,
                 Duration duration,
                 Duration current) ->
        {
            progressSlider.setValue(current.toSeconds());
        });

        // Adds a listener to the value, allowing it to determine where to play
        // from when the user drags.
        progressSlider.valueProperty().addListener((Observable ov) ->
        {
            // If the value of the slider is currently 'changing' referring to
            // the listeners task it'll set the value to percentage from the
            // song, where max length = song duration.
            if (progressSlider.isValueChanging())
            {
                wm.seek(Duration.seconds(progressSlider.getValue()));
            }
        });
        //Above we determine if the user is dragging the progress slider, and here we determine what to do if the user clicks the progress bar
        progressSlider.setOnMouseClicked((MouseEvent mouseEvent) ->
        {
            wm.seek(Duration.seconds(progressSlider.getValue()));
        });
    }
    //</editor-fold>

    /**
     * Chooses the file(s)
     * Handles the file chooser and allows multiple selections of files to add (
     * NEEDS A FIX TO ADD MEDIA PLAYER TO ALL FILES )
     */
    private void choosingFiles()
    {
        File file;

        // Checks if the queue is empty.
        if (wm.getQueueList().isEmpty())
        {
            // If the queue is empty. Add a place holder track
            wm.addElevatorMusic();
            file = new File(wm.getQueueList().get(0).getLocation().toLowerCase());
        }
        else
        {
            // Otherwise just play the first track in the queue
            file = new File(wm.getQueueList().get(0).getLocation().toLowerCase());
        }

        wm.setSong(new Media(file.toURI().toString()));

        try
        {
            FileInputStream inputStream = new FileInputStream(file);
        }
        catch (FileNotFoundException ex)
        {
            Logger.getLogger(MainWindowController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //<editor-fold defaultstate="collapsed" desc="Create Playlist | Delete Playlist | Random Song Fold">
    /**
     * Creates a new playlist
     * and adds it to the list of playlists
     */
    @FXML
    private void createPlaylist(ActionEvent event)
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

    //<editor-fold defaultstate="collapsed" desc="Change next Song in Q | Get next Song in Q Fold">
    /**
     * Plays the next song in queue if there is one
     */
    private void changeSongInQue()
    {
        if (!wm.getQueueList().isEmpty())
        {
            getNewSongInQue();
        }
    }

    /**
     * Gets ahold of the new song in queue by checking the index of the list.
     * When the media is ready to play we set the duration of the new
     * MediaPlayer
     *
     * //TODO : Needs to add MORE than just mpduration (I think?)
     */
    private void getNewSongInQue()
    {
        wm.newMedias();
        wm.stopMediaPlayer();

        for (int index = 0; index < wm.getQueueList().size(); index++)
        {
            File newFile = new File(wm.getQueueList().get(index).getLocation());
            Media song = new Media(newFile.toURI().toString());
            wm.getMedias().add(song);
        }
        wm.getMediaPlayer().setOnReady(() ->
        {
            wm.updateDuration();
        });
    }
    //</editor-fold>

    /**
     * Sets up MediaPlayers
     * Sets up the Media Player by first running the fileChooser method and
     * afterwards - once the media is ready - it creates a mediaview for it,
     * acquires its duration, sets the
     * progress sliders value to a double of 0 (reset) and then sets the max
     * value of the slider to the songs duration in seconds.
     * It retrieves the MediaPlayer Status in order to display it to the user as
     * the final part in the setonready listener.
     */
    private void setupMediaPlayer()
    {
        choosingFiles(); //Needs a fix as mentioned in the method

        // As soon as the media player is ready to play a song we allow for
        // manipulating the media file (playback speed, volume etc.)
        wm.getMediaPlayer().setOnReady(() ->
        {
            wm.updateDuration();
            progressSlider.setValue(0.0);
            progressSlider.setMax(wm.getduration().toSeconds());
//            progressSlider.setMax(mPlayer.getTotalDuration().toSeconds());
            GetMediaPlayerStatus();
        });
    }

    /**
     * A method to listen to the MediaPlayer Status
     * A listener which gives feedback on what status the MediaPlayer currently
     * has (for visual debugging)
     */
    private void GetMediaPlayerStatus()
    {
        wm.getMediaPlayer().statusProperty()
                .addListener((observable, oldValue, newValue)
                        -> lblmPlayerStatus
                        .setText("MediaPlayer Status: "
                                 + newValue.toString().toLowerCase()));
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

        //wm.setPathAndName(chosenFiles);
        if (chosenFiles != null)
        {
            for (int index = 0; index < chosenFiles.size(); index++)
            {
                wm.getQueueList().add(new Music(
                        0,
                        chosenFiles.get(index).getName(),
                        chosenFiles.get(index).getName(),
                        chosenFiles.get(index).getName(),
                        9999,
                        chosenFiles.get(index).getAbsolutePath()
                ));
            }
        }
        else
        {
            System.out.println("One or more invalid file(s) / None selected");
            return;
        }

        if (!wm.getQueueList().isEmpty())
        {
            prepareSetup();
        }
//        pathNames = chosenFiles;
    }

    /**
     * Clears the queue
     * We check if queue is empty, if it is not we force all songs to stop,
     * followed by a method to clear our queue and
     * finally set the isPlaying boolean accordingly and the text of the
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
}
