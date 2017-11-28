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
import javafx.collections.MapChangeListener;
import javafx.scene.media.Media;
import mytunes.be.Music;
import mytunes.dal.SongDAO;

/**
 *
 * @author B
 */
public class MetaData {
    
    private String artist;
    private String title;
    private String album;
    private String year;
    private String genre;
    private String description;
    private int iterations;
    
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
    public void MetaData(List<File> chosenFiles){
        
        
            List<Music> tracks = new ArrayList();

            Music track = new Music();
            
            for(int i = 0; i < chosenFiles.size(); i++){
            
                String fileString = chosenFiles.get(i).getAbsolutePath();

                Media song = new Media(new File(fileString.toLowerCase()).toURI().toString());
                
                 System.out.println(song.getMetadata().entrySet().toString()); 
                
                song.getMetadata().addListener((MapChangeListener<String, Object>) change -> {
                    
                    if (change.wasAdded()) {

                        if ("artist".equals(change.getKey())) 
                        {
                            artist = change.getValueAdded().toString();

                        } else if ("title".equals(change.getKey())) 
                        {
                            title = change.getValueAdded().toString();

                        } else if ("album".equals(change.getKey())) 
                        {
                            album = change.getValueAdded().toString();

                        } else if("year".equals(change.getKey()))
                        {
                            year = change.getValueAdded().toString();

                        } else if("genre".equals(change.getKey()))
                        {
                            genre = change.getValueAdded().toString();

                        } else if("comment-N".equals(change.getKey()))
                        {
                            description = change.getValueAdded().toString();

                        }
                        
                        System.out.println(iterations);
                        if(artist != null && title != null && album != null && year != null){
                            iterations++;
                            track.setArtist(artist);
                            track.setTitle(title);
                            track.setAlbum(album);
                            track.setYear(Integer.parseInt(year));
                            track.setGenre(genre);
                            track.SetDescription(description);
                            
                            tracks.add(track);
                        
                            finished(tracks);
                            
                        }
                        
                    }


                });
            
            }
/**            
            if(track.getArtist().isEmpty())
            {

                if(fileString.contains("."))
                {
                   track.setArtist(chosenFiles.get(i).getName().substring(0, chosenFiles.get(i).getName().lastIndexOf(".")));
                }

            }
**/
            
           
            
       
  
    }
    
    private void finished(List<Music> tracks)
    {
 
        
            System.out.println(tracks.get(0).getGenre());
     }
    
}
