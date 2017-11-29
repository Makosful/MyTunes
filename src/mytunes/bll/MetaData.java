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
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import mytunes.be.Music;
import mytunes.dal.SongDAO;
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
    private String year;
    private String genre;
    private String duration;
    private String description;
    private String songPathName;
    
    
    SongDAO sDAO;

    public MetaData() throws IOException
    {
        this.sDAO = new SongDAO();
    }
    
    
        /**
         * 
         * @param chosenFiles
         * @throws FileNotFoundException
         * @throws IOException
         * @throws SAXException
         * @throws TikaException 
         */
        public void MetaData(List<File> chosenFiles) throws FileNotFoundException, IOException, SAXException, TikaException{


            List<Music> tracks = new ArrayList();



            for(int i = 0; i < chosenFiles.size(); i++){


                setMetaData(chosenFiles.get(i));

                validateMetaData(chosenFiles.get(i));

                Music track = createMusicObject();

                tracks.add(track);

                /*
                System.out.println("Artist: "+tracks.get(i).getArtist());   
                System.out.println("Title: "+tracks.get(i).getTitle());
                System.out.println("Genre: "+tracks.get(i).getGenre());
                System.out.println("Album: "+tracks.get(i).getAlbum());
                System.out.println("Description: "+tracks.get(i).getDescription());
                System.out.println("Year: "+tracks.get(i).getYear());
                System.out.println("Duration: "+tracks.get(i).getDuration());
                */

            }

            
            tracks.forEach((track) ->
            {
                try
                {
                    sDAO.setSong(track);
                }
                catch (SQLException ex)
                {
                    System.out.println(ex.getMessage());
                }
            });
           
           

        }
    

    

        /**
         * 
         * @param fileString
         * @throws FileNotFoundException
         * @throws IOException
         * @throws SAXException
         * @throws TikaException 
         */
        private void setMetaData(File chosenFile)throws FileNotFoundException, IOException, SAXException, TikaException
        {


            Metadata meta;
            try (InputStream input = new FileInputStream(new File(chosenFile.getAbsolutePath()))) {
                ContentHandler handler = (ContentHandler) new DefaultHandler();
                meta = new Metadata();
                Parser parser = new Mp3Parser();
                ParseContext parseCtx  = new ParseContext();
                parser.parse(input, handler, meta, parseCtx);
            }


            artist = meta.get("xmpDM:artist");
            title = meta.get("title");
            album = meta.get("xmpDM:album");
            year = meta.get("xmpDM:releaseDate");
            genre = meta.get("xmpDM:genre");
            description = meta.get("xmpDM:logComment"); 
            duration = meta.get("xmpDM:duration");     
            
            songPathName = chosenFile.getName();
            
        }
    


        /**
         * 
         * @param chosenFile 
         */
        private void validateMetaData(File chosenFile)
        {

            if(artist == null && title == null)
            {
                String songName = chosenFile.getName();

                if(songName.contains("."))
                {
                   songName = songName.substring(0, chosenFile.getName().lastIndexOf("."));
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

                    title = "title";
                }


            }
            else if(artist == null)
            {
                artist = "artist";
            }
            else if(title == null)
            {
                title = "title";
            }
            
            
            
            if(album == null)
            {
                album = "album";
            }
            
            if(genre == null)
            {
                genre = "genre";
            }
            
            if(year == null)
            {
                year = "1337";
            }
            
            if(description == null)
            {
                description = "No description";
            }
            
            if(duration == null)
            {
                description = "0";
            }

        }

        
        
        
        
        
        /**
         * 
         * @return 
         */
        private Music createMusicObject()
        {
            Music track = new Music();
            
            track.setArtist(artist);
            track.setTitle(title);
            track.setGenre(genre);
            track.setAlbum(album);
            track.SetDescription(description);
            track.setYear(Integer.parseInt(year));
            track.setDuration(Double.parseDouble(duration));
            track.setSongPathName(songPathName);
            
            return track;
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
