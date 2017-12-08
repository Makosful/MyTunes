package mytunes.gui.model;

import java.io.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.media.EqualizerBand;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;
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
        int queueSize = this.queue.size() - 1;
        System.out.println(queueSize);

        // If queue does not have next stop playing
        // If queue has next play next
        currentSong++;
        setSong(this.queueMedia.get(currentSong).getMedia());
        startMediaPlayer();
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
                                  "./res/songs/placeholder/Elevator (Control).mp3");
                break;
            case 1:
                track = new Music(0, title, album, artist, 2017,
                                  "./res/songs/placeholder/Elevator (Caverns).mp3");
                break;
            default:
                track = new Music(0, title, album, artist, 2017,
                                  "./res/songs/placeholder/elevatormusic.mp3");
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
    //</editor-fold>

    
    /**
     * Formats seconds to hh:mm:ss
     * @param seconds
     * @return 
     */
    public int[] getSecondsToMinAndHour(int seconds)
    {
        int minutes = seconds / 60;
        
        seconds -= minutes*60;
        
        int hours = minutes / 60;
        
        minutes -= hours*60;
        
        int[] minSec = new int[3];
        
        minSec[0] = seconds;
        
        minSec[1] = minutes;
        
        minSec[2] = hours;
        
        return minSec;
       
    }

}
