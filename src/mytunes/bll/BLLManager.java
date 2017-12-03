package mytunes.bll;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javafx.collections.ObservableList;
import mytunes.be.Music;
import mytunes.be.Playlist;
import mytunes.dal.MockMusic;
import mytunes.dal.PlaylistDAO;
import mytunes.dal.SongDAO;

/**
 *
 * @author Axl
 */
public class BLLManager
{

    private final SongDAO songDAO;
    private final PlaylistDAO plDAO;
    private final MockMusic mm;

    public BLLManager() throws IOException
    {
        this.songDAO = new SongDAO();
        this.plDAO = new PlaylistDAO();
        this.mm = new MockMusic();
    }

    public List<Music> getSongList() throws IOException
    {
        return mm.getAllSongsLocal();
    }

    public void createSongPath(String setPath) throws SQLException
    {
        //songDAO.createSongPath(setPath);
    }

    public List<String> checkIfIsInDatabase() throws SQLException
    {
//        return songDAO.checkIfIsInDatabase();
        return null;
    }

    public ObservableList<Playlist> getPlaylists()
    {
        return plDAO.getPlaylists();
    }

    /**
     * Adds a new playlist to the storage
     *
     * @param playlist
     */
    public void addPlaylist(Playlist playlist)
    {
        plDAO.addPlaylist(playlist);
    }

    /**
     * Removes a playlist from the database
     *
     * @param playlist
     */
    public void removePlaylist(Playlist playlist)
    {
        plDAO.removePlaylist(playlist);
    }
    
    
    
    
     /**
     * Determines with id should be used in the song table, if the artist/album/genre 
     * already exists get the id from those, else get the newly inserted id's
     * @param song
     * @throws SQLException 
     */
    public void setRelationIds(Music song) throws SQLException
    {
        List<Integer> ids = new ArrayList();
        
        int artistId;
        int albumId;
        int genreId;
        int pathId;
        
        //Determine if the artist already is in the db, and get the resulting id
        int getArtistId = songDAO.getExistingArtist(song.getArtist());
        if(getArtistId != 0){
            
            artistId = getArtistId;
        }
        else
        {
            artistId = songDAO.setArtist(song.getArtist());
        }
        
        //Determine if the album already is in the db, and get the resulting id
        int getAlbumId = songDAO.getExistingAlbum(song.getAlbum());
        if(getAlbumId != 0){ 
            
            albumId = getAlbumId;
        }
        else
        {
            albumId = songDAO.setAlbum(song.getAlbum(), song.getYear());
        }
        
        //Determine if the genre already is in the db, and get the resulting id
        int getGenreId = songDAO.getExistingGenre(song.getGenre());
        if(getGenreId != 0){
            
            genreId = getGenreId;
            
        }
        else
        {
            genreId = songDAO.setGenre(song.getGenre());
        }


        pathId = songDAO.setPath(song.getSongPathName());
        
        ids.add(artistId);
        ids.add(albumId);
        ids.add(genreId);
        ids.add(pathId);
        
        songDAO.setSong(song, ids);
       
    }
    
    
    
}
