package mytunes.gui.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import mytunes.be.Music;
import mytunes.bll.BLLManager;

/**
 *
 * @author Storm
 */
public class MainWindowModel
{

    // Lists
    private final ObservableList<Music> allSongs;
    private final ObservableList<String> queue;

    // Objects
    private final BLLManager bllManager;

    /**
     * Constructor
     */
    public MainWindowModel()
    {
        this.bllManager = new BLLManager();
        this.allSongs = FXCollections.observableArrayList();
        this.queue = FXCollections.observableArrayList();
    }

    //<editor-fold defaultstate="collapsed" desc="Getters">
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
     * Gets the queue list
     *
     * @return The list containing the queues
     */
    public ObservableList<String> getQueueList()
    {
        return queue;
    }
    //</editor-fold>

    /**
     * Loads all the songs into the program
     */
    public void loadSongList()
    {
        allSongs.clear();
        allSongs.addAll(bllManager.getSongList());
    }

    public void clearQueueList()
    {
        this.queue.clear();
    }
}
