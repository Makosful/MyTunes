package mytunes.bll;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import mytunes.be.Music;
import mytunes.dal.SongDAO;

/**
 *
 * @author Axl
 */
public class BLLManager
{

    private final SongDAO songDAO;

    public BLLManager() throws IOException
    {
        this.songDAO = new SongDAO();
    }

    public List<Music> getSongList()
    {
        return songDAO.getAllSongs();
    }
    
    public void createSongPath(String setPath) throws SQLException
    {
        songDAO.createSongPath(setPath);
    }

    /**
     * TODO
     * @param tracks 
     */
    public void setSongs(List<Music> tracks) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
