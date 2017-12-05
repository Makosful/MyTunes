package mytunes.dal;

import com.microsoft.sqlserver.jdbc.SQLServerException;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import mytunes.be.Music;

/**
 *
 * @author Axl
 */
public class SongDAO
{

    DataBaseConnector db;

    public SongDAO() throws IOException
    {
        db = new DataBaseConnector();
    }

    public List<Music> getAllSongs() throws SQLServerException, SQLException
    {
        try (Connection con = db.getConnection())
        {

            List<Music> allSongs = new ArrayList<>();

            String sql = "SELECT Songs.title, Songs.releasedate, Songs.description, Songs.duration, Artist.artist, Albums.album, Genre.genre, Path.pathname, Location.location "
                         + "FROM Songs "
                         + "INNER JOIN Artist ON Songs.artistid = Artist.id "
                         + "INNER JOIN Albums ON Songs.albumid = Albums.id "
                         + "INNER JOIN Genre ON Songs.genreid = Genre.id "
                         + "INNER JOIN Path ON Songs.pathid = Path.id "
                         + "INNER JOIN Location ON Path.locationid = Location.id ";

            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(sql);

            while (rs.next())
            {
                Music song = createSongFromDB(rs);

                allSongs.add(song);
            }

            return allSongs;

        }
    }

    /**
     * Gets the ids and inserts the song parameters/ creates the song in the
     * songs table
     *
     * @param song
     * @param relationIds
     *
     * @throws SQLServerException
     * @throws SQLException
     */
    public void setSong(Music song, List<Integer> relationIds) throws SQLServerException, SQLException
    {

        int artistId = relationIds.get(0);
        int albumId = relationIds.get(1);
        int genreId = relationIds.get(2);
        int pathId = relationIds.get(3);

        try (Connection con = db.getConnection())
        {

            String sqlInsert = "INSERT INTO Songs VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement preparedStatementInsert = con.prepareStatement(sqlInsert);
            preparedStatementInsert.setString(1, song.getTitle());
            preparedStatementInsert.setInt(2, artistId);
            preparedStatementInsert.setInt(3, albumId);
            preparedStatementInsert.setInt(4, genreId);
            preparedStatementInsert.setInt(5, pathId);
            preparedStatementInsert.setString(6, song.getDescription());
            preparedStatementInsert.setInt(7, song.getDuration());
            preparedStatementInsert.setInt(8, song.getYear());
            preparedStatementInsert.executeUpdate();

        }
    }

    /**
     * If the artist already exsists in the artist table get the id
     *
     * @param artist
     *
     * @return
     *
     * @throws SQLServerException
     * @throws SQLException
     */
    public int getExistingArtist(String artist) throws SQLServerException, SQLException
    {
        try (Connection con = db.getConnection())
        {
            int id = 0;

            String sqlSelect = "SELECT id FROM Artist WHERE artist = ?";
            PreparedStatement preparedStatement = con.prepareStatement(sqlSelect, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, artist);
            ResultSet rs = preparedStatement.executeQuery();

            if (rs.next())
            {
                id = rs.getInt("id");
            }

            return id;
        }
    }

