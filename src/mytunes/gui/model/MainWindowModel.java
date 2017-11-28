package mytunes.gui.model;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
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
    private ObservableList<Music> allSongs;
    private ObservableList<String> queue;

    // Objects
    private BLLManager bllManager;

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
        }
        catch (IOException ex)
        {
            Logger.getLogger(MainWindowModel.class.getName()).log(Level.SEVERE, null, ex);
        }
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

    /**
     * Saves song name to database.
     *
     * @param setPath
     *
     * @throws SQLException
     */
    public void createSongPath(String setPath) throws SQLException
    {
        bllManager.createSongPath(setPath);
    }

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
            songPath.add(chosenFiles.get(i).getName());
        return songPath;
    }

    public void setPathAndName(List<File> chosenFiles) throws IOException
    {

        writeMusicFolderPath(chosenFiles.get(0).getAbsolutePath());
        for (int i = 0; i < chosenFiles.size(); i++)

            System.out.println(chosenFiles.get(i).getName());
    }

    public void writeMusicFolderPath(String path) throws IOException
    {

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("path.txt")))
        {
            writer.write(path);
        }
    }

    public void clearQueueList()
    {
        this.queue.clear();
    }
    
    public List<String> checkIfIsInDatabase() throws SQLException
    {
       return bllManager.checkIfIsInDatabase();
    }
}
