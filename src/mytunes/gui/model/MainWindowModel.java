package mytunes.gui.model;

import com.jfoenix.controls.*;
import java.io.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
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
import javafx.scene.input.MouseEvent;
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
import mytunes.gui.controller.CreatePlaylistWindowController;
import mytunes.gui.controller.EditSongController;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.TagException;

/**
 *
 * @author Storm
 */
public class MainWindowModel
{

    // Lists
    private ObservableList<Music> allSongs;
    private ObservableList<Music> queue;
    private ObservableList<MediaPlayer> queueMedia;
    private ObservableList<Playlist> playlists;
    private ObservableList<Media> medias;

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
            this.progressSlider.setDisable(false);
            this.volumeSlider = new JFXSlider();
            this.volumeSlider.setDisable(true);
            this.lblmPlayerStatus = new Label();
            this.lblTimer = new Label("00:00");

            this.lblAlbumCurrent = new Label();
            this.lblArtistCurrent = new Label();
            this.lblDescriptionCurrent = new Label();
            this.lblDurationCurrent = new Label();
            this.lblGenreCurrent = new Label();
            this.lblTitleCurrent = new Label();
            this.lblYearCurrent = new Label();
        }
        catch (IOException ex)
        {
            System.out.println(ex.getMessage());
        }
    }

    //<editor-fold defaultstate="collapsed" desc="Song List">
    /**
     * Loads all the songs into the program
     */
    public void loadSongList() throws SQLException
    {
        try
        {
            allSongs.clear();
            allSongs.addAll(bllManager.getSongList());
        }
        catch (IOException ex)
        {
            System.out.println(ex.getMessage());
        }
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
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Queue List">
    /**
     * Clears the Queue List entirely
     */
    public void clearQueueList()
    {
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

    /**
     * Gets the next song in the queue
     *
     * Gets ahold of the new song in queue by checking the index of the list.
     * When the media is ready to play we set the duration of the new
     * MediaPlayer
     *
     * //TODO : Needs to add MORE than just mpduration (I think?)
     */
    public void getNewSongInQue()
    {
        newMedias();
        stopMediaPlayer();

        for (int index = 0; index < getQueueList().size(); index++)
        {
            newFile = new File(getQueueList().get(index).getLocation());
            song = new Media(newFile.toURI().toString());
            getMedias().add(song);
        }
        getMediaPlayer().setOnReady(() ->
        {
            updateDuration();
        });
    }

    private void playNextSong()
    {
        int queueSize = getQueueList().size() - 1;

        // If the currently playing song is at an index smaller than the queue
        // size, then it has a next
        if (getCurrentSong() < queueSize)
        {
            currentSongNext();
            setSong(getQueueListMedia().get(getCurrentSong()).getMedia());
            prepareAndPlay();
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
     * @throws java.sql.SQLException
     */
    public void loadPlaylists() throws SQLException
    {
        playlists.addAll(bllManager.getPlaylists());
    }

    /**
     * Displays the window for creating new playlists
     *
     * @throws java.sql.SQLException
     */
    public void createPlaylistWindow() throws SQLException
    {
        try
        {
            // Gets a hold of the FXML and controller
            File fxml = new File("src/mytunes/gui/view/CreatePlaylistWindow.fxml");
            FXMLLoader fxLoader = new FXMLLoader(fxml.toURL());

            // Loads the window
            Parent root = fxLoader.load();
            Scene scene = new Scene(root);
            Stage stage = new Stage();

            // Gets the controller for the window, so we can retrieve data after
            // it's been closed
            CreatePlaylistWindowController plCont = fxLoader.getController();

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
            if (plCont.shouldSave())
            {
                Playlist pl = new Playlist(plCont.getTitle());
                pl.setPlaylist(plCont.getPlaylist());
                this.playlists.add(pl);
            }
        }
        catch (IOException ex)
        {
            System.out.println(ex.getMessage());
        }
    }

    /**
     * Displays the window for creating new playlists
     *
     * @param playlist
     *
     * @throws java.sql.SQLException
     */
    public void createPlaylistWindow(Playlist playlist) throws SQLException
    {
        try
        {
            // Gets a hold of the FXML and controller
            File fxml = new File("src/mytunes/gui/view/CreatePlaylistWindow.fxml");
            FXMLLoader fxLoader = new FXMLLoader(fxml.toURL());

            // Loads the window
            Parent root = fxLoader.load();
            Scene scene = new Scene(root);
            Stage stage = new Stage();

            // Gets the controller for the window, so we can retrieve data after
            // it's been closed
            CreatePlaylistWindowController plCont = fxLoader.getController();
            plCont.setPlaylist(playlist);
            System.out.println("");
            plCont.setSaveButton("Save");

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
            if (plCont.shouldSave())
            {
                playlist.setTitle(plCont.getTitle());
                playlist.getPlaylist().clear();
                playlist.getPlaylist().addAll(plCont.getPlaylist());
                bllManager.updatePlaylist(playlist);
            }
        }
        catch (IOException ex)
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
            // Do nothing
        }
        else
        {
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

    public void savePlaylist(String title, ObservableList<Music> playlist) throws SQLException
    {
        bllManager.savePlaylist(title, playlist);
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

    public boolean isPlaying()
    {
        return playing;
    }

    public void updateStatus()
    {
//        this.mStatus = this.mPlayer.getStatus();
        this.mStatus = this.queueMedia.get(currentSong).getStatus();
    }

    public Status getMediaStatus()
    {
        return this.mStatus;
    }

    public MediaPlayer getMediaPlayer()
    {
//        return this.mPlayer;
        return this.queueMedia.get(currentSong);
    }

    public List<Media> getMedias()
    {
        return this.medias;
    }

    public double getVolume()
    {
//        return (this.mPlayer.getVolume() * 100.0) / 100.0;
        return (this.queueMedia.get(currentSong).getVolume() * 100.0) / 100.0;
    }

    public Duration getCurrentTime()
    {
//        return this.mPlayer.getCurrentTime();
        return this.queueMedia.get(currentSong).getCurrentTime();
    }

    public Duration getduration()
    {
        return this.mpduration;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Setters">
    /**
     *
     * @param chosenFiles
     *
     * @return
     *
     * @throws IOException
     * @throws CannotReadException
     * @throws FileNotFoundException
     * @throws ReadOnlyFileException
     * @throws TagException
     * @throws InvalidAudioFrameException
     */
    public List<Music> setMetaData(List<File> chosenFiles) throws IOException,
                                                                  CannotReadException,
                                                                  FileNotFoundException,
                                                                  ReadOnlyFileException,
                                                                  TagException,
                                                                  InvalidAudioFrameException
    {

        return meta.MetaData(chosenFiles);

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

    //<editor-fold defaultstate="collapsed" desc="Commands">
    public void stopMediaPlayer()
    {
//        this.mediaPlayer.stop();
        if (playing)
        {
            this.queueMedia.get(currentSong).stop();

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
                        progressSlider.setMax(getMediaPlayer()
                                .getTotalDuration()
                                .toSeconds());
                        getMediaPlayerStatus();
                        break;
                    default:
                        break;
                }
            }
        }
    }

    public void updateDuration()
    {
//        this.mpduration = this.mediaPlayer.getTotalDuration();
        this.mpduration = this.queueMedia.get(currentSong).getTotalDuration();
    }

    public void startMediaPlayer()
    {
//        this.mediaPlayer.play();
        this.queueMedia.get(currentSong).play();
    }

    public void reverseLooping()
    {
        this.looping = !this.looping;
    }

    public void pauseMediaPlayer()
    {
//        this.mediaPlayer.pause();
        this.queueMedia.get(currentSong).pause();
    }

    public void seek(Duration seconds)
    {
//        this.mediaPlayer.seek(seconds);
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
                                  "./res/songs/placeholder");
                track.setSongPathName("Elevator (Control).mp3");
                break;
            case 1:
                track = new Music(0, title, album, artist, 2017,
                                  "./res/songs/placeholder");
                track.setSongPathName("Elevator (Caverns).mp3");
                break;
            default:
                track = new Music(0, title, album, artist, 2017,
                                  "./res/songs/placeholder");
                track.setSongPathName("elevatormusic.mp3");
                break;
        }

        this.setPlaying(true);
        this.queue.add(track);
    }

    public void newMedias()
    {
        this.medias = FXCollections.observableArrayList();
    }

    // COPY PASTED METHOD TO FORMAT TIME PROPERLY
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
     *
     * @throws java.sql.SQLException
     */
    public void songSearch(String text, ArrayList<String> filters) throws SQLException
    {
        List<Music> results = search.prepareSearch(filters, text);
        this.allSongs.clear();
        this.allSongs.addAll(results);
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
    public void openEditSongWindow(String title, String artist, int time, String file, String genre) throws IOException, SQLException
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
        stage.initOwner(btnPlayPause.getScene().getWindow());
        stage.show();
        controller.closeWindow();
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
    //</editor-fold>

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

    public ContextMenu tableContextMenu(TableView<Music> tbl)
    {
        ContextMenu cm = new ContextMenu();

        // Gets the selected items
        ObservableList<Music> items = tbl
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
                Music musicInfo = tbl.getSelectionModel().getSelectedItem();
                getSongId(musicInfo.getId());
                String title = musicInfo.getTitle();
                String artist = musicInfo.getArtist();
                int time = musicInfo.getDuration();
                String genre = musicInfo.getGenre();
                String pathName = musicInfo.getSongPathName();

                openEditSongWindow(title, artist, time, pathName, genre);
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

        return cm;
    }

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

            try
            {
                createPlaylistWindow(pl);
            }
            catch (SQLException ex)
            {
                System.out.println(ex.getMessage());
            }
        });

        return cm;
    }

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

    public void getMediaPlayerStatus()
    {
        getMediaPlayer().statusProperty().addListener((observable,
                                                       oldValue,
                                                       newValue)
                -> lblmPlayerStatus.setText("MediaPlayer Status: "
                                            + newValue.toString().toLowerCase()));
    }

    /**
     * A collection of things we execute when we prepare the setups
     */
    public void prepareSetup()
    {
        setupMediaPlayer();
        enableSettings();
        timeChangeListener();

        lblAlbumCurrent.setText(getQueueList().get(currentSong).getAlbum());
        lblArtistCurrent.setText(getQueueList().get(currentSong).getArtist());
        lblDescriptionCurrent.setText(getQueueList().get(currentSong).getDescription());
        lblDurationCurrent.setText(String.valueOf(getQueueList().get(currentSong).getDuration()));
        lblGenreCurrent.setText(getQueueList().get(currentSong).getGenre());
        lblTitleCurrent.setText(getQueueList().get(currentSong).getTitle());
        lblYearCurrent.setText(String.valueOf(getQueueList().get(currentSong).getYear()));
    }

    /**
     * A preperation of our setup, followed by the play function
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

    private void setupMediaPlayer()
    {
        //choosingFiles(); //Needs a fix as mentioned in the method

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

    public void enableSettings()
    {
        volumeSlider.setDisable(false);
        btnLoop.setDisable(false);
        playbackSpeed.setDisable(false);
        progressSlider.setDisable(false);
        lblTimer.setDisable(false);
        progressSlider.setStyle("-fx-control-inner-background: #0E9654;");
    }

    public void timeChangeListener()
    {
        getMediaPlayer().currentTimeProperty().addListener((Observable ov) ->
        {
            updateSliderAndTimer();
        });
    }

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

    //<editor-fold defaultstate="collapsed" desc="Property Getters">
    public DoubleProperty getProgressSliderValueProperty()
    {
        return progressSlider.valueProperty();
    }

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

    public BooleanProperty getPlaybackSpeedDisabledProperty()
    {
        return playbackSpeed.disableProperty();
    }

    public BooleanProperty getTimerDisableProperty()
    {
        return lblTimer.disableProperty();
    }

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

    public void fxmlNextSong()
    {
        stopMediaPlayer();
        skipToNextSong();
        prepareAndPlay();
    }

    public void fxmlPrevSong()
    {
        stopMediaPlayer();
        skipToPrevSong();
        prepareAndPlay();
    }

    public void fxmlClearQueue()
    {
        // Checks if the queue is empty
        if (!getQueueList().isEmpty())
        // If it's not empty, stop all songs from playing
        {
            // Call the method to stop the song
            fxmlSongStop();
        }

        // Clears the queue list
        clearQueueList();

        setPlaying(false);
        btnPlayPause.setText("Play");
    }

    public void fxmlLoadMediaFiles(TableView tblSongList)
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
                addedMusic = setMetaData(chosenFiles);
                loadSongList();
                tblSongList.setItems(getSongList());
                getQueueList().addAll(addedMusic);
                prepareSetup();
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

        if (!getQueueList().isEmpty())
        {
            prepareSetup();
        }
    }

    public void fxmlDeletePlaylist(JFXListView playlistPanel)
    {
        ObservableList<Playlist> selectedItems = playlistPanel
                .getSelectionModel().getSelectedItems();
        deletePlaylists(selectedItems);
    }

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

    public void fxmlLoopAction()
    {
        reverseLooping();

        // If our loop slide-button is enabled we change the text, set the cycle
        // count to indefinite and reverse the boolean
        if (btnLoop.isSelected() == true)
        {
            btnLoop.setText("Loop: ON");
            setLooping();
            System.out.println("Looping on");
        }
        else if (btnLoop.isSelected() != true)
        {
            btnLoop.setText("Loop: OFF");
            reverseLooping();
            System.out.println("Looping off");
        }
    }

    public void fxmlMusicPlayPause()
    {
        if (getQueueList().isEmpty() && !isPlaying())
        {
            enableSettings();
            addElevatorMusic();
            prepareSetup();
            startMediaPlayer();
        }
        else if (!isPlaying())
        {
            //Needs to set the BEFORE media is played (apparently?)
            timeChangeListener();
            startMediaPlayer();
            setPlaying(true);
            btnPlayPause.setText("Pause");
            enableSettings();
        }
        // if the boolean is true we shall stop playing, reverse the boolean and edit the buttons text.
        else
        {
            pauseMediaPlayer();
            setPlaying(false);
            btnPlayPause.setText("Play");
        }
    }

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

    public void fxmlPlaybackSpeed()
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
        setPlayckSpeed(playbackIndex);
    }
}
