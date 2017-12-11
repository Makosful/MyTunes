package mytunes.bll;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javafx.collections.ObservableList;
import mytunes.be.Music;
import mytunes.be.Playlist;
import mytunes.bll.exception.BLLException;
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

    public List<Music> getSongList() throws BLLException
    {
        try
        {
            return songDAO.getAllSongs();
        }
        catch (SQLException ex)
        {
            throw new BLLException();
        }
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

    public List<Playlist> getPlaylists() throws SQLException
    {
        List<Playlist> playlists = plDAO.getPlaylists();
        for (int i = 0; i < playlists.size(); i++)
        {
            playlists.get(i).setPlaylist(
                    plDAO.getPlaylistSongs(
                            playlists.get(i).getId()
                    ));
        }
        return playlists;
    }

    /**
     * Removes a playlist from the database
     *
     * @param playlist
     *
     * @throws java.sql.SQLException
     */
    public void removePlaylist(Playlist playlist) throws SQLException
    {
        plDAO.removePlaylist(playlist.getId());
    }
    // Changes the song's info.

    public void editSongDataBase(String oldTitle, String newTitle, String oldArtist, String newArtist, int songId,
                                 String oldFile, String newFile, String oldGenre, String newGenre) throws SQLException
    {
        songDAO.editSong(oldTitle, newTitle, oldArtist, newArtist, songId, oldFile, newFile, oldGenre, newGenre);
    }

    /**
     * Determines which id should be used in the song table, if the
     * artist/album/genre
     * already exists get the id from those, else get the newly inserted id's
     *
     * @param song
     *
     * @throws SQLException
     */
    public void setRelationIds(Music song) throws SQLException
    {
        List<Integer> ids = new ArrayList();

        int artistId;
        int albumId;
        int genreId;
        int pathId;
        int locationId;

        //Determine if the location already is in the db, and get the resulting id
        int getLocationId = songDAO.getExistingLocation(song.getLocation());
        if (getLocationId != 0)
        {
            locationId = getLocationId;
        }
        else
        {
            locationId = songDAO.setLocation(song.getLocation());
        }

        // Determine if the location already is in the db, and get the resulting id
        // If the location(path) and pathname(something.mp3) already exsists in the
        // db stop the proccess of uploading to the db
        int getPathId = songDAO.getExistingPath(song.getSongPathName(), getLocationId);
        if (getPathId == 0)
        {

            //Determine if the artist already is in the db, and get the resulting id
            int getArtistId = songDAO.getExistingArtist(song.getArtist());
            if (getArtistId != 0)
            {

                artistId = getArtistId;
            }
            else
            {
                artistId = songDAO.setArtist(song.getArtist());
            }

            //Determine if the album already is in the db, and get the resulting id
            int getAlbumId = songDAO.getExistingAlbum(song.getAlbum());
            if (getAlbumId != 0)
            {

                albumId = getAlbumId;
            }
            else
            {
                albumId = songDAO.setAlbum(song.getAlbum());
            }

            //Determine if the genre already is in the db, and get the resulting id
            int getGenreId = songDAO.getExistingGenre(song.getGenre());
            if (getGenreId != 0)
            {

                genreId = getGenreId;

            }
            else
            {

                genreId = songDAO.setGenre(song.getGenre());

            }

            pathId = songDAO.setPath(song.getSongPathName(), locationId);

            ids.add(artistId);
            ids.add(albumId);
            ids.add(genreId);
            ids.add(pathId);

            songDAO.setSong(song, ids);

        }

    }

    /**
     * Gets the index of an item in a List
     *
     * @param item The object to look for
     * @param list The List to look in
     *
     * @return Returns the index of the item if it has been found. If the item
     *         does not exist in the List, thsi will return -1
     */
    public int getIndexOf(Music item, List<Music> list)
    {
        for (int i = 0; i < list.size(); i++)
        {
            if (list.get(i).equals(item))
            {
                return i;
            }
        }

        return -1;
    }

    public void savePlaylist(String title, ObservableList<Music> playlist) throws SQLException
    {
        int playId = plDAO.addPlaylist(title);

        for (int i = 0; i < playlist.size(); i++)
        {
            plDAO.insertPlaylistSong(playId, playlist.get(i).getId());
        }
    }

    public void updatePlaylist(Playlist playlist) throws SQLException
    {
        plDAO.updatePlaylist(playlist.getId(), playlist.getTitle());
    }
}
