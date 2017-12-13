package mytunes.gui.model;

import com.jfoenix.controls.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.Observable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.media.EqualizerBand;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import mytunes.be.Music;
import mytunes.be.Playlist;
import mytunes.bll.BLLManager;
import mytunes.bll.MetaData;
import mytunes.bll.Search;
import mytunes.bll.exception.BLLException;
import mytunes.gui.controller.EditSongController;
import mytunes.gui.controller.PlaylistWindowController;

/**
 *
 * @author Storm
 */
public class MainWindowModel
{

    AnchorPane controllerAnchorPane;

    // Lists
    private ObservableList<Music> allSongs;
    private ObservableList<Music> queue;
    private ObservableList<MediaPlayer> queueMedia;
    private ObservableList<Playlist> playlists;

    // Class references
    private BLLManager bllManager;
    private MetaData meta;
    private Search search;

    // Static variables
    private static final double START_FREQ = 250;
    private static final int AMOUNT_OF_BANDS = 7; // the minimum amount

    //<editor-fold defaultstate="collapsed" desc="Instance Variables">
    private boolean playing;
    private boolean looping;

    private MediaPlayer mediaPlayer;
    private Duration mpduration;
    private Media song;
    private Status mStatus;
    private File newFile;

    private List<File> pathNames;
    private int songIdFromTable;

    private int currentSong = 0;
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="FXML Placeholder">
    private Slider progressSlider;
    private Label lblmPlayerStatus;
    private Label lblTimer;
    private Label lblArtist;
    private Label lblTitle;
    private Label lblAlbum;
    private Label lblDescription;
    private Label lblGenre;
    private Label lblYear;
    private Label lblDuration;
    private Label lblAlbumCurrent;
    private Label lblArtistCurrent;
    private Label lblDescriptionCurrent;
    private Label lblDurationCurrent;
    private Label lblGenreCurrent;
    private Label lblTitleCurrent;
    private Label lblYearCurrent;
    private ComboBox<String> playbackSpeed;
    private JFXButton btnPlayPause;
    private JFXSlider volumeSlider;
    private JFXToggleButton btnLoop;
    //</editor-fold>

    /**
     * Constructor
     */
    public MainWindowModel()
    {
        try
        {
            this.meta = new MetaData();
            this.bllManager = new BLLManager();
            this.search = new Search();
            this.allSongs = FXCollections.observableArrayList();
            this.queue = FXCollections.observableArrayList();
            this.queueMedia = FXCollections.observableArrayList();
            this.playlists = FXCollections.observableArrayList();

            this.btnPlayPause = new JFXButton("Play");
            this.btnLoop = new JFXToggleButton();
            this.btnLoop.setDisable(true);
            this.playbackSpeed = new ComboBox<>();
            this.playbackSpeed.setDisable(true);
            this.progressSlider = new Slider();
            this.progressSlider.setDisable(true);
            this.volumeSlider = new JFXSlider();
            this.volumeSlider.setDisable(true);
            this.lblmPlayerStatus = new Label();
            this.lblTimer = new Label("00:00/13:37");

            this.lblAlbum = new Label();
            this.lblAlbumCurrent = new Label();
            this.lblArtist = new Label();
            this.lblArtistCurrent = new Label();
            this.lblDescription = new Label();
            this.lblDescriptionCurrent = new Label();
            this.lblDuration = new Label();
            this.lblDurationCurrent = new Label();
            this.lblGenre = new Label();
            this.lblGenreCurrent = new Label();
            this.lblTitle = new Label();
            this.lblTitleCurrent = new Label();
            this.lblYear = new Label();
            this.lblYearCurrent = new Label();
        }
        catch (IOException ex)
        {
            System.out.println(ex.getMessage());
        }
        catch (BLLException ex)
        {
           System.out.println(ex.getMessage());
        }
    }

    //<editor-fold defaultstate="collapsed" desc="FXML Method calls">
    /**
     * Skips to the next song in the queue
     */
    public void fxmlNextSong()
    {
        stopMediaPlayer();
        skipToNextSong();
        prepareAndPlay();
    }

    /**
     * Skips to the previous song in the queue
     */
    public void fxmlPrevSong()
    {
        stopMediaPlayer();
        skipToPrevSong();
        prepareAndPlay();
    }

    /**
     * Clears the queue
     *
     * IF any track is currently playing, stop said track before removing it
     * from the queue, otherwise we'll lose control over said track
     */
    public void fxmlClearQueue()
    {
        // Checks if the queue is empty
        if (!getQueueList().isEmpty())
        {
            // If the queue contains Music, stop it
            fxmlSongStop();
        }

        // Clears the queue list
        clearQueueList();
    }

    /**
     * Opens the native file chooser
     *
     * Allows the user to add new files to the library of music.
     * Currently supported files: MP3, MP4, FXM, FXL, WAV, HLS, AIF, AIFF
     */
    public void fxmlLoadMediaFiles()
    {
        // Creates a new FileChooser object
        FileChooser fc = new FileChooser();

        // Defines what files it will look for
        FileChooser.ExtensionFilter mp3Filter = new FileChooser.ExtensionFilter("MP3 Files", "*.mp3");
        FileChooser.ExtensionFilter fxmFilter = new FileChooser.ExtensionFilter("FXM Files", "*.fxm");
        FileChooser.ExtensionFilter flvFilter = new FileChooser.ExtensionFilter("FXL Files", "*.flv");
        FileChooser.ExtensionFilter mp4Filter = new FileChooser.ExtensionFilter("MP4 Files", "*.mp4");
        FileChooser.ExtensionFilter wavFilter = new FileChooser.ExtensionFilter("WAV Files", "*.wav");
        FileChooser.ExtensionFilter hlsFilter = new FileChooser.ExtensionFilter("HLS Files", "*.hls");
        FileChooser.ExtensionFilter aiffFilter = new FileChooser.ExtensionFilter("AIF(F) Files", "*.aif", "*.aiff");

        // Adds the filters
        fc.getExtensionFilters().addAll(mp3Filter, fxmFilter, flvFilter, mp4Filter, wavFilter, hlsFilter, aiffFilter);

        // Opens the FileChooser and saves the results in a list
        List<File> chosenFiles = fc.showOpenMultipleDialog(null);

        // Checks if any files where chosen
        if (chosenFiles != null)
        {
            // If valid files were chosen, add them as music
            try
            {
                List<Music> addedMusic;
                addedMusic = setMetaData(chosenFiles);
                loadSongList();
                getQueueList().addAll(addedMusic);
                prepareSetup();
            }
            catch (BLLException ex)
            {
                System.out.println(ex.getMessage());
            }
        }
        else
        {
            // Otherwise return
            System.out.println("One or more invalid file(s) / None selected");
            return;
        }

        // If the queue isn't empty, prepare the first media in the queue
        if (!getQueueList().isEmpty())
        {
            prepareSetup();
        }
    }

