package mytunes.bll;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javafx.collections.ObservableList;
import mytunes.be.Music;
import mytunes.be.Playlist;
import mytunes.bll.exception.BLLException;
import mytunes.dal.PlaylistDAO;
import mytunes.dal.SongDAO;
import mytunes.dal.exception.DALException;

/**
 *
 * @author Axl
 */
public class BLLManager
{

    private final SongDAO songDAO;
    private final PlaylistDAO plDAO;

    /**
     *
     * @throws BLLException
     */
    public BLLManager() throws BLLException
    {
        try
        {
            this.songDAO = new SongDAO();
            this.plDAO = new PlaylistDAO();
        }
        catch (IOException ex)
        {
            throw new BLLException();
        }
    }

    /**
     *
     * @return
     * @throws BLLException
     */
    public List<Music> getSongList() throws BLLException
    {
        try
        {
            return songDAO.getAllSongs();
        }
        catch (DALException ex)
        {
            throw new BLLException();
        }
    }

    /**
     *
     * @param setPath
     *
     * @throws SQLException
     */
    public void createSongPath(String setPath) throws SQLException
    {
        //songDAO.createSongPath(setPath);
    }

    /**
     *
     * @return
     * @throws SQLException
     */
    public List<String> checkIfIsInDatabase() throws SQLException
    {
//        return songDAO.checkIfIsInDatabase();
        return null;
    }

    /**
     *
     * @return
     * @throws BLLException
     */
    public List<Playlist> getPlaylists() throws BLLException
    {
        try
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
        catch (DALException ex)
        {
            throw new BLLException();
        }
    }

    /**
     * Removes a playlist from the database
     *
     * @param playlist
     *
     * @throws mytunes.bll.exception.BLLException
     */
    public void removePlaylist(Playlist playlist) throws BLLException
    {
        try
        {
            plDAO.removePlaylist(playlist.getId());
        }
        catch (DALException ex)
        {
            throw new BLLException();
        }
    }
    // Changes the song's info.

    /**
     *
     * @param newTitle
     * @param newArtist
     * @param songId
     * @param oldFile
     * @param newFile
     * @param oldGenre
     * @param newGenre
     * @param addGenres
     *
     * @throws BLLException
     */
    public void editSongDataBase(String newTitle, String newArtist, int songId,
                                 String oldFile, String newFile, String oldGenre, String newGenre, boolean addGenres) throws BLLException
    {
        try
        {
            songDAO.editSong(newTitle, newArtist, songId, oldFile, newFile);
            if (addGenres == true)
            {
                insertGenres(songId, newGenre);
            }
            else
            {
                replaceGenre(songId, oldGenre, newGenre);
            }
        }
        catch (DALException ex)
        {
            throw new BLLException();
        }

    }

    /**
     * Determines which id should be used in the song table, if the
     * artist/album/genre
     * already exists get the id from those, else get the newly inserted id's
     *
     * @param song
     *
     * @throws mytunes.bll.exception.BLLException
     */
    public void setRelationIds(Music song) throws BLLException
    {
        try
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
            // If the location(path) and pathname(something.mp3) already exists in the
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

                pathId = songDAO.setPath(song.getSongPathName(), locationId);

                ids.add(artistId);
                ids.add(albumId);
                ids.add(pathId);

                int songId = songDAO.setSong(song, ids);

                //insert the songs genres in the db
                insertGenres(songId, song.getGenre());

            }
        }
        catch (DALException ex)
        {
            throw new BLLException();
        }

    }

    /**
     * Splits the genre string into separate genres.
     * Inserts the specific songs genre, by testing if the genre already exists
     * in the db, if it does use the id from this genre and relate it to this
     * song
     * else create the genre in the db and link it to the song
     *
     * @param songId
     * @param genre
     *
     * @throws mytunes.bll.exception.BLLException
     */
    public void insertGenres(int songId, String genre) throws BLLException
    {

        try
        {
            int genreTestId;
            String[] genres = genre.split(" ");
            for (String specificGenre : genres)
            {

                int getGenreTestId = songDAO.getExistingTestGenre(specificGenre);
                if (getGenreTestId != 0)
                {

                    genreTestId = getGenreTestId;

                }
                else
                {

                    genreTestId = songDAO.setTestGenre(specificGenre);

                }
                songDAO.setTestGenres(songId, genreTestId);
            }
        }
        catch (DALException ex)
        {
            throw new BLLException();
        }

    }

    /**
     * Deletes the songs reference to the old genre, then calls insertGenres to
     * either create or use existing genre as the specific songs genre reference
     *
     * @param songId
     * @param oldGenre
     * @param newGenre
     *
     * @throws mytunes.bll.exception.BLLException
     */
    public void replaceGenre(int songId, String oldGenre, String newGenre) throws BLLException
    {
        try
        {
            String[] genres = oldGenre.split(" ");
            for (String specificGenre : genres)
            {
                int getGenreTestId = songDAO.getExistingTestGenre(specificGenre);
                songDAO.removeSongsGenre(songId, getGenreTestId);
            }

            insertGenres(songId, newGenre);
        }
        catch (DALException | BLLException ex)
        {
            throw new BLLException();
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

    /**
     *
     * @param title
     * @param playlist
     *
     * @throws BLLException
     */
    public void savePlaylist(String title, ObservableList<Music> playlist) throws BLLException
    {
        try
        {
            int playId = plDAO.addPlaylist(title);

            for (int i = 0; i < playlist.size(); i++)
            {
                plDAO.insertPlaylistSong(playId, playlist.get(i).getId());
            }
        }
        catch (DALException ex)
        {
            throw new BLLException();
        }
    }

    /**
     *
     * @param playlist
     *
     * @throws BLLException
     */
    public void updatePlaylist(Playlist playlist) throws BLLException
    {
        try
        {
            plDAO.updatePlaylist(playlist.getId(), playlist.getTitle());
        }
        catch (DALException ex)
        {
            throw new BLLException();
        }
    }

    /**
     *
     * @return
     * @throws BLLException
     */
    public List<String> getAllGenres() throws BLLException
    {

        try
        {
            List<String> allGenres = songDAO.getAllGenres();

            return allGenres;
        }
        catch (DALException ex)
        {
            throw new BLLException();
        }
    }

    /**
     *
     * @param id
     *
     * @throws BLLException
     */
    public void deleteSong(int id) throws BLLException
    {
        try
        {
            songDAO.deleteSong(id);
        }
        catch (DALException ex)
        {
            throw new BLLException();
        }
    }

    /**
     *
     * @param id
     *
     * @throws BLLException
     */
    public void deletePlaylist(int id) throws BLLException
    {
        try
        {
            plDAO.deletePlaylist(id);
        }
        catch (DALException ex)
        {
            throw new BLLException();
        }

    }
}
