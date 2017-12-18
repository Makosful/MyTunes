package mytunes.dal;

import com.microsoft.sqlserver.jdbc.SQLServerException;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import mytunes.be.Music;
import mytunes.be.Playlist;
import mytunes.dal.exception.DALException;

/**
 *
 * @author Axl
 */
public class PlaylistDAO
{

    DataBaseConnector db;
    SongDAO sDAO;

    public PlaylistDAO() throws IOException
    {

        db = new DataBaseConnector();
        sDAO = new SongDAO();

    }

    /**
     * gets all playlists
     *
     * @return all playlists in a ArrayList
     *
     * @throws mytunes.dal.exception.DALException
     */
    public List<Playlist> getPlaylists() throws DALException
    {
        try (Connection con = db.getConnection())
        {

            List<Playlist> playlists = new ArrayList<>();

            String sql = "SELECT Playlists.id, Playlists.playlist FROM Playlists";

            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(sql);

            while (rs.next())
            {
                Playlist pl = createPlaylistFromDB(rs);

                playlists.add(pl);
            }

            return playlists;

        }
        catch (SQLServerException ex)
        {
            throw new DALException();
        }
        catch (SQLException ex)
        {
            throw new DALException();
        }
    }

    /**
     * Adds a new playlist to the database
     *
     * @param playlistTitle
     *
     * @return id of inserted playlist
     *
     * @throws mytunes.dal.exception.DALException
     */
    public int addPlaylist(String playlistTitle) throws DALException
    {
        try (Connection con = db.getConnection())
        {
            int id;

            String sqlInsert = "INSERT INTO Playlists (playlist) VALUES (?)";
            PreparedStatement preparedStatementInsert = con.prepareStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS);
            preparedStatementInsert.setString(1, playlistTitle);
            preparedStatementInsert.executeUpdate();

            ResultSet rsi = preparedStatementInsert.getGeneratedKeys();

            rsi.next();

            id = rsi.getInt(1);

            return id;
        }
        catch (SQLServerException ex)
        {
            throw new DALException();
        }
        catch (SQLException ex)
        {
            throw new DALException();
        }

    }

    /**
     * Removes a playlist from the database
     *
     * @param id
     *
     * @throws mytunes.dal.exception.DALException
     */
    public void removePlaylist(int id) throws DALException
    {

        try (Connection con = db.getConnection())
        {
            String sqlDelete = "DELETE FROM Playlists WHERE id = ?";
            PreparedStatement preparedStatement = con.prepareStatement(sqlDelete);
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
        }
        catch (SQLServerException ex)
        {
            throw new DALException();
        }
        catch (SQLException ex)
        {
            throw new DALException();
        }

    }

    /**
     * Get all songs from a specific playlist
     * @param id
     *
     * @return
     *
     * @throws DALException
     */
    public ObservableList<Music> getPlaylistSongs(int id) throws DALException
    {
        try (Connection con = db.getConnection())
        {
            ObservableList<Music> allSongs = FXCollections.observableArrayList();

            String sql = "SELECT "
                         + "Songs.id, "
                         + "Songs.title, "
                         + "Artist.artist, "
                         + "Albums.album, "
                         + "Songs.releasedate, "
                         + "Location.location, "
                         + "Genres_test.genre, "
                         + "Path.pathname, "
                         + "Songs.description, "
                         + "Songs.duration "
                         + "FROM Songs "
                         + "INNER JOIN Artist ON Songs.artistid = Artist.id "
                         + "INNER JOIN Albums ON Songs.albumid = Albums.id "
                         + "INNER JOIN Path ON Songs.pathid = Path.id "
                         + "INNER JOIN Location ON Path.locationid = Location.id "
                         + "INNER JOIN Genre_test ON Songs.id = Genre_test.songid "
                         + "INNER JOIN Genres_test ON Genre_test.genreid = Genres_test.id "
                         + "INNER JOIN playlist_with_songs ON Songs.id = playlist_with_songs.songid "
                         + "WHERE playlist_with_songs.playlistid = ?";

            PreparedStatement preparedStatement = con.prepareStatement(sql);
            preparedStatement.setInt(1, id);
            ResultSet rs = preparedStatement.executeQuery();

            Music song = new Music();
            while (rs.next())
            {

                song = sDAO.createSongFromDB(rs, song);
                if (!allSongs.contains(song))
                {
                    allSongs.add(song);
                }
            }

            return allSongs;

        }
        catch (SQLServerException ex)
        {
            throw new DALException();
        }
        catch (SQLException ex)
        {
            throw new DALException();
        }
    }

    /**
     * Add a song to a specific playlist
     * @param playlistid
     * @param songid
     *
     * @throws DALException
     */
    public void insertPlaylistSong(int playlistid, int songid) throws DALException
    {
        try (Connection con = db.getConnection())
        {

            String sql = "INSERT INTO playlist_with_songs (playlistid, songid) VALUES (?, ?)";

            PreparedStatement preparedStatement = con.prepareStatement(sql);
            preparedStatement.setInt(1, playlistid);
            preparedStatement.setInt(2, songid);

            preparedStatement.executeUpdate();

        }
        catch (SQLServerException ex)
        {
            throw new DALException();
        }
        catch (SQLException ex)
        {
            throw new DALException();
        }

    }

    /**
     * Update the playlists name 
     * @param id
     * @param playlist
     *
     * @throws DALException
     */
    public void updatePlaylist(int id, String playlist) throws DALException
    {
        try (Connection con = db.getConnection())
        {

            String sql = "UPDATE playlists SET playlist = ? WHERE id = ?";

            PreparedStatement preparedStatement = con.prepareStatement(sql);
            preparedStatement.setString(1, playlist);
            preparedStatement.setInt(2, id);

            preparedStatement.executeUpdate();

        }
        catch (SQLServerException ex)
        {
            throw new DALException();
        }
        catch (SQLException ex)
        {
            throw new DALException();
        }
    }

    /**
     * Deletes a specific playlist
     * @param id
     *
     * @throws DALException
     */
    public void deletePlaylist(int id) throws DALException
    {
        try (Connection con = db.getConnection())
        {

            String sql = "DELETE Playlists FROM Playlists "
                         + "INNER JOIN playlist_with_songs ON Playlists.id = playlist_with_songs.playlistid "
                         + "WHERE Playlists.id = ?";

            PreparedStatement preparedStatement = con.prepareStatement(sql);
            preparedStatement.setInt(1, id);
            preparedStatement.execute();

        }
        catch (SQLServerException ex)
        {
            throw new DALException();
        }
        catch (SQLException ex)
        {
            throw new DALException();
        }
    }

    /**
     * Create a playlist object
     * @param rs
     * @return
     * @throws DALException 
     */
    private Playlist createPlaylistFromDB(ResultSet rs) throws DALException
    {
        try
        {
            Playlist pl = new Playlist();

            pl.setId(rs.getInt("id"));
            pl.setTitle(rs.getString("playlist"));

            return pl;
        }
        catch (SQLException ex)
        {
            throw new DALException();
        }
    }
    
}
