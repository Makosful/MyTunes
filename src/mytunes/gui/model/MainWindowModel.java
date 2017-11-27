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

/**
 *
 * @author Storm
 */
public class MainWindowModel
{

    private ObservableList<Music> list;

    // Objects
    private BLLManager bllManager;

    public MainWindowModel() throws IOException
    {
        this.bllManager = new BLLManager();
        this.list = FXCollections.observableArrayList();
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
     *  Adds the mp3's metadata to music objects, then adds them to an arraylist and sends it to bllManager
     * @param chosenFiles 
     */
    public void setMetaData(List<File> chosenFiles)
    {
        List<Music> tracks = new ArrayList();
        
        for(int i = 0; i < chosenFiles.size(); i++){
            
            Music track = new Music();
            
            String fileString = chosenFiles.get(0).getAbsolutePath();
            
            Media song = new Media(new File(fileString.toLowerCase()).toURI().toString());
              
            song.getMetadata().addListener((MapChangeListener<String, Object>) change -> {
               
                if (change.wasAdded()) {
                                    
                   
                    if ("artist".equals(change.getKey())) 
                    {
                        track.setArtist(change.getValueAdded().toString());
                      
                    } else if ("title".equals(change.getKey())) 
                    {
                        track.setTitle(change.getValueAdded().toString());
                    
                    } else if ("album".equals(change.getKey())) 
                    {
                        track.setAlbum(change.getValueAdded().toString());
                      
                    } else if("year".equals(change.getKey()))
                    {
                        track.setYear(Integer.parseInt(change.getValueAdded().toString()));
                       
                    } else if("genre".equals(change.getKey()))
                    {
                        track.setGenre(change.getValueAdded().toString());
                       
                    } else if("comment-N".equals(change.getKey()))
                    {
                        track.SetDescription(change.getValueAdded().toString());
                       
                    } else if("composer".equals(change.getKey()))
                    {
                        String whiteSpaceStart;
                        String whiteSpaceEnd;
                        
                        if(track.getDescription().isEmpty()){
                            whiteSpaceStart = "";
                            whiteSpaceEnd = " ";
                        }else {
                            whiteSpaceStart = " ";
                            whiteSpaceEnd = "";
                        }
                        
                        track.SetDescription(whiteSpaceStart+"Composers: "+change.getValueAdded().toString()+whiteSpaceEnd);
                       
                    }
                }
           
            });
            
            if(track.getArtist().isEmpty())
            {
                if(fileString.contains("."))
                {
                   track.setArtist(chosenFiles.get(i).getName().substring(0, chosenFiles.get(i).getName().lastIndexOf(".")));
                }
            }
            
            tracks.add(track);
       }
        
       bllManager.setSongs(tracks);
    }
}
