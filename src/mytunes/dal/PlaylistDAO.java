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
     * @throws SQLException
     */
    public List<Playlist> getPlaylists() throws SQLException
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
    }

    /**
     * Adds a new playlist to the database
     *
     * @param playlistTitle
     *
     * @return id of inserted playlist
     *
     * @throws com.microsoft.sqlserver.jdbc.SQLServerException
     */
    public int addPlaylist(String playlistTitle) throws SQLServerException, SQLException
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

    }

    /**
     * Removes a playlist from the database
     *
     * @param id
     *
     * @throws com.microsoft.sqlserver.jdbc.SQLServerException
     * @throws java.sql.SQLException
     */
    public void removePlaylist(int id) throws SQLServerException, SQLException
    {

        try (Connection con = db.getConnection())
        {
            String sqlDelete = "DELETE FROM Playlists WHERE id = ?";
            PreparedStatement preparedStatement = con.prepareStatement(sqlDelete);
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
        }

    }

    public ObservableList<Music> getPlaylistSongs(int id) throws SQLServerException, SQLException
    {
        try (Connection con = db.getConnection())
        {
            ObservableList<Music> allSongs = FXCollections.observableArrayList();

            String sql = "SELECT "
                         + "Songs.id, "
                         + "Songs.title, "
                         + "Artist.artist, "
                         + "Albums.album, "
                         + "Genre.genre, "
                         + "Songs.releasedate, "
                         + "Location.location, "
                         + "Path.pathname, "
                         + "Songs.description, "
                         + "Songs.duration "
                         + "FROM Songs "
                         + "INNER JOIN Artist ON Songs.artistid = Artist.id "
                         + "INNER JOIN Albums ON Songs.albumid = Albums.id "
                         + "INNER JOIN Genre ON Songs.genreid = Genre.id "
                         + "INNER JOIN Path ON Songs.pathid = path.id "
                         //+ "INNER JOIN Location on Songs.locationid = location.id "
                         + "INNER JOIN Location on Path.locationid = location.id "
                         + "INNER JOIN playlist_with_songs ON Songs.id = playlist_with_songs.songid "
                         + "WHERE playlist_with_songs.playlistid = ?";

            PreparedStatement preparedStatement = con.prepareStatement(sql);
            preparedStatement.setInt(1, id);
            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next())
            {
                Music song = sDAO.createSongFromDB(rs);
                allSongs.add(song);
            }

            return allSongs;

        }

    }

    public void insertPlaylistSong(int playlistid, int songid) throws SQLServerException, SQLException
    {
        try (Connection con = db.getConnection())
        {

            String sql = "INSERT INTO playlist_with_songs (playlistid, songid) VALUES (?, ?)";

            PreparedStatement preparedStatement = con.prepareStatement(sql);
            preparedStatement.setInt(1, playlistid);
            preparedStatement.setInt(2, songid);

            preparedStatement.executeUpdate();

        }

    }

    public void updatePlaylist(int id, String playlist) throws SQLException
    {
        try (Connection con = db.getConnection())
        {

            String sql = "UPDATE playlists SET playlist = ? WHERE id = ?";

            PreparedStatement preparedStatement = con.prepareStatement(sql);
            preparedStatement.setString(1, playlist);
            preparedStatement.setInt(2, id);

            preparedStatement.executeUpdate();

        }
    }

    public void deletePlaylist(int id) throws SQLServerException, SQLException
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
    }

    private Playlist createPlaylistFromDB(ResultSet rs) throws SQLException
    {
        Playlist pl = new Playlist();

        pl.setId(rs.getInt("id"));
        pl.setTitle(rs.getString("playlist"));

        return pl;
    }
}
