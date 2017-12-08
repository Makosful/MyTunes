package mytunes.gui.model;

import java.io.IOException;
import java.sql.SQLException;
import mytunes.bll.BLLManager;

/**
 *
 * @author Hussain
 */
public class EditSongModel
{

    BLLManager bllManager;

    public EditSongModel() throws IOException
    {
        this.bllManager = new BLLManager();
    }
    // Changes the song's info.

    public void editSongDatabase(String oldTitle, String newTitle, String oldArtist, String newArtist, int songId,
                                 String oldFile, String newFile, String oldGenre, String newGenre) throws SQLException
    {
        bllManager.editSongDataBase(oldTitle, newTitle, oldArtist, newArtist, songId, oldFile, newFile, oldGenre, newGenre);
    }

}