    /**
     * Deletes the selected playlist
     *
     * @param listElement The List element containing the playlist objects
     */
    public void fxmlDeletePlaylist(JFXListView listElement)
    {
        // Create a new list based on all the selected items
        ObservableList<Playlist> selectedItems = listElement
                .getSelectionModel().getSelectedItems();

        // Deletes the selected items from storage
        deletePlaylists(selectedItems);
    }

    /**
     * Handles the volume
     */
    public void fxmlVolumeMixer()
    {
        //Creates a new volume slider and sets the default value to 50%
        JFXSlider volSlide = volumeSlider;

        // It was necessary to time it with 100 to be able to receive 100
        // possible positions for the mixer. For each number is a %, so 0 is 0%,
        // 1 is 1% --> 100 is 100%
        volSlide.setValue(getVolume());

        //Adds a listener on an observable in the volume slider, which allows
        //users to tweak the volume of the player.
        volSlide.valueProperty().addListener(
                (javafx.beans.Observable observable) ->
        {
            setVolume(volSlide.getValue() / 100);
        });
    }

    /**
     * Toggles looping on the current track
     *
     * @param loop
     */
    public void fxmlLoopAction(JFXToggleButton loop)
    {
        // When called, reverse the current loop from whatever it is
        reverseLooping();

        // If our loop slide-button is enabled we change the text, set the cycle
        // count to indefinite and reverse the boolean
        if (loop.isSelected() == true)
        {
            loop.setText("Loop: ON");
            setLooping();
            System.out.println("Looping on");
        }
        else if (loop.isSelected() != true)
        {
            loop.setText("Loop: OFF");
            reverseLooping();
            System.out.println("Looping off");
        }
    }

    /**
     * Handles the Starting and Pausing of the current track
     */
    public void fxmlMusicPlayPause()
    {
        // If the queue is empty and nothing is playing
        if (getQueueList().isEmpty() && !isPlaying())
        {
            // Enable contols
            enableSettings();

            // Adds a track to the list
            addElevatorMusic();

            // Prepare the track to be played and start it
            prepareAndPlay();
        }

        // If nothing is playing
        else if (!isPlaying())
        {
            // Adds a listener to the current media's duration
            timeChangeListener();

            // Starts the mediaplayer
            startMediaPlayer();

            // Indicates the media is playing
            setPlaying(true);

            // change the play button
            btnPlayPause.setText("Pause");

            // Enables the settings if they aren't
            enableSettings();
        }

        // If theres' something in the queue and something is playing
        else
        {
            // Pauses the media player
            pauseMediaPlayer();

            // Indicates that nothing is playing
            setPlaying(false);

            // changes the play button
            btnPlayPause.setText("Play");
        }
    }

