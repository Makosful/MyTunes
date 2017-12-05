package mytunes.dal;

import com.microsoft.sqlserver.jdbc.SQLServerException;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
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
     * @return
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
     */
    public int addPlaylist(String playlistTitle) throws SQLServerException, SQLException
    {
        try (Connection con = db.getConnection())
        {
            int id;

            String sqlInsert = "INSERT INTO Playlists SET (playlist) VALUES (?)";
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

    public List<Music> getPlaylistSongs(int id) throws SQLServerException, SQLException
    {
        try (Connection con = db.getConnection())
        {
            List<Music> allSongs = new ArrayList<>();

            String sql = "SELECT Songs.title, Artist.artist, Albums.album, Albums.releasedate, Genre.genre, Path.pathname "
                         + "FROM Songs "
                         + "INNER JOIN Artist ON Songs.artistid = Artist.id "
                         + "INNER JOIN Albums ON Songs.albumid = Albums.id "
                         + "INNER JOIN Genre ON Songs.genreid = Genre.id "
                         + "INNER JOIN Path ON Songs.pathid = path.id "
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

    public void updatePlaylist(Playlist playlist)
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private Playlist createPlaylistFromDB(ResultSet rs) throws SQLException
    {
        Playlist pl = new Playlist();

        pl.setId(rs.getInt("id"));
        pl.setTitle("playlist");

        return pl;
    }
}
