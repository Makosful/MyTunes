package mytunes.bll;

import com.sun.javaws.Main;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import mytunes.be.Music;
import mytunes.be.Path;
import mytunes.dal.PlaylistDAO;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;




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
    private int duration;
    private String description;
    private String songPathName;
    private String location;
    
    
    BLLManager bllManager;
    PlaylistDAO pDAO;
    
    public MetaData() throws IOException
    {
        this.bllManager = new BLLManager();
        this.pDAO = new PlaylistDAO();
    }
    
    
        /**
         * 
         * @param chosenFiles
         * @throws FileNotFoundException
         * @throws IOException
         * @throws org.jaudiotagger.audio.exceptions.CannotReadException
         * @throws org.jaudiotagger.audio.exceptions.ReadOnlyFileException
         * @throws org.jaudiotagger.tag.TagException
         * @throws org.jaudiotagger.audio.exceptions.InvalidAudioFrameException
         */
        public void MetaData(List<File> chosenFiles) throws FileNotFoundException, 
                                                            IOException,
                                                            CannotReadException,
                                                            TagException,
                                                            ReadOnlyFileException,
                                                            InvalidAudioFrameException
        {


            List<Music> tracks = new ArrayList();



            for(int i = 0; i < chosenFiles.size(); i++){


                setMetaData(chosenFiles.get(i));

                validateMetaData(chosenFiles.get(i));

                Music track = createMusicObject();

                tracks.add(track);


            }

            
            tracks.forEach((track) ->
            {
                try
                {
                    bllManager.setRelationIds(track);
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
        private void setMetaData(File chosenFile)throws FileNotFoundException,
                                                        IOException,
                                                        CannotReadException,
                                                        TagException,
                                                        ReadOnlyFileException,
                                                        InvalidAudioFrameException
        {
            
 

             
            File file = chosenFile;
            AudioFile f = AudioFileIO.read(file);
            Tag tag = f.getTag();

            
             
            artist = tag.getFirst(FieldKey.ARTIST);
            title = tag.getFirst(FieldKey.TITLE);
            album = tag.getFirst(FieldKey.ALBUM);
            year = tag.getFirst(FieldKey.YEAR);
            genre = tag.getFirst(FieldKey.GENRE);
            description = tag.getFirst(FieldKey.COMMENT);
            duration = f.getAudioHeader().getTrackLength();
            songPathName = chosenFile.getName();
            location = chosenFile.getAbsolutePath().replace(songPathName, "");
            
    
        }  
            



        /**
         * 
         * @param chosenFile 
         */
        private void validateMetaData(File chosenFile)
        {

            if(artist.equals("") && title.equals(""))
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
                    artist = "artist";

                    title = songName;
                }


            }
            else if(artist.equals(""))
            {
                artist = "artist";
            }
            else if(title.equals(""))
            {
                title = "title";
            }
            
            
            
            if(album.equals(""))
            {
                album = "album";
            }
            
            if(genre.equals(""))
            {
                genre = "genre";
            }
            
            if(year.equals(""))
            {
                year = "1337";
            }
            
            if(description.equals(""))
            {
                description = "No description";
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
            track.setDuration(duration);
            track.setSongPathName(songPathName);
            track.SetLocation(location);
            
            return track;
        }
}
