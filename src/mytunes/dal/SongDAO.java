package mytunes.dal;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import mytunes.be.Music;
import mytunes.dal.exception.DALException;

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

    /**
     * Gets all the songs from the db then calls a method that creates song
     * objects
     * then it returns these songs
     *
     * @return list of songs
     *
     * @throws mytunes.dal.exception.DALException
     */
    public List<Music> getAllSongs() throws DALException
    {
        try (Connection con = db.getConnection())
        {

            List<Music> allSongs = new ArrayList<>();

            String sql = "SELECT "
                         + "Songs.id, "
                         + "Songs.title, "
                         + "Songs.releasedate, "
                         + "Songs.description, "
                         + "Songs.duration, "
                         + "Artist.artist, "
                         + "Albums.album, "
                         + "Genres_test.genre, "
                         + "Path.pathname, "
                         + "Location.location "
                         + "FROM Songs "
                         + "INNER JOIN Artist ON Songs.artistid = Artist.id "
                         + "INNER JOIN Albums ON Songs.albumid = Albums.id "
                         + "INNER JOIN Path ON Songs.pathid = Path.id "
                         + "INNER JOIN Location ON Path.locationid = Location.id "
                         + "INNER JOIN Genre_test ON Songs.id = Genre_test.songid "
                         + "INNER JOIN Genres_test ON Genre_test.genreid = Genres_test.id ";

            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(sql);

            Music song = new Music();
            while (rs.next())
            {

                song = createSongFromDB(rs, song);
                if (!allSongs.contains(song))
                {
                    allSongs.add(song);
                }
            }

            return allSongs;

        }
        catch (SQLException ex)
        {
            throw new DALException();
        }
    }

    /**
     * Gets the ids and inserts the song parameters/ creates the song in the
     * songs table
     *
     * @param song
     * @param relationIds
     *
     * @return
     *
     * @throws mytunes.dal.exception.DALException
     */
    public int setSong(Music song, List<Integer> relationIds) throws DALException
    {

        int artistId = relationIds.get(0);
        int albumId = relationIds.get(1);
        int pathId = relationIds.get(2);

        try (Connection con = db.getConnection())
        {

            String sqlInsert = "INSERT INTO Songs VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement preparedStatementInsert = con.prepareStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS);
            preparedStatementInsert.setString(1, song.getTitle());
            preparedStatementInsert.setInt(2, artistId);
            preparedStatementInsert.setInt(3, albumId);
            preparedStatementInsert.setInt(4, pathId);
            preparedStatementInsert.setString(5, song.getDescription());
            preparedStatementInsert.setInt(6, song.getDuration());
            preparedStatementInsert.setInt(7, song.getYear());

            preparedStatementInsert.executeUpdate();

            ResultSet rs = preparedStatementInsert.getGeneratedKeys();

            int id;
            rs.next();
            id = rs.getInt(1);
            return id;
        }
        catch (SQLException ex)
        {
            throw new DALException();
        }
    }

    /**
     * If the artist already exists in the artist table get the id
     *
     * @param artist
     *
     * @return
     *
     * @throws mytunes.dal.exception.DALException
     */
    public int getExistingArtist(String artist) throws DALException
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
        catch (SQLException ex)
        {
            throw new DALException();
        }
    }

    /**
     * Insert the artist and get the artistId
     *
     * @param artist
     *
     * @return id
     *
     * @throws mytunes.dal.exception.DALException
     */
    public int setArtist(String artist) throws DALException
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
        catch (SQLException ex)
        {
            throw new DALException();
        }

    }

    /**
     * If the album already exists in the album table get the id
     *
     * @param album
     *
     * @return
     *
     * @throws mytunes.dal.exception.DALException
     */
    public int getExistingAlbum(String album) throws DALException
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
        catch (SQLException ex)
        {
            throw new DALException();
        }
    }

    /**
     * Insert the album and get the artistId
     *
     * @param album
     *
     * @return
     *
     * @throws mytunes.dal.exception.DALException
     */
    public int setAlbum(String album) throws DALException
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
        catch (SQLException ex)
        {
            throw new DALException();
        }
    }

    /**
     * If the path already exists in the path table get the id
     *
     * @param path
     * @param locationid
     *
     * @return
     *
     * @throws mytunes.dal.exception.DALException
     */
    public int getExistingPath(String path, int locationid) throws DALException
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
        catch (SQLException ex)
        {
            throw new DALException();
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
     * @throws mytunes.dal.exception.DALException
     */
    public int setPath(String songPathName, int locationId) throws DALException
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
        catch (SQLException ex)
        {
            throw new DALException();
        }

    }

    /**
     * If the loaction already exists in the loaction table get the id
     *
     * @param location
     *
     * @return
     *
     * @throws mytunes.dal.exception.DALException
     */
    public int getExistingLocation(String location) throws DALException
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
        catch (SQLException ex)
        {
            throw new DALException();
        }
    }

    /**
     * Sets the location of the folder and returns the id
     *
     * @param location
     *
     * @return
     *
     * @throws mytunes.dal.exception.DALException
     */
    public int setLocation(String location) throws DALException
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
        catch (SQLException ex)
        {
            throw new DALException();
        }

    }

    /**
     * Creates a new song based on data from the database
     *
     * @param rs
     * @param previousSong
     *
     * @return
     *
     * @throws mytunes.dal.exception.DALException
     */
    public Music createSongFromDB(ResultSet rs, Music previousSong) throws DALException
    {

        try
        {
            if (previousSong.getId() == rs.getInt("id"))
            {
                previousSong.setGenre(previousSong.getGenre() + " " + rs.getString("genre"));

                return previousSong;
            }
            else
            {
                Music song = new Music();

                song.setId(rs.getInt("id"));
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
        }
        catch (SQLException sQLException)
        {
            throw new DALException();
        }

    }

    /**
     *
     * @param id
     *
     * @return
     *
     * @throws mytunes.dal.exception.DALException
     */
    public Music getSong(int id) throws DALException
    {

        try (Connection con = db.getConnection())
        {

            String sql = "SELECT Songs.title, Songs.releasedate, Artist.artist, Albums.album, Genres_test.genre, Path.pathname "
                         + "FROM Songs "
                         + "INNER JOIN Artist ON Songs.artistid = Artist.id "
                         + "INNER JOIN Albums ON Songs.albumid = Albums.id "
                         + "INNER JOIN Path ON Songs.pathid = Path.id "
                         + "INNER JOIN Location ON Path.locationid = Location.id "
                         + "INNER JOIN Genre_test ON Songs.id = Genre_test.songid "
                         + "INNER JOIN Genres_test ON Genre_test.genreid = Genres_test.id "
                         + "WHERE id = ?";

            PreparedStatement preparedStatement = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setInt(1, id);
            ResultSet rs = preparedStatement.executeQuery();

            Music song = new Music();
            while (rs.next())
            {
                song = createSongFromDB(rs, song);

            }
            return song;

        }
        catch (SQLException ex)
        {
            throw new DALException();
        }

    }

    /**
     *
     * @param id
     *
     * @throws mytunes.dal.exception.DALException
     */
    public void deleteSong(int id) throws DALException
    {

        try (Connection con = db.getConnection())
        {

            String sql = "DELETE Path FROM Path "
                         + "INNER JOIN Songs ON Path.id = Songs.pathid "
                         + "WHERE Songs.id = ?";

            PreparedStatement preparedStatement = con.prepareStatement(sql);
            preparedStatement.setInt(1, id);
            preparedStatement.execute();

        }
        catch (SQLException ex)
        {
            throw new DALException();
        }
    }

    /**
     * Gets the parameter length, which defines how many tables to search in
     * and the parameter which specifies which tables to search in with the sql
     * string
     * then calls the method that creates the song objects and then returns all
     * the
     * songs corresponds with the searchtext
     *
     * @param length
     * @param sqlSearch
     * @param searchText
     *
     * @return list of songs
     *
     * @throws mytunes.dal.exception.DALException
     */
    public List<Music> getSongsFromSearch(int length,
                                          String sqlSearch,
                                          String searchText)
            throws DALException
    {

        try (Connection con = db.getConnection())
        {

            String sql = "SELECT "
                         + "Songs.id, "
                         + "Songs.title, "
                         + "Songs.releasedate, "
                         + "Songs.description, "
                         + "Songs.duration, "
                         + "Artist.artist, "
                         + "Albums.album, "
                         + "Genres_test.genre, "
                         + "Location.location, "
                         + "Path.pathname "
                         + "FROM Songs "
                         + "INNER JOIN Artist ON Songs.artistid = Artist.id "
                         + "INNER JOIN Albums ON Songs.albumid = Albums.id "
                         + "INNER JOIN Path ON Songs.pathid = Path.id "
                         + "INNER JOIN Location ON Path.locationid = Location.id "
                         + "INNER JOIN Genre_test ON Songs.id = Genre_test.songid "
                         + "INNER JOIN Genres_test ON Genre_test.genreid = Genres_test.id "
                         + "WHERE " + sqlSearch;

            PreparedStatement preparedStatement = con.prepareStatement(sql);
            for (int index = 1; index <= length; index++)
            {
                preparedStatement.setString(index, "%" + searchText + "%");
            }
            ResultSet rs = preparedStatement.executeQuery();

            List<Music> allSongs = new ArrayList();

            Music song = new Music();
            while (rs.next())
            {

                song = createSongFromDB(rs, song);
                if (!allSongs.contains(song))
                {
                    allSongs.add(song);
                }
            }

            return allSongs;
        }
        catch (SQLException ex)
        {
            throw new DALException();
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
     *
     * @throws DALException
     */
    public void editSong(String newTitle, String newArtist, int songId,
                         String oldFile, String newFile) throws DALException
    {
        try (Connection con = db.getConnection())
        {
            // Title
            editTitle(songId, newTitle, con);
            // Artist
            editArtist(songId, newArtist, con);

            // Path
            editPath(newFile, oldFile, con);

        }
        catch (SQLException ex)
        {
            throw new DALException();
        }
    }

    // Methods to change title etc.
    /**
     *
     * @param songId
     * @param newTitle
     * @param con
     *
     * @throws DALException
     */
    public void editTitle(int songId, String newTitle, Connection con) throws DALException
    {
        try
        {
            // Title
            String sqlTitle = "UPDATE Songs SET title = ? WHERE Songs.id = ?";

            PreparedStatement preparedStatementTitle = con.prepareStatement(sqlTitle);
            preparedStatementTitle.setString(1, newTitle);
            preparedStatementTitle.setInt(2, songId);

            preparedStatementTitle.execute();
        }
        catch (SQLException sQLException)
        {
            throw new DALException();
        }
    }

    /**
     *
     * @param songId
     * @param newArtist
     * @param con
     *
     * @throws DALException
     */
    public void editArtist(int songId, String newArtist, Connection con) throws DALException
    {

        try
        {
            // Artist
            int artistId = getExistingArtist(newArtist);
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
        catch (DALException | SQLException dALException)
        {
            throw new DALException();
        }
    }

    /**
     *
     * @param newFile
     * @param oldFile
     * @param con
     *
     * @throws DALException
     */
    public void editPath(String newFile, String oldFile, Connection con) throws DALException
    {
        try
        {
            // Path
            String sqlFile = "UPDATE Path SET pathname = ? WHERE pathname like ?";

            PreparedStatement preparedStatementFile = con.prepareStatement(sqlFile);
            preparedStatementFile.setString(1, newFile);
            preparedStatementFile.setString(2, oldFile);
            preparedStatementFile.execute();
        }
        catch (SQLException sQLException)
        {
            throw new DALException();
        }
    }

    /**
     *
     * @param songid
     * @param genreid
     *
     * @throws DALException
     */
    public void setTestGenres(int songid, int genreid) throws DALException
    {
        try (Connection con = db.getConnection())
        {
            String sql = "INSERT INTO Genre_test VALUES (?, ?)";
            PreparedStatement preparedStatementInsert = con.prepareStatement(sql);
            preparedStatementInsert.setInt(1, songid);
            preparedStatementInsert.setInt(2, genreid);
            preparedStatementInsert.executeUpdate();
        }
        catch (SQLException ex)
        {
            throw new DALException();
        }
    }

    /**
     *
     * @param genre
     *
     * @return
     *
     * @throws DALException
     */
    public int getExistingTestGenre(String genre) throws DALException
    {
        try (Connection con = db.getConnection())
        {
            int id = 0;

            String sqlSelect = "SELECT id FROM Genres_test WHERE genre = ?";
            PreparedStatement preparedStatement = con.prepareStatement(sqlSelect, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, genre);
            ResultSet rs = preparedStatement.executeQuery();

            if (rs.next())
            {
                id = rs.getInt("id");

            }

            return id;
        }
        catch (SQLException ex)
        {
            throw new DALException();
        }
    }

    /**
     *
     * @param genre
     *
     * @return
     *
     * @throws DALException
     */
    public int setTestGenre(String genre) throws DALException
    {

        try (Connection con = db.getConnection())
        {
            int id;

            String sqlInsert = "INSERT INTO Genres_test (genre) VALUES (?)";
            PreparedStatement preparedStatementInsert = con.prepareStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS);
            preparedStatementInsert.setString(1, genre);
            preparedStatementInsert.executeUpdate();

            ResultSet rsi = preparedStatementInsert.getGeneratedKeys();

            rsi.next();

            id = rsi.getInt(1);

            return id;
        }
        catch (SQLException ex)
        {
            throw new DALException();
        }

    }

    /**
     *
     * @param songId
     * @param genreId
     *
     * @throws DALException
     */
    public void removeSongsGenre(int songId, int genreId) throws DALException
    {
        try (Connection con = db.getConnection())
        {
            String sql = "DELETE Genre_test FROM Genre_test WHERE Genre_test.songid = ? AND Genre_test.genreid = ?";

            PreparedStatement preparedStatement = con.prepareStatement(sql);
            preparedStatement.setInt(1, songId);
            preparedStatement.setInt(2, genreId);
            preparedStatement.execute();
        }
        catch (SQLException ex)
        {
            throw new DALException();
        }
    }

    /**
     *
     * @return
     * @throws DALException
     */
    public List<String> getAllGenres() throws DALException
    {
        try (Connection con = db.getConnection())
        {
            String sqlSelect = "SELECT genre FROM Genres_test";
            PreparedStatement preparedStatement = con.prepareStatement(sqlSelect, Statement.RETURN_GENERATED_KEYS);
            ResultSet rs = preparedStatement.executeQuery();

            List<String> allGenres = new ArrayList();
            while (rs.next())
            {
                allGenres.add(rs.getString("genre"));

            }

            return allGenres;
        }
        catch (SQLException ex)
        {
            throw new DALException();
        }
    }
}