    /**
     * Insert the artist and get the artistId
     *
     * @param artist
     *
     * @return id
     *
     * @throws SQLServerException
     * @throws SQLException
     */
    public int setArtist(String artist) throws SQLServerException, SQLException
    {

        try (Connection con = db.getConnection())
        {
            int id;

            String sqlInsert = "INSERT INTO Artist (artist) VALUES (?)";
            PreparedStatement preparedStatementInsert = con.prepareStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS);
            preparedStatementInsert.setString(1, artist);
            preparedStatementInsert.executeUpdate();

            ResultSet rsi = preparedStatementInsert.getGeneratedKeys();

            rsi.next();

            id = rsi.getInt(1);

            return id;
        }

    }

    /**
     * If the album already exsists in the album table get the id
     *
     * @param album
     *
     * @return
     *
     * @throws SQLServerException
     * @throws SQLException
     */
    public int getExistingAlbum(String album) throws SQLServerException, SQLException
    {
        try (Connection con = db.getConnection())
        {
            int id = 0;

            String sqlSelect = "SELECT id FROM Albums WHERE album = ?";
            PreparedStatement preparedStatement = con.prepareStatement(sqlSelect, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, album);
            ResultSet rs = preparedStatement.executeQuery();

            if (rs.next())
            {
                id = rs.getInt("id");

            }

            return id;
        }
    }

    /**
     * Insert the album and get the artistId
     *
     * @param album
     *
     * @return
     *
     * @throws SQLServerException
     * @throws SQLException
     */
    public int setAlbum(String album) throws SQLServerException, SQLException
    {

        try (Connection con = db.getConnection())
        {
            int id;

            String sqlInsert = "INSERT INTO Albums (album) VALUES (?)";
            PreparedStatement preparedStatementInsert = con.prepareStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS);
            preparedStatementInsert.setString(1, album);
            preparedStatementInsert.executeUpdate();

            ResultSet rsi = preparedStatementInsert.getGeneratedKeys();

            rsi.next();

            id = rsi.getInt(1);

            return id;
        }

    }

    /**
     * If the genre already exsists in the genre table get the id
     *
     * @param genre
     *
     * @return
     *
     * @throws SQLServerException
     * @throws SQLException
     */
    public int getExistingGenre(String genre) throws SQLServerException, SQLException
    {
        try (Connection con = db.getConnection())
        {
            int id = 0;

            String sqlSelect = "SELECT id FROM Genre WHERE genre = ?";
            PreparedStatement preparedStatement = con.prepareStatement(sqlSelect, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, genre);
            ResultSet rs = preparedStatement.executeQuery();

            if (rs.next())
            {
                id = rs.getInt("id");

            }

            return id;
        }
    }

    /**
     * Insert the genre and get the genreId
     *
     * @param genre
     *
     * @return
     *
     * @throws SQLServerException
     * @throws SQLException
     */
    public int setGenre(String genre) throws SQLServerException, SQLException
    {

        try (Connection con = db.getConnection())
        {
            int id;

            String sqlInsert = "INSERT INTO Genre (genre) VALUES (?)";
            PreparedStatement preparedStatementInsert = con.prepareStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS);
            preparedStatementInsert.setString(1, genre);
            preparedStatementInsert.executeUpdate();

            ResultSet rsi = preparedStatementInsert.getGeneratedKeys();

            rsi.next();

            id = rsi.getInt(1);

            return id;
        }

    }

    /**
     * If the path already exsists in the path table get the id
     *
     * @param path
     * @param locationid
     *
     * @return
     *
     * @throws SQLServerException
     * @throws SQLException
     */
    public int getExistingPath(String path, int locationid) throws SQLServerException, SQLException
    {
        try (Connection con = db.getConnection())
        {
            int id = 0;

            String sqlSelect = "SELECT id FROM Path WHERE pathname = ? AND locationid = ?";
            PreparedStatement preparedStatement = con.prepareStatement(sqlSelect, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, path);
            preparedStatement.setInt(2, locationid);
            ResultSet rs = preparedStatement.executeQuery();

            if (rs.next())
            {
                id = rs.getInt("id");

            }

            return id;
        }
    }

    /**
     * Sets the path/name of the mp3 file and returns the id
     *
     * @param songPathName
     * @param locationId
     *
     * @return
     *
     * @throws SQLServerException
     * @throws SQLException
     */
    public int setPath(String songPathName, int locationId) throws SQLServerException, SQLException
    {
        try (Connection con = db.getConnection())
        {
            int id;

            String sqlInsert = "INSERT INTO Path (pathname, locationid) VALUES (?, ?)";
            PreparedStatement preparedStatementInsert = con.prepareStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS);
            preparedStatementInsert.setString(1, songPathName);
            preparedStatementInsert.setInt(2, locationId);
            preparedStatementInsert.executeUpdate();

            ResultSet rsi = preparedStatementInsert.getGeneratedKeys();

            rsi.next();

            id = rsi.getInt(1);

            return id;

        }

    }

    /**
     * If the loaction already exsists in the loaction table get the id
     *
     * @param location
     *
     * @return
     *
     * @throws SQLServerException
     * @throws SQLException
     */
    public int getExistingLocation(String location) throws SQLServerException, SQLException
    {
        try (Connection con = db.getConnection())
        {
            int id = 0;

            String sqlSelect = "SELECT id FROM Location WHERE location = ?";
            PreparedStatement preparedStatement = con.prepareStatement(sqlSelect, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, location);
            ResultSet rs = preparedStatement.executeQuery();

            if (rs.next())
            {
                id = rs.getInt("id");

            }

            return id;
        }
    }

    /**
     * Sets the location of the folder and returns the id
     *
     * @param location
     *
     * @return
     *
     * @throws SQLServerException
     * @throws SQLException
     */
    public int setLocation(String location) throws SQLServerException, SQLException
    {
        try (Connection con = db.getConnection())
        {
            int id;

            String sqlInsert = "INSERT INTO Location (location) VALUES (?)";
            PreparedStatement preparedStatementInsert = con.prepareStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS);
            preparedStatementInsert.setString(1, location);
            preparedStatementInsert.executeUpdate();

            ResultSet rsi = preparedStatementInsert.getGeneratedKeys();

            rsi.next();

            id = rsi.getInt(1);

            return id;

        }

    }

    public Music createSongFromDB(ResultSet rs) throws SQLException
    {

        Music song = new Music();

        song.setTitle(rs.getString("title"));
        song.setArtist(rs.getString("artist"));
        song.setAlbum(rs.getString("album"));
        song.setGenre(rs.getString("genre"));
        song.setYear(rs.getInt("releasedate"));
        song.SetLocation(rs.getString("location").trim());
        song.setSongPathName(rs.getString("pathname").trim());
        song.SetDescription(rs.getString("description"));
        song.setDuration(rs.getInt("duration"));

        return song;
    }

    /**
     *
     * @param id
     *
     * @return
     *
     * @throws SQLServerException
     * @throws SQLException
     */
    public Music getSong(int id) throws SQLServerException, SQLException
    {

        try (Connection con = db.getConnection())
        {

            String sql = "SELECT Songs.title, Songs.releasedate, Artist.artist, Albums.album, Genre.genre, Path.pathname "
                         + "FROM Songs "
                         + "INNER JOIN Artist ON Songs.artistid = Artist.id "
                         + "INNER JOIN Albums ON Songs.albumid = Albums.id "
                         + "INNER JOIN Genre ON Songs.genreid = Genre.id "
                         + "INNER JOIN Path ON Songs.pathid = path.id "
                         + "WHERE id = ?";

            PreparedStatement preparedStatement = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setInt(1, id);
            ResultSet rs = preparedStatement.executeQuery();

            if (rs.next())
            {
                Music song = createSongFromDB(rs);

                return song;

            }
            else
            {
                return null;
            }

        }

    }

    /**
     *
     * @param id
     *
     * @throws SQLServerException
     * @throws SQLException
     */
    public void deleteSong(int id) throws SQLServerException, SQLException
    {

        try (Connection con = db.getConnection())
        {

            String sql = "DELETE FROM Songs "
                         + "INNER JOIN Artist ON Songs.artistid = Artist.id "
                         + "INNER JOIN Albums ON Songs.albumid = Albums.id "
                         + "INNER JOIN Genre ON Songs.genreid = Genre.id "
                         + "WHERE Songs.id = ?";

            PreparedStatement preparedStatement = con.prepareStatement(sql);
            preparedStatement.setInt(1, id);
            preparedStatement.executeQuery();

        }
    }

    /**
     *
     * @param length
     * @param sqlSearch
     * @param searchText
     *
     * @return list of songs
     *
     * @throws SQLServerException
     * @throws SQLException
     */
    public List<Music> getSongsFromSearch(int length, String sqlSearch, String searchText) throws SQLServerException, SQLException
    {

        try (Connection con = db.getConnection())
        {

            String sql = "SELECT Songs.title, Songs.releasedate, Songs.description, Songs.duration, Artist.artist, Albums.album, Genre.genre, Path.pathname "
                         + "FROM Songs "
                         + "INNER JOIN Artist ON Songs.artistid = Artist.id "
                         + "INNER JOIN Albums ON Songs.albumid = Albums.id "
                         + "INNER JOIN Genre ON Songs.genreid = Genre.id "
                         + "INNER JOIN Path ON Songs.pathid = path.id "
                         + "WHERE " + sqlSearch;

            PreparedStatement preparedStatement = con.prepareStatement(sql);
            for (int index = 1; index <= length; index++)
            {
                preparedStatement.setString(index, "%" + searchText + "%");
            }
            ResultSet rs = preparedStatement.executeQuery();

            List<Music> songs = new ArrayList();

            while (rs.next())
            {

                Music song = createSongFromDB(rs);
                songs.add(song);

            }

            return songs;
        }
    }

    public void editSong(String oldTitle, String newTitle, String oldArtist, String newArtist, int songId,
                         String oldFile, String newFile, String oldGenre, String newGenre) throws SQLServerException, SQLException
    {
        try (Connection con = db.getConnection())
        {
            // Title
            editTitle(newTitle, oldTitle, con);
            // Artist
            editArtist(oldArtist, newArtist, con, songId);

            // Path
            editPath(newFile, oldFile, con);

            // Genre
            editGenre(oldGenre, newGenre, con, songId);
        }
    }

    public void editTitle(String newTitle, String oldTitle, Connection con) throws SQLException
    {
        // Title
        String sqlTitle = "UPDATE Songs SET title = ? WHERE title like ?";

        PreparedStatement preparedStatementTitle = con.prepareStatement(sqlTitle);
        preparedStatementTitle.setString(1, newTitle);
        preparedStatementTitle.setString(2, oldTitle);

        preparedStatementTitle.execute();
    }

    public void editArtist(String oldArtist, String newArtist, Connection con, int songId) throws SQLException
    {

        // Artist
        int artistId = getExistingArtist(oldArtist);
        String sqlArtist = "UPDATE Songs set artistid = ? WHERE songs.id = ?";

        if (artistId == 0)
        {
            artistId = setArtist(newArtist);
        }

        PreparedStatement preparedStatementArtist = con.prepareStatement(sqlArtist);

        preparedStatementArtist.setInt(1, artistId);
        preparedStatementArtist.setInt(2, songId);
        preparedStatementArtist.execute();
    }

    public void editPath(String newFile, String oldFile, Connection con) throws SQLException
    {
        // Path

        String sqlFile = "UPDATE Path SET pathname = ? WHERE pathname like ?";
        
        PreparedStatement preparedStatementFile = con.prepareStatement(sqlFile);
        preparedStatementFile.setString(1, newFile);
        preparedStatementFile.setString(2, oldFile);
        preparedStatementFile.execute();
    }

    public void editGenre(String oldGenre, String newGenre, Connection con, int songId) throws SQLException
    {
        int genreId = getExistingGenre(oldGenre);
        String sqlGenre = "UPDATE Songs set genreid = ? WHERE Songs.id = ?";
        if (genreId == 0)
        {
            genreId = setGenre(newGenre);
        }
        
        PreparedStatement preparedStatementGenre = con.prepareStatement(sqlGenre);
        preparedStatementGenre.setInt(1, genreId);
        preparedStatementGenre.setInt(2, songId);
        preparedStatementGenre.execute();
    }
}
