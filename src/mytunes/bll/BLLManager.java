package mytunes.bll;

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

    public BLLManager()
    {
        this.songDAO = new SongDAO();
    }

    public List<Music> getSongList()
    {
        return songDAO.getAllSongs();
    }
}
