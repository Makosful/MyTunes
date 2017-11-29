/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mytunes.bll;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import mytunes.be.Music;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.parser.mp3.Mp3Parser;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


/**
 *
 * @author B
 */
public class MetaData {
       
    private String title;
    private String artist;
    private String album;
    private int year;
    private int duration;
    private String description;
    private String location;
    
    
    
    public void MetaData(List<File> chosenFiles) throws FileNotFoundException, IOException, SAXException, TikaException{
            
            String name = "C:\\Users\\B\\Desktop\\Davids mappe\\datamatiker\\Javafx\\MetaData\\media\\Elevator (Control).mp3";
            
            List<Music> tracks = new ArrayList();

         
            
            for(int i = 0; i < chosenFiles.size(); i++){
                
                Music track = new Music(0, null, null, null, 0);
                
                String fileString = chosenFiles.get(i).getAbsolutePath();


                Metadata meta;
                try (InputStream input = new FileInputStream(new File(fileString))) {
                    ContentHandler handler = (ContentHandler) new DefaultHandler();
                    meta = new Metadata();
                    Parser parser = new Mp3Parser();
                    ParseContext parseCtx  = new ParseContext();
                    parser.parse(input, handler, meta, parseCtx);
                }

              
                track.setArtist(meta.get("xmpDM:artist"));
                track.setTitle(meta.get("title"));
                track.setAlbum(meta.get("xmpDM:album"));
                track.setYear(Integer.parseInt(meta.get("xmpDM:releaseDate")));
//                track.setGenre(meta.get("xmpDM:genre"));
                track.SetDescription(meta.get("xmpDM:logComment")); 
                //track.setComposer(meta.get("xmpDM:composer"));
              //  track.setDuration(Integer.parseInt(meta.get("xmpDM:duration")));
                
                
                
                //System.out.println("artist:"+meta.get("xmpDM:artist"));
               // System.out.println("title:"+meta.get("title"));
               // System.out.println("releasedate:"+meta.get("xmpDM:releaseDate"));
               // System.out.println("genre:"+meta.get("xmpDM:genre"));
               // System.out.println("composer:"+meta.get("xmpDM:composer"));
               // System.out.println("genre:"+meta.get("xmpDM:logComment"));
               
               
                if(track.getArtist().isEmpty())
                {
                    String songName = chosenFiles.get(i).getName();
                    if(songName.contains("."))
                    {
                       songName = songName.substring(0, chosenFiles.get(i).getName().lastIndexOf("."));
                    }
                    
                    if(songName.contains("-"))
                    {
                        String[] artistAndTitle = songName.split("-", 2);
                        
                        artist = artistAndTitle[0];
                        
                        title = artistAndTitle[1];
                    }
                    else
                    {
                        artist = songName;
                    }
                    
                    System.out.println("artist: "+artist+" title:"+title);
                    
                    
                }

                
                tracks.add(track);
        
            }
            
          System.out.println(tracks.get(0).getArtist()+"!!!Virker");  
    
    
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    //OLD CODE
    /*
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
         
            if(track.getArtist().isEmpty())
            {

                if(fileString.contains("."))
                {
                   track.setArtist(chosenFiles.get(i).getName().substring(0, chosenFiles.get(i).getName().lastIndexOf(".")));
                }

            }

            
           
            
       
  
    }
    
    private void finished(List<Music> tracks)
    {
 
        
            System.out.println(tracks.get(0).getGenre());
     }
 **/   
}
