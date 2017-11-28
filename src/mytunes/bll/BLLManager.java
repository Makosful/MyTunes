package mytunes.bll;

import java.io.IOException;
import java.sql.SQLException;
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
        return mm.getAllSongs();
    }

    public void createSongPath(String setPath) throws SQLException
    {
        songDAO.createSongPath(setPath);
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
}
