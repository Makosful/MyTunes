/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mytunes.bll;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.MapChangeListener;
import javafx.scene.media.Media;
import mytunes.be.Music;
import mytunes.dal.SongDAO;

/**
 *
 * @author B
 */
public class MetaData {
    
    SongDAO songDAO;
    
    public MetaData()
    {
        try {
            this.songDAO = new SongDAO();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }
    
    /**
    *  Adds the mp3's metadata to music objects, then adds them to an arraylist and sends it to bllManager
    * @param chosenFiles 
    */
    public MetaData(List<File> chosenFiles){
        
        
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
        
       songDAO.setSongs(tracks);
       
    }
}
