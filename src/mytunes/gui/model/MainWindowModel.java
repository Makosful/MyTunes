package mytunes.gui.model;

import java.io.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import mytunes.be.Music;
import mytunes.be.Playlist;
import mytunes.bll.BLLManager;
import mytunes.bll.MetaData;
import mytunes.gui.controller.CreatePlaylistWindowController;
import org.apache.tika.exception.TikaException;
import org.xml.sax.SAXException;

/**
 *
 * @author Storm
 */
public class MainWindowModel
{

    // Lists
    private ObservableList<Music> allSongs;
    private ObservableList<String> queue;
    private ObservableList<Playlist> playlists;

    // Objects
    private BLLManager bllManager;
    private MetaData metadata;

    /**
     * Constructor
     */
    public MainWindowModel()
    {
        try
        {
            this.metadata = new MetaData();
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
    public ObservableList<String> getQueueList()
    {
        return queue;
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
     * Handles everything about creating new playlists and adding them to the
     * list
     */
    public void createPlaylistWindow()
    {
        try
        {
            // Gets a hold of the FXML and controller
            File fxml = new File("./src/MyTunes/gui/view/CreatePlaylistWindow.fxml");
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
            Playlist pl = new Playlist(plCont.getTitle());
            this.playlists.add(pl);

            // Saves the playlist to storage
            bllManager.addPlaylist(pl); // Not implimented yet
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
            for (int i = 0; i < playlists.size(); i++)
            {
                this.playlists.remove(playlists.get(i));
            }
        }
    }
    //</editor-fold>

    public List<String> checkIfIsInDatabase() throws SQLException
    {
        return bllManager.checkIfIsInDatabase();
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

    
    
    public void setMetaData(List<File> chosenFiles) throws IOException, FileNotFoundException, TikaException, SAXException 
    {
        
            metadata.MetaData(chosenFiles);

    }
    
}







   