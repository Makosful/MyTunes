package mytunes.gui.model;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;
import javafx.stage.Stage;
import javafx.util.Duration;
import mytunes.be.Music;
import mytunes.be.Playlist;
import mytunes.bll.BLLManager;
import mytunes.gui.controller.CreatePlaylistWindowController;

/**
 *
 * @author Storm
 */
public class MainWindowModel
{

    // Lists
    private ObservableList<Music> allSongs;
    private ObservableList<Music> queue;
    private ObservableList<Playlist> playlists;

    // Objects
    private BLLManager bllManager;

    //<editor-fold defaultstate="collapsed" desc="Instance Variables">
    private boolean playing;
    private boolean looping;

    private MediaPlayer mPlayer;
    private Duration mpduration;
    private Media song;
    private MediaPlayer.Status mStatus;

    private Media currentlyPlaying;
    private List<Media> medias;

    private int i = 0;
    private File newFile;
    private List<File> pathNames;
    //</editor-fold>

    /**
     * Constructor
     */
    public MainWindowModel()
    {
        try
        {
            this.bllManager = new BLLManager();
            this.allSongs = FXCollections.observableArrayList();
            this.queue = FXCollections.observableArrayList();
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
    public void loadSongList()
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
        this.queue.clear();
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
     */
    public void loadPlaylists()
    {
        playlists.addAll(bllManager.getPlaylists());
    }

    /**
     * Displays the window for creating new playlists
     */
    public void createPlaylistWindow()
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

            //plCont.setSongList(this.allSongs);
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

                // Saves the playlist to storage
                bllManager.addPlaylist(pl); // Not implimented yet
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
    //</editor-fold>

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

    public void setPathAndName(List<File> chosenFiles) throws IOException
    {

        writeMusicFolderPath(chosenFiles.get(0).getAbsolutePath());
        for (int i = 0; i < chosenFiles.size(); i++)

        {
            System.out.println(chosenFiles.get(i).getName());
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

    public void setPlaying(boolean isPlaying)
    {
        this.playing = isPlaying;
    }

    public boolean isPlaying()
    {
        return playing;
    }

    public void updateStatus()
    {
        this.mStatus = this.mPlayer.getStatus();
    }

    public Status getMediaStatus()
    {
        return this.mStatus;
    }

    public void stopMediaPlayer()
    {
        this.mPlayer.stop();
    }

    public void updateDuration()
    {
        this.mpduration = this.mPlayer.getTotalDuration();
    }

    public MediaPlayer getMediaPlayer()
    {
        return this.mPlayer;
    }

    public void startMediaPlayer()
    {
        this.mPlayer.play();
    }

    public List<Media> getMedias()
    {
        return this.medias;
    }

    public void setMediaPlayer(MediaPlayer mediaPlayer)
    {
        this.mPlayer = mediaPlayer;
    }

    public void reverseLooping()
    {
        this.looping = !this.looping;
    }

    public void pauseMediaPlayer()
    {
        this.mPlayer.pause();
    }

    public void setLooping()
    {
        this.mPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        this.looping = false;
    }

    public double getVolume()
    {
        return (this.mPlayer.getVolume() * 100.0) / 100.0;
    }

    public void setVolume(double value)
    {
        this.mPlayer.setVolume(value);
        if (value > 3 && value < 0)
        {
            mPlayer.setVolume(5);
        }
    }

    public Duration getCurrentTime()
    {
        return this.mPlayer.getCurrentTime();
    }

    public Duration getduration()
    {
        return this.mpduration;
    }

    public void seek(Duration seconds)
    {
        this.mPlayer.seek(seconds);
    }

    public void setSong(Media media)
    {
        this.song = media;

        this.setMediaPlayer(new MediaPlayer(this.song));
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
        int r = rnd.nextInt(2) + 2;
        if (r > 2)
        {
            track = new Music(0,
                              title,
                              album,
                              artist,
                              2017,
                              "./res/songs/placeholder/Elevator (Control).mp3");
        }
        else if (r > 3)
        {
            track = new Music(0,
                              title,
                              album,
                              artist,
                              2017,
                              "./res/songs/placeholder/Elevator (Caverns).mp3");
        }
        else
        {
            track = new Music(0,
                              title,
                              album,
                              artist,
                              2017,
                              "./res/songs/placeholder/elevatormusic.mp3");
        }

        this.setPlaying(true);
        this.queue.add(track);
    }

    public void newMedias()
    {
        this.medias = new ArrayList<>();
    }
}
