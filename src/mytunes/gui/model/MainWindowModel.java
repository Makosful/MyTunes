package mytunes.gui.model;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.media.Media;
import mytunes.be.Music;
import mytunes.bll.BLLManager;
import mytunes.bll.MetaData;

/**
 *
 * @author Storm
 */
public class MainWindowModel
{

    private ObservableList<Music> list;

    // Objects
    private BLLManager bllManager;
    private MetaData metadata;
            
    public MainWindowModel() throws IOException
    {
        this.bllManager = new BLLManager();
        this.list = FXCollections.observableArrayList();
        this.metadata = new MetaData();
    }

    public ObservableList<Music> getSongList()
    {
        return list;
    }

    public void loadSongList()
    {
        list.clear();
        list.addAll(bllManager.getSongList());
    }
   /**
    * Saves song name to database.
    * @param setPath
    * @throws SQLException 
    */
    public void createSongPath(String setPath) throws SQLException
    {
        bllManager.createSongPath(setPath);
    }
    /**
     * Goes through song files, and gets their name.
     * Returns a list with their name.
     * @param chosenFiles
     * @return
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
    

    public void setPathAndName(List<File> chosenFiles) throws IOException {
        
        writeMusicFolderPath(chosenFiles.get(0).getAbsolutePath());
        
        for(int i = 0; i < chosenFiles.size(); i++){
            
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
     * Passes the chosenFiles to get the metadata from the mp3's in the metadata class
     * @param chosenFiles 
     */
    public void setMetaData(List<File> chosenFiles)
    {
        metadata.MetaData(chosenFiles);
    }
    
   
 
}
