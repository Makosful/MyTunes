package mytunes.bll;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import mytunes.be.Music;
import mytunes.dal.SongDAO;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;
import org.jaudiotagger.tag.id3.ID3v23Tag;
import org.jaudiotagger.tag.id3.ID3v24Tag;
import org.jaudiotagger.tag.wav.WavTag;



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
                    sDAO.setSong(track);
                    //sDAO.getSongsFromSearch();
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
            duration = String.valueOf(f.getAudioHeader().getTrackLength());
            songPathName = chosenFile.getName();
          


    
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
            
            if(duration.equals(""))
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
            track.setDuration(Integer.parseInt(duration));
            track.setSongPathName(songPathName);
            
            return track;
        }
}