    /**
     * Stops the current playing song
     */
    public void fxmlSongStop()
    {
        // Updates the status
        updateStatus();

        // Stores the status as a local variable
        Status status = getMediaStatus();

        // Check if the status is actuall exist
        if (null != status)
        {
            switch (status)
            {
                case PLAYING:
                    System.out.println("Status is: " + status);
                    stopMediaPlayer();
                    setPlaying(false);
                    btnPlayPause.setText("Play");
                    progressSlider.setValue(0.0);
                    break;
                case STOPPED:
                    System.out.println("Status is: " + status);
                    break;
                case PAUSED:
                    updateDuration();
                    progressSlider.setValue(0.0);
                    progressSlider.setMax(getMediaPlayer().getTotalDuration().toSeconds());
                    getMediaPlayerStatus();
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * Change the playback speed
     */
    public void fxmlPlaybackSpeed()
    {
        //an int to see where we are in the combobox' index.
        int playbackIndex = playbackSpeed.getSelectionModel().getSelectedIndex();

        // Creating a list starting from 0 + 1 (convert index to number in list)
        System.out.println("the line is #: " + (playbackIndex + 1));

        // switch case for all the possible playback speeds MAYBE convert to a
        // slider in future instead (free choice and set the speed to the value
        // of the bar)
        setPlayckSpeed(playbackIndex);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Property Getters">
    //<editor-fold defaultstate="collapsed" desc="Slider">
    public DoubleProperty getProgressSliderValueProperty()
    {
        return progressSlider.valueProperty();
    }

    public BooleanProperty getProgressSliderDisableProperty()
    {
        return progressSlider.disableProperty();
    }

    public BooleanProperty getValueChangingProperty()
    {
        return progressSlider.valueChangingProperty();
    }

    public DoubleProperty getProgressSliderMaxProperty()
    {
        return progressSlider.maxProperty();
    }
    //</editor-fold>

    public StringProperty getMediaplayerLabelTextProperty()
    {
        return lblmPlayerStatus.textProperty();
    }

    public BooleanProperty getVolumeDisableProperty()
    {
        return volumeSlider.disableProperty();
    }

    public BooleanProperty getLoopDisableProperty()
    {
        return btnLoop.disableProperty();
    }

    public StringProperty getLoopButtonTextProperty()
    {
        return btnLoop.textProperty();
    }

    public BooleanProperty getPlaybackSpeedDisabledProperty()
    {
        return playbackSpeed.disableProperty();
    }

    public BooleanProperty getTimerDisableProperty()
    {
        return lblTimer.disableProperty();
    }

    public StringProperty getTimerTextProperty()
    {
        return lblTimer.textProperty();
    }

    //<editor-fold defaultstate="collapsed" desc="Playing Song">
    public StringProperty getCurrentAlbumProperty()
    {
        return lblAlbumCurrent.textProperty();
    }

    public StringProperty getCurrentArtistProperty()
    {
        return lblArtistCurrent.textProperty();
    }

    public StringProperty getCurrentDescProperty()
    {
        return lblDescriptionCurrent.textProperty();
    }

    public StringProperty getCurrentDurationProperty()
    {
        return lblDurationCurrent.textProperty();
    }

    public StringProperty getCurrentGenreProperty()
    {
        return lblGenreCurrent.textProperty();
    }

    public StringProperty getCurrentTitleProperty()
    {
        return lblTitleCurrent.textProperty();
    }

    public StringProperty getCurrentYearProperty()
    {
        return lblYearCurrent.textProperty();
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Selected Song">
    public StringProperty getArtistProperty()
    {
        return lblArtist.textProperty();
    }

    public StringProperty getTitleProperty()
    {
        return lblTitle.textProperty();
    }

    public StringProperty getAlbumProperty()
    {
        return lblAlbum.textProperty();
    }

    public StringProperty getDescProperty()
    {
        return lblDescription.textProperty();
    }

    public StringProperty getGenreProperty()
    {
        return lblGenre.textProperty();
    }

    public StringProperty getYearProperty()
    {
        return lblYear.textProperty();
    }

    public StringProperty getDurationProperty()
    {
        return lblDuration.textProperty();
    }
    //</editor-fold>

    public StringProperty getPlayPauseButton()
    {
        return btnPlayPause.textProperty();
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Getters">
    /**
     * Goes through song files, and gets their name. Returns a list with their
     * name.
     *
     * @param chosenFiles
     *
     * @return
     *
     * @throws SQLException
     */
    public List<String> getPath(List<File> chosenFiles) throws SQLException
    {
        List<String> songPath = new ArrayList();
        for (int i = 0; i < chosenFiles.size(); i++)
        {
            songPath.add(chosenFiles.get(i).getName());
        }
        return songPath;
    }

    /**
     * Checks if a media is playing
     *
     * @return
     */
    public boolean isPlaying()
    {
        return playing;
    }

    /**
     * Updates the status of the current media
     */
    public void updateStatus()
    {
        this.mStatus = this.queueMedia.get(currentSong).getStatus();
    }

    /**
     * Gets the current status of the media
     *
     * @return
     */
    public Status getMediaStatus()
    {
        return this.mStatus;
    }

    /**
     * Gets the status of the current mediaplayer
     */
    public void getMediaPlayerStatus()
    {
        getMediaPlayer().statusProperty().addListener((observable,
                                                       oldValue,
                                                       newValue)
                -> lblmPlayerStatus.setText("MediaPlayer Status: "
                                            + newValue.toString().toLowerCase()));
    }

    /**
     * Gets the current mediaplayer
     *
     * @return
     */
    public MediaPlayer getMediaPlayer()
    {
        return this.queueMedia.get(currentSong);
    }

    /**
     * Gets the volume
     *
     * @return
     */
    public double getVolume()
    {
        return (this.queueMedia.get(currentSong).getVolume() * 100.0) / 100.0;
    }

    /**
     * Gets the current time from the mediaplayer
     *
     * @return
     */
    public Duration getCurrentTime()
    {
        return this.queueMedia.get(currentSong).getCurrentTime();
    }

    /**
     * Gets the duration of the current MediaPlayer
     *
     * @return
     */
    public Duration getduration()
    {
        return this.mpduration;
    }

    /**
     * Gets the speed settings for the playback
     *
     * @return
     */
    public ArrayList<String> getPlaybackSpeed()
    {
        ArrayList<String> settings = new ArrayList<>();
        settings.add("50% speed");
        settings.add("75% speed");
        settings.add("Default speed");
        settings.add("125% speed");
        settings.add("125% speed");
        settings.add("150% speed");
        settings.add("200% speed");
        return settings;
    }

    /**
     * Gets the filters
     *
     * This method will check the current status of the filters and return an
     * Arraylist of Strings containing the given filters
     *
     * @param searchTagTitle
     * @param searchTagAlbum
     * @param searchTagArtist
     * @param searchTagGenre
     * @param searchTagDesc
     * @param searchTagYear
     *
     * @return Returns an ArrayList containing the filters
     */
    public ArrayList<String> getFilters(JFXCheckBox searchTagTitle,
                                        JFXCheckBox searchTagAlbum,
                                        JFXCheckBox searchTagArtist,
                                        JFXCheckBox searchTagGenre,
                                        JFXCheckBox searchTagDesc,
                                        JFXCheckBox searchTagYear
    )
    {
        ArrayList<String> filter = new ArrayList<>();
        if (searchTagTitle.selectedProperty().get())
        {
            filter.add("title");
        }
        if (searchTagArtist.selectedProperty().get())
        {
            filter.add("artist");
        }
        if (searchTagAlbum.selectedProperty().get())
        {
            filter.add("album");
        }
        if (searchTagGenre.selectedProperty().get())
        {
            filter.add("genre");
        }
        if (searchTagDesc.selectedProperty().get())
        {
            filter.add("description");
        }
        if (searchTagYear.selectedProperty().get())
        {
            filter.add("year");
        }
        return filter;
    }

    /**
     * Converts seconds into hours and minutes
     *
     * @param seconds
     *
     * @return
     */
    public int[] getSecondsToMinAndHour(int seconds)
    {
        int minutes = seconds / 60;

        seconds -= minutes * 60;

        int hours = minutes / 60;

        minutes -= hours * 60;

        int[] minSec = new int[3];

        minSec[0] = seconds;

        minSec[1] = minutes;

        minSec[2] = hours;

        return minSec;

    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Setters">
    /**
     *
     * @param chosenFiles
     *
     * @return
     */
    public List<Music> setMetaData(List<File> chosenFiles)
    {

        try
        {
            return meta.MetaData(chosenFiles);
        }
        catch (BLLException ex)
        {
            System.out.println(ex.getMessage());
            return null;
        }
    }

    public void setPlayckSpeed(int playbackIndex)
    {
        switch (playbackIndex)
        {
            /*
             * in the first case we set the text to 50% and set the play back
             * rate to 0.5 (0 being 0% --> 2 being 200%)
             */
            case 0:
                System.out.println("50%");
                this.queueMedia.get(playbackIndex).setRate(0.5);
                break;
            case 1:
                System.out.println("75%");
                this.queueMedia.get(playbackIndex).setRate(0.75);
                break;
            case 2:
                System.out.println("100%");
                this.queueMedia.get(playbackIndex).setRate(1.0);
                break;
            case 3:
                System.out.println("125%");
                this.queueMedia.get(playbackIndex).setRate(1.25);
                break;
            case 4:
                System.out.println("150%");
                this.queueMedia.get(playbackIndex).setRate(1.5);
                break;
            case 5:
                System.out.println("175%");
                this.queueMedia.get(playbackIndex).setRate(1.75);
                break;
            case 6:
                System.out.println("200%");
                this.queueMedia.get(playbackIndex).setRate(2.0);
                break;
            default:
                break;
        }
    }

    public void setPlaying(boolean isPlaying)
    {
        this.playing = isPlaying;
    }

    public void setMediaPlayer(MediaPlayer mediaPlayer)
    {
        this.mediaPlayer = mediaPlayer;
    }

    public void setLooping()
    {
//        this.mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        this.queueMedia.get(currentSong).setCycleCount(MediaPlayer.INDEFINITE);
        this.looping = false;
    }

    public void setVolume(double value)
    {
//        this.mediaPlayer.setVolume(value);
        this.queueMedia.get(currentSong).setVolume(value);
        if (value > 3 && value < 0)
        {
//            mediaPlayer.setVolume(5);
            this.queueMedia.get(currentSong).setVolume(5);
        }
    }

    public void setSong(Media media)
    {
        this.song = media;

        this.setMediaPlayer(new MediaPlayer(this.song));
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Song List">
    /**
     * Loads all the songs into the program
     *
     * @throws mytunes.bll.exception.BLLException
     */
    public void loadSongList() throws BLLException
    {
        allSongs.clear();
        allSongs.addAll(bllManager.getSongList());
    }

    /**
     * Gets the list containing all the songs
     *
     * @return a List containing all the registered songs
     */
    public ObservableList<Music> getSongList()
    {
        return allSongs;
    }

    /**
     * Creates and defines a context menu for the TableView
     *
     * This menu creates a context menu and fills it with items. The it assigns
     * each item a command
     *
     * @param table The TableView which to connect this context menu to
     *
     * @return A fully functional ContextMenu
     */
    private ContextMenu tableContextMenu(TableView<Music> table)
    {
        ContextMenu cm = new ContextMenu();

        // Gets the selected items
        ObservableList<Music> items = table
                .getSelectionModel().getSelectedItems();

        // Creates a new item for the menu and puts it in
        MenuItem play = new MenuItem("Play");
        cm.getItems().add(play);
        play.setOnAction(action ->
        {
            if (!items.isEmpty())
            {
                if (isPlaying())
                {
                    this.stopMediaPlayer();
                }
                setQueuePlay(items);
                prepareAndPlay();
            }
        });

        // Creates a new item for the menu and puts it in
        MenuItem addQueue = new MenuItem("Add to queue");
        cm.getItems().add(addQueue);
        addQueue.setOnAction(action ->
        {

            if (!items.isEmpty())
            {
                setQueueAdd(items);
                prepareSetup();
            }
        });

        MenuItem editSong = new MenuItem("Edit Song");
        cm.getItems().add(editSong);
        editSong.setOnAction((event) ->
        {
            try
            {
                Music musicInfo = table.getSelectionModel().getSelectedItem();
                getSongId(musicInfo.getId());
                String title = musicInfo.getTitle();
                String artist = musicInfo.getArtist();
                int time = musicInfo.getDuration();
                String genre = musicInfo.getGenre();
                String pathName = musicInfo.getSongPathName();

                openEditSongWindow(title, artist, time, pathName, genre, controllerAnchorPane);
            }
            catch (IOException ex)
            {
                System.out.println("Cannot open window.");
            }
            catch (SQLException ex)
            {
                System.out.println("Cannot edit song");
            }
        });

        // Deletes the selected song from the db
        MenuItem deleteSong = new MenuItem("Remove song");
        cm.getItems().add(deleteSong);
        deleteSong.setOnAction(action ->
        {
            Music musicInfo = table.getSelectionModel().getSelectedItem();
            System.out.println(musicInfo.getId());
            deleteSong(musicInfo.getId());
            try
            {
                loadSongList();
            }
            catch (BLLException ex)
            {
                System.out.println(ex.getMessage());
            }
        });

        return cm;
    }

    /**
     * Creates a listener for the TableView
     *
     * This method ceates a listener for the TableView. It adds a context menu
     * for when the table has been right clicked. It plays the selected track
     * whenever it's been double clicked. And it sets the selected track to the
     * Selected section when it has been marked
     *
     * @param table The TableView at which to add this listener
     */
    public void setTableMouseListener(TableView<Music> table)
    {
        ContextMenu cm = tableContextMenu(table);

        table.setOnMouseClicked((MouseEvent event) ->
        {
            if (!table.getSelectionModel().getSelectedItems().isEmpty())
            {
                System.out.println("Not Empty");
                // Double click - Single action
                if (event.getClickCount() == 2)
                {
                    // Extracts the item that's been clicked on
                    Music item = table.getSelectionModel()
                            .getSelectedItem();

                    // Adds the selected item to the queue
                    setQueuePlay(item);

                    // Plays the queue
                    prepareAndPlay();
                }

                if (event.getClickCount() == 1)
                {
                    Music item = table
                            .getSelectionModel().getSelectedItem();

                    lblArtist.setText(item.getArtist());
                    lblTitle.setText(item.getTitle());
                    lblAlbum.setText(item.getAlbum());
                    lblDescription.setText(item.getDescription());
                    lblGenre.setText(item.getGenre());
                    lblYear.setText(String.valueOf(
                            item.getYear()));
                    int[] minSec = getSecondsToMinAndHour(item.getDuration());
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
                    cm.show(table, event.getScreenX(), event.getScreenY());
                }
            }
            else
            {
                System.out.println("Empty");
            }
        });
    }

    /**
     * Adds a listener to the search bar
     *
     * This method will listen for changes to the search bar's input. Whenever
     * it's been changed, it'll search the storage for any matching searches
     * within the given filters
     *
     * @param txtTableSearch The text to search for
     * @param filters        The filters to use
     */
    public void setTableSearchListener(TextField txtTableSearch, ArrayList<String> filters)
    {
        txtTableSearch.textProperty().addListener(
                (ObservableValue<? extends String> observable,
                 String oldText,
                 String newText) ->
        {
            songSearch(txtTableSearch.getText(), filters);
        });
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Queue List">
    /**
     * Clears the Queue List entirely
     */
    public void clearQueueList()
    {
        currentSong = 0;
        this.queueMedia.clear();
        this.queue.clear();
    }

    /**
     * Gets the queue list
     *
     * @return The list containing the queues
     */
    public ObservableList<Music> getQueueList()
    {
        return queue;
    }

    /**
     * Replaces all the items in the Queue with the selected items
     *
     * @param selectedItems
     */
    public void setQueuePlay(ObservableList<Music> selectedItems)
    {
        this.queue.clear();
        this.queue.addAll(selectedItems);
    }

    public void setQueuePlay(Music track)
    {
        this.stopMediaPlayer();
        this.queue.clear();
        this.queueMedia.clear();
        this.queue.add(track);
    }

    /**
     * Adds the selected items to the end of the queue list
     *
     * @param selectedItems
     */
    public void setQueueAdd(ObservableList<Music> selectedItems)
    {
        this.queue.addAll(selectedItems);
    }

    private void playNextSong()
    {
        int queueSize = this.queue.size() - 1;
        System.out.println(queueSize);

        // If queue does not have next stop playing
        // If queue has next play next
        if (currentSong < queueSize)
        {
            currentSong++;
            setSong(this.queueMedia.get(currentSong).getMedia());
            startMediaPlayer();
        }
    }

    public void currentSongNext()
    {
        currentSong++;
    }

    private void currentSongPrev()
    {
        currentSong--;
    }

    public int getCurrentSong()
    {
        return currentSong;
    }

    public ObservableList<MediaPlayer> getQueueListMedia()
    {
        return this.queueMedia;
    }

    /**
     * Changes the currently playing song to the given music
     *
     * Note: This will only change which song to play. You must manually set the
     * song to be played
     *
     * @param music The music to play
     */
    public void skipToSong(Music music)
    {
        int index = bllManager.getIndexOf(music, this.queue);

        currentSong = index;
    }

    /**
     * Skips ahead to the next song if applicable
     */
    public void skipToNextSong()
    {
        if (currentSong < queue.size() - 1)
        {
            currentSongNext();
        }
    }

    /**
     * Skips back to the previous song if applicable
     */
    public void skipToPrevSong()
    {
        if (currentSong > 0)
        {
            currentSongPrev();
        }
    }

    /**
     * Adds a mouse listener to the queue
     *
     * @param list
     */
    public void setQueueMouseListener(JFXListView<Music> list)
    {
        // Loads the queue list
        list.setItems(getQueueList());

        // Sets up a mouse listener for the queue
        ContextMenu qcm = queueContextMenu(list);

        list.setOnMouseClicked((MouseEvent event) ->
        {
            if (event.getButton() == MouseButton.SECONDARY)
            {
                qcm.show(list, event.getScreenX(), event.getScreenY());
            }

            if (event.getClickCount() == 2)
            {
                stopMediaPlayer();
                Music selectedItem = list.getSelectionModel().getSelectedItem();
                skipToSong(selectedItem);
                prepareAndPlay();
            }
        });
    }

    /**
     * Creates a change listener for the queue
     *
     * This method creates a change listener for the queue.
     * Whenever a Music has been added the the list, it'll create a MediaPlayer
     * with that Music and add it to a mirror list, which contain all the
     * MediaPlayers used by this application
     * Whenever a Music has been removed from the queue, this will look for the
     * MediaPayer with the same Media and remove it from the list as well,
     * preventing it from being played
     */
    public void setupQueueListener()
    {
        getQueueList().addListener((ListChangeListener.Change<? extends Music> c) ->
        {
            // Must be called to initiate the change listener
            c.next();

            // If there has been added something to the queue
            if (!c.getAddedSubList().isEmpty())
            {
                // Go though each new item and make a mediaplayer for them
                c.getAddedSubList().forEach((music) ->
                {
                    File file = new File(music.getLocation() + "/" + music.getSongPathName());
                    Media media = new Media(file.toURI().toString());
                    MediaPlayer mp = new MediaPlayer(media);

                    mp.setOnEndOfMedia(() ->
                    {
                        playNextSong();
                    });

                    // Add this new media player to a parallel list to the queue
                    getQueueListMedia().add(mp);

                });
            }

            // If some thing has been removed from the list
            if (!c.getRemoved().isEmpty())
            {
                // Go through the queue media list
                for (int i = 0; i < getQueueListMedia().size(); i++)
                {
                    // Gets the full path for the current media
                    String storedMedia = getQueueListMedia().get(i).getMedia().getSource();

                    // Goes through
                    for (int j = 0; j < c.getRemoved().size(); j++)
                    {
                        File file = new File(c.getRemoved().get(j).getLocation());
                        String removedMedia = file.toURI().toString();

                        if (storedMedia.equals(removedMedia))
                        {
                            getQueueListMedia().remove(i);
                            i--;
                        }
                    }

                }
            }
        });
    }

    /**
     * Creates a ContextMenu for the queue
     *
     * @param listQueue
     *
     * @return
     */
    public ContextMenu queueContextMenu(JFXListView<Music> listQueue)
    {
        ContextMenu cm = new ContextMenu();

        // Creates an option to remove the selected item from the list
        MenuItem remove = new MenuItem("Remove");
        cm.getItems().add(remove);
        remove.setOnAction((event) ->
        {
            ObservableList<Music> selectedItems = listQueue
                    .getSelectionModel().getSelectedItems();

            getQueueList().removeAll(selectedItems);
        });

        // Creates an option for clearing the entire list
        MenuItem clear = new MenuItem("Clear");
        cm.getItems().add(clear);
        clear.setOnAction((event) ->
        {
            stopMediaPlayer();
            clearQueueList();
        });

        return cm;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Playlist Methods">
    /**
     * Gets the list of playlists
     *
     * @return An Observeable Array list of all the Playlists
     */
    public ObservableList<Playlist> getPlaylists()
    {
        return playlists;
    }

    /**
     * Loads the playlist from storage
     *
     */
    public void loadPlaylists()
    {
        try
        {
            playlists.addAll(bllManager.getPlaylists());
        }
        catch (BLLException ex)
        {
            System.out.println(ex.getMessage());
        }
    }

    /**
     * Displays the window for creating new playlists
     */
    public void createPlaylistWindow()
    {
        try
        {
            // Gets a hold of the FXML and controller
            File fxml = new File("src/mytunes/gui/view/PlaylistWindow.fxml");
            FXMLLoader fxLoader = new FXMLLoader(fxml.toURL());

            // Loads the window
            Parent root = fxLoader.load();
            Scene scene = new Scene(root);
            Stage stage = new Stage();

            // Gets the controller for the window, so we can retrieve data after
            // it's been closed
            PlaylistWindowController controller = fxLoader.getController();
            PlaylistWindowModel plModel = controller.getModel();

            // Sets the icon for the new window
            File ico = new File("./res/icon/TrollTunes56x56.png");
            Image icon = new Image(ico.toURI().toString());
            stage.getIcons().add(icon);

            // Sets the title for the new window
            stage.setTitle("Create Playlist");

            stage.setScene(scene);
            stage.showAndWait();
            // Waits for the user to give the playlist a name

            // Adds the new playlist to the list of lists, dawg
            if (plModel.shouldSave())
            {
                Playlist pl = new Playlist(plModel.getTitle());
                savePlaylist(plModel.getTitle(), plModel.getPlaylist());
                pl.setPlaylist(plModel.getPlaylist());
                this.playlists.add(pl);
            }
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
    }

    /**
     * Displays the window for creating new playlists
     *
     * @param playlist
     *
     */
    public void createPlaylistWindow(Playlist playlist)
    {
        try
        {
            // Gets a hold of the FXML and controller
            File fxml = new File("src/mytunes/gui/view/PlaylistWindow.fxml");
            FXMLLoader fxLoader = new FXMLLoader(fxml.toURL());

            // Loads the window
            Parent root = fxLoader.load();
            Scene scene = new Scene(root);
            Stage stage = new Stage();

            // Gets the controller for the window, so we can retrieve data after
            // it's been closed
            PlaylistWindowController controller = fxLoader.getController();
            PlaylistWindowModel plModel = controller.getModel();

            controller.setPlaylist(playlist);
            plModel.setSaveButton("Save");

            // Sets the icon for the new window
            File ico = new File("./res/icon/TrollTunes56x56.png");
            Image icon = new Image(ico.toURI().toString());
            stage.getIcons().add(icon);

            // Sets the title for the new window
            stage.setTitle("Update Playlist");

            stage.setScene(scene);
            stage.showAndWait();
            // Waits for the user to give the playlist a name

            // Adds the new playlist to the list of lists, dawg
            if (plModel.shouldSave())
            {
                playlist.setTitle(plModel.getTitle());
                playlist.getPlaylist().clear();
                playlist.getPlaylist().addAll(plModel.getPlaylist());
                bllManager.updatePlaylist(playlist);
                int index = this.playlists.indexOf(playlist);
                this.playlists.add(index, playlist);
                this.playlists.remove(index + 1);
            }
        }
        catch (IOException ex)
        {
            System.out.println(ex.getMessage());
        }
        catch (BLLException ex)
        {
            System.out.println(ex.getMessage());
        }
    }

    /**
     * Deletes one or more playlists from the list
     *
     * @param playlists The playlist to delete
     */
    public void deletePlaylists(ObservableList<Playlist> playlists)
    {
        if (playlists.isEmpty())
        {

        }
        else
        {
            playlists.forEach((pl) ->
            {
                try
                {
                    bllManager.deletePlaylist(pl.getId());
                }
                catch (BLLException ex)
                {
                    System.out.println(ex.getMessage());
                }
            });
            this.playlists.removeAll(playlists);
        }
    }

    public void setPathAndName(List<File> chosenFiles) throws IOException
    {

        writeMusicFolderPath(chosenFiles.get(0).getAbsolutePath());
        for (int j = 0; j < chosenFiles.size(); j++)

        {
            System.out.println(chosenFiles.get(j).getName());
        }
    }

    public void writeMusicFolderPath(String path) throws IOException
    {

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("path.txt")))
        {
            writer.write(path);
        }
    }

    /**
     * Searches through the given list for a match
     *
     * This method will clear the Edited List, then search through the Complete
     * List for any matching cases of the Text String. All matching results will
     * be put into the Edited List.
     *
     * The Edited List and the Complete List muct nto be the same. Instead, they
     * should mirror the same list so one can be edited while the other remains
     * unchanged.
     *
     * @param text         The text to search for
     * @param editedList   The list that will contain the results
     * @param completeList The list that contains all items to be search through
     */
    public void searchPlaylist(String text,
                               ObservableList<Music> editedList,
                               ObservableList<Music> completeList)
    {
        editedList.clear();
        List<Music> result = getSearchResultPlaylist(text, completeList);
        editedList.addAll(result);
    }

    /**
     * Gets the search results for the playlist window
     *
     * Searches the given list for instances of the given text. If any is found,
     * it'll be returned as an ArrayList containing Music elements.
     *
     * The fields that'll be searched for a match are the Title, the Artist and
     * the Album
     *
     * @param text The text to search for
     * @param list The ObservableList to search through
     *
     * @return Returns an ArrayList containing all the matching results based on
     *         predetermined search criteria
     */
    private List<Music> getSearchResultPlaylist(String text,
                                                ObservableList<Music> list)
    {
        List<Music> searchResult = new ArrayList();

        list.forEach((music) ->
        {
            if (music.getTitle().toLowerCase().contains(text.toLowerCase())
                || music.getArtist().toLowerCase().contains(text.toLowerCase())
                || music.getAlbum().toLowerCase().contains(text.toLowerCase()))
            {
                searchResult.add(music);
            }
        });
        return searchResult;
    }

    /**
     * Saves the playlist to storage
     *
     * @param title    The title of the playlist
     * @param playlist A List containing the songs of the playlist
     *
     */
    public void savePlaylist(String title, ObservableList<Music> playlist)
    {
        try
        {
            bllManager.savePlaylist(title, playlist);
        }
        catch (BLLException ex)
        {
            System.out.println(ex.getMessage());
        }
    }

    /**
     * Adds a mouse listener to the playlist
     *
     * @param playlistPanel
     */
    public void setPlaylistMouseListener(JFXListView<Playlist> playlistPanel)
    {

        // Sets up am mouse listener for the playlist
        // Creates a new context menu
        ContextMenu plcm = playlistContextMenu(playlistPanel);

        // Adds the actual listener to the playlist
        playlistPanel.setOnMouseClicked((MouseEvent event) ->
        {
            // Double click - Single action
            if (event.getClickCount() == 2)
            {
                stopMediaPlayer();
                clearQueueList();
                ObservableList<Music> playlist = playlistPanel
                        .getSelectionModel()
                        .getSelectedItem()
                        .getPlaylist();

                setQueuePlay(playlist);
                prepareAndPlay();
            }

            // Right click - Context Menu
            if (event.getButton() == MouseButton.SECONDARY)
            {
                plcm.show(playlistPanel, event.getScreenX(), event.getScreenY());
            }
        });
    }

    /**
     * Creates a ContextMenu for the playlist
     *
     * @param plp
     *
     * @return
     */
    public ContextMenu playlistContextMenu(JFXListView<Playlist> plp)
    {
        ContextMenu cm = new ContextMenu();

        // Creates the option to replace the queue with the playlist
        MenuItem playPlaylist = new MenuItem("Play List");
        cm.getItems().add(playPlaylist);
        playPlaylist.setOnAction((Action) ->
        {
            setQueuePlay(plp
                    .getSelectionModel()
                    .getSelectedItem()
                    .getPlaylist());
        });

        // Creates the option to add the playlist to the queue
        MenuItem addPlaylist = new MenuItem("Add to Queue");
        cm.getItems().add(addPlaylist);
        addPlaylist.setOnAction((action) ->
        {
            setQueueAdd(plp
                    .getSelectionModel()
                    .getSelectedItem()
                    .getPlaylist());
        });

        // Creates the option to edit playlists
        MenuItem editPlaylist = new MenuItem("Edit Playlist");
        cm.getItems().add(editPlaylist);
        editPlaylist.setOnAction((event) ->
        {
            // Do not remove. This output is VERY important
            System.out.println("Thoust be changing thee order of musical arts.");

            // Gets the selected playlist
            Playlist pl = plp.getSelectionModel().getSelectedItem();

            createPlaylistWindow(pl);
        });

        return cm;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Commands">
    /**
     * Stops the current MediaPlayer
     */
    public void stopMediaPlayer()
    {
        if (playing)
        {
            this.queueMedia.get(currentSong).stop();
        }
    }

    /**
     * Updates the duration based on the current song
     */
    public void updateDuration()
    {
        this.mpduration = this.queueMedia.get(currentSong).getTotalDuration();
    }

    /**
     * Starts the current MediaPlayer
     */
    public void startMediaPlayer()
    {
        this.queueMedia.get(currentSong).play();
    }

    /**
     * Reverses the current looping boolean
     */
    public void reverseLooping()
    {
        this.looping = !this.looping;
    }

    /**
     * Pauses the current MediaPlayer
     */
    public void pauseMediaPlayer()
    {
        this.queueMedia.get(currentSong).pause();
    }

    /**
     * Skips to the given point in the song
     *
     * @param seconds The given point in seconds
     */
    public void seek(Duration seconds)
    {
        this.queueMedia.get(currentSong).seek(seconds);
    }

    /**
     * Adds a random song to the playlist
     * if no song has been selected by the user (Empty list & user clicks Play)
     */
    public void addElevatorMusic()
    {
        Music track;

        String title = "Elevator Music";
        String album = "PlaceHolder";
        String artist = "YouTube";

        Random rnd = new Random();
        int r = rnd.nextInt(3);
        System.out.println(r);
        switch (r)
        {
            case 0:
                track = new Music(0, title, album, artist, 2017,
                                  "./res/songs/placeholder",
                                  "Elevator (Control).mp3"
                );
                break;
            case 1:
                track = new Music(0, title, album, artist, 2017,
                                  "./res/songs/placeholder",
                                  "Elevator (Caverns).mp3"
                );
                break;
            default:
                track = new Music(0, title, album, artist, 2017,
                                  "./res/songs/placeholder",
                                  "elevatormusic.mp3"
                );
                break;
        }

        this.setPlaying(true);
        this.queue.add(track);
    }

    // COPY PASTED METHOD TO FORMAT TIME PROPERLY
    /**
     * Correctly formats the Duration into mm:ss format
     *
     * @param elapsed
     * @param duration
     *
     * @return
     */
    public static String formatTime(Duration elapsed, Duration duration)
    {
        int intElapsed = (int) Math.floor(elapsed.toSeconds());
        int elapsedHours = intElapsed / (60 * 60);
        if (elapsedHours > 0)
        {
            intElapsed -= elapsedHours * 60 * 60;
        }
        int elapsedMinutes = intElapsed / 60;
        int elapsedSeconds = intElapsed - elapsedHours * 60 * 60 - elapsedMinutes * 60;

        if (duration.greaterThan(Duration.ZERO))
        {
            int intDuration = (int) Math.floor(duration.toSeconds());
            int durationHours = intDuration / (60 * 60);
            if (durationHours > 0)
            {
                intDuration -= durationHours * 60 * 60;
            }
            int durationMinutes = intDuration / 60;
            int durationSeconds = intDuration - durationHours * 60 * 60 - durationMinutes * 60;
            if (durationHours > 0)
            {
                return String.format("%d:%02d:%02d/%d:%02d:%02d", elapsedHours, elapsedMinutes, elapsedSeconds, durationHours, durationMinutes, durationSeconds);
            }
            else
            {
                return String.format("%02d:%02d/%02d:%02d", elapsedMinutes, elapsedSeconds, durationMinutes, durationSeconds);
            }
        }
        else
        {
            if (elapsedHours > 0)
            {
                return String.format("%d:%02d:%02d", elapsedHours, elapsedMinutes, elapsedSeconds);
            }
            else
            {
                return String.format("%02d:%02d", elapsedMinutes, elapsedSeconds);
            }
        }
    }

    /**
     * Creates a grid for the Equalizer
     * Currently out of comission
     *
     * @param gridEqualizer
     * @param mPlayer
     */
    private void createEqualizerGrid(GridPane gridEqualizer, MediaPlayer mPlayer)
    {
        ObservableList<EqualizerBand> bands = mPlayer.getAudioEqualizer().getBands();

        bands.clear();

        double eqMin = EqualizerBand.MIN_GAIN;
        double eqMax = EqualizerBand.MAX_GAIN;
        double freq = START_FREQ;
        double median = eqMax - eqMin;

        for (int i = 0; i < AMOUNT_OF_BANDS; i++)
        {
            double theta = (double) i / (double) (AMOUNT_OF_BANDS - 1) * (2 * Math.PI);

            double scale = 0.4 * (1 + Math.cos(theta));

            double gain = eqMin + median + (median * scale);

            bands.add(new EqualizerBand(freq, freq / 2, gain));

            freq *= 2;
        }

        for (int i = 0; i < bands.size(); i++)
        {
            EqualizerBand eb = bands.get(i);

            //gridEqualizer.add(eb, 0, 0);
        }
    }

    /**
     * Search the storage for songs
     *
     * @param text    The text to search for
     * @param filters The filters to apply for the earch
     */
    public void songSearch(String text, ArrayList<String> filters)
    {
        try
        {
            List<Music> results = search.prepareSearch(filters, text);
            this.allSongs.clear();
            this.allSongs.addAll(results);
        }
        catch (BLLException ex)
        {
            System.out.println(ex.getMessage());
        }
    }

    /**
     * Opens new FXML Window, editsong.
     *
     * @param title
     * @param artist
     * @param time
     * @param file
     * @param genre
     * @param anchorPane
     *
     * @throws IOException
     * @throws SQLException
     */
    public void openEditSongWindow(String title, String artist, int time, String file, String genre, AnchorPane anchorPane) throws IOException, SQLException
    {
        File fxml = new File("src/mytunes/gui/view/EditSong.fxml");
        FXMLLoader fxLoader = new FXMLLoader(fxml.toURL());

        Parent root = fxLoader.load();
        EditSongController controller = fxLoader.getController();
        fxLoader.setController(controller);

        Scene scene = new Scene(root);
        Stage stage = new Stage();
        stage.setTitle("Edit song");
        stage.setScene(scene);
        controller.setData(title, artist, time, file, genre);
        controller.getSongIdFromMainController(songIdFromTable);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(anchorPane.getScene().getWindow());
        stage.show();
    }

    /**
     * Goes through the controller, takes the id of the song.
     *
     * @param id
     *
     * @return
     */
    public int getSongId(int id)
    {
        songIdFromTable = id;
        return id;
    }

    /**
     * Takes care of preparing the mediaplayer
     */
    public void prepareSetup()
    {
        setupMediaPlayer();
        enableSettings();
        timeChangeListener();

        lblAlbumCurrent.setText(getQueueList().get(currentSong).getAlbum());
        lblArtistCurrent.setText(getQueueList().get(currentSong).getArtist());
        lblDescriptionCurrent.setText(getQueueList().get(currentSong).getDescription());
        int[] minSec = getSecondsToMinAndHour(getQueueList().get(currentSong).getDuration());
        lblDurationCurrent.setText(String.valueOf(minSec[2]
                                                  + ":"
                                                  + minSec[1]
                                                  + ":"
                                                  + minSec[0]));
        lblGenreCurrent.setText(getQueueList().get(currentSong).getGenre());
        lblTitleCurrent.setText(getQueueList().get(currentSong).getTitle());
        lblYearCurrent.setText(String.valueOf(getQueueList().get(currentSong).getYear()));
    }

    /**
     * Takes care of preparing the mediaplayer and starting it
     */
    public void prepareAndPlay()
    {
        if (isPlaying())
        {
            stopMediaPlayer();
        }
        prepareSetup();
        getMediaPlayerStatus();
        startMediaPlayer();
        setPlaying(true);
        btnPlayPause.setText("Pause");
    }

    /**
     * Sets up the new media player
     */
    private void setupMediaPlayer()
    {
        // As soon as the media player is ready to play a song we allow for
        // manipulating the media file (playback speed, volume etc.)
        getMediaPlayer().setOnReady(() ->
        {
            updateDuration();
            progressSlider.setValue(0.0);
            progressSlider.setMax(getduration().toSeconds());
            getMediaPlayerStatus();
        });
    }

    /**
     * Enables the UI controls for the song
     */
    public void enableSettings()
    {
        volumeSlider.setDisable(false);
        btnLoop.setDisable(false);
        playbackSpeed.setDisable(false);
        progressSlider.setDisable(false);
        lblTimer.setDisable(false);
        progressSlider.setStyle("-fx-control-inner-background: #0E9654;");
    }

    /**
     * Adds a listener to the current mediaplayer's duration
     */
    public void timeChangeListener()
    {
        getMediaPlayer().currentTimeProperty().addListener((Observable ov) ->
        {
            updateSliderAndTimer();
        });
    }

    /**
     * Updates the slider and the timer
     */
    private void updateSliderAndTimer()
    {
        Duration currentTime = getCurrentTime();
        this.mpduration = getduration();

        lblTimer.setText(MainWindowModel.formatTime(currentTime, mpduration));

        // Adds a listener to the value, allowing it to automatically adjust to
        // where it is - displaying the progress to the user.
        getMediaPlayer().currentTimeProperty().addListener(
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
                seek(Duration.seconds(progressSlider.getValue()));
            }
        });
        //Above we determine if the user is dragging the progress slider, and here we determine what to do if the user clicks the progress bar
        progressSlider.setOnMouseClicked((MouseEvent mouseEvent) ->
        {
            seek(Duration.seconds(progressSlider.getValue()));
        });
    }
    //</editor-fold>

    private void deleteSong(int id)
    {
        try
        {
            bllManager.deleteSong(id);
        }
        catch (BLLException ex)
        {
            System.out.println(ex.getMessage());
        }
    }

    /**
     *
     * @param anchorPane
     *
     * @return
     */
    public AnchorPane getAnchorPaneController(AnchorPane anchorPane)
    {
        controllerAnchorPane = anchorPane;
        return anchorPane;
    }
}
