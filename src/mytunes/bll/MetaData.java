package mytunes.bll;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import mytunes.be.Music;
import mytunes.bll.exception.BLLException;
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
public class MetaData
{

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

    public MetaData()
    {
        try
        {
            this.bllManager = new BLLManager();
        }
        catch (BLLException ex)
        {
            System.out.println(ex.getMessage());
        }
        try
        {
            this.pDAO = new PlaylistDAO();
        }
        catch (IOException ex)
        {
            System.out.println(ex.getMessage());
        }
    }

    /**
     * For each music file get the metadata, validate it, create the music
     * objects
     * then return the music objects/songs/tracks and send them to the db
     *
     * @param chosenFiles
     *
     * @return list of songs
     *
     * @throws mytunes.bll.exception.BLLException
     */
    public List<Music> MetaData(List<File> chosenFiles) throws BLLException
    {

        List<Music> tracks = new ArrayList();

        for (int i = 0; i < chosenFiles.size(); i++)
        {

            try
            {
                setMetaData(chosenFiles.get(i));

                validateMetaData(chosenFiles.get(i));

                Music track = createMusicObject();

                tracks.add(track);
            }
            catch (IOException
                   | CannotReadException
                   | TagException
                   | ReadOnlyFileException
                   | InvalidAudioFrameException ex)
            {
                throw new BLLException();
            }

        }

        tracks.forEach((track) ->
        {

            try
            {
                bllManager.setRelationIds(track);
            }
            catch (BLLException ex)
            {
                System.out.println(ex.getMessage());
            }
        
        });

        return tracks;
    }

    /**
     * retrieve the meta data from the specific song
     *
     * @param fileString
     *
     * @throws FileNotFoundException
     * @throws IOException
     * @throws SAXException
     * @throws TikaException
     */
    private void setMetaData(File chosenFile) throws FileNotFoundException,
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
        //"res/songs";
        
    }

    /**
     * check if some meta data is missing and replace the emypy ones with
     * placeholders
     *
     * @param chosenFile
     */
    private void validateMetaData(File chosenFile)
    {

        if (artist.equals("") && title.equals(""))
        {
            String songName = chosenFile.getName();

            if (songName.contains("."))
            {
                songName = songName.substring(0, chosenFile.getName().lastIndexOf("."));
            }

            if (songName.contains("-"))
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
        else if (artist.equals(""))
        {
            artist = "artist";
        }
        else if (title.equals(""))
        {
            title = "title";
        }

        if (album.equals(""))
        {
            album = "album";
        }

        if (genre.equals(""))
        {
            genre = "genre";
        }

        if (year.equals(""))
        {
            year = "1337";
        }

        if (description.equals(""))
        {
            description = "No description";
        }

    }

    /**
     * Create the music object
     *
     * @return the music object/song
     */
    private Music createMusicObject()
    {
        Music track = new Music();

        track.setArtist(artist);
        track.setTitle(title);
        track.setGenre(genre);
        track.setAlbum(album);
        track.SetDescription(description);
        track.setYear(Integer.parseInt(year.trim()));
        track.setDuration(duration);
        track.setSongPathName(songPathName);
        track.SetLocation(location);

        return track;
    }
}
