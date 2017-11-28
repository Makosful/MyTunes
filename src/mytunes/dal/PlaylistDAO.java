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

        Playlist fav = new Playlist("My favorites");
        list.add(fav);

        for (int i = 0; i < 10; i++)
        {
            Playlist pl = new Playlist("test" + i);
            list.add(pl);
        }

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

    /**
     * Removes a playlist from the database
     *
     * @param playlist
     */
    public void removePlaylist(Playlist playlist)
    {
    }
}
