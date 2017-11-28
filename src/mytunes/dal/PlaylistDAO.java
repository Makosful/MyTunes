package mytunes.dal;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import mytunes.be.Playlist;

/**
 *
 * @author Axl
 */
public class PlaylistDAO
{

    public PlaylistDAO()
    {
    }

    public ObservableList<Playlist> getPlaylists()
    {
        ObservableList<Playlist> list = FXCollections.observableArrayList();

        return list;
    }

    /**
     * Adds a new playlist to the database
     *
     * @param playlist
     */
    public void addPlaylist(Playlist playlist)
    {
    }
}
