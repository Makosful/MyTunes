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
    List<String> dataBaseSongNames;

    public SongDAO() throws IOException
    {
        db = new DataBaseConnector();
        dataBaseSongNames = new ArrayList();
    }

    public List<Music> getAllSongs() throws SQLServerException, SQLException
    {
        try (Connection con = db.getConnection())
        {
        List<Music> allSongs = new ArrayList<>();
        
        String sql = "SELECT Songs.title, Artist.artist, Album.album, Album.releasedate, Genre.genre"
                   + "FROM Songs"
                   + "INNER JOIN Artist ON Songs.artistid = Artist.id "
                   + "INNER JOIN Album ON Songs.albumid = Album.id "
                   + "INNER JOIN Genre ON Songs.genreid = Genre.id ";
        
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
     * Gets the ids and inserts the song parameters/ creates the song in the songs table
     * @param song
     * @throws SQLServerException
     * @throws SQLException
     */
    public void setSong(Music song) throws SQLServerException, SQLException
    {
        
        List<Integer> relationIds = getRelationIds(song);
        
        int artistId = relationIds.get(0);
        int albumId = relationIds.get(1);
        int genreId = relationIds.get(2);
        int pathId = relationIds.get(3);

        try (Connection con = db.getConnection())
        {

            String sqlInsert = "INSERT INTO Songs VALUES (?, ?, ?, ?, ?)";
            PreparedStatement preparedStatementInsert = con.prepareStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS);
            preparedStatementInsert.setString(1, song.getTitle());
            preparedStatementInsert.setInt(2, artistId);
            preparedStatementInsert.setInt(3, albumId);
            preparedStatementInsert.setInt(4, genreId);
            preparedStatementInsert.setInt(5, pathId);
            preparedStatementInsert.executeUpdate();

            // ResultSet rs = preparedStatementInsert.getGeneratedKeys();
        }
    }
    
    
    /**
     * Determines with id should be used in the song table, if the artist/album/genre 
     * already exists get the id from those, else get the newly inserted id's
     * @param song
     * @return list of id's 
     * @throws SQLException 
     */
    private List<Integer> getRelationIds(Music song) throws SQLException
    {
        List<Integer> ids = new ArrayList();
        
        int artistId;
        int albumId;
        int genreId;
        int pathId;
        
        //Determine if the artist already is in the db, and get the resulting id
        int getArtistId = getExistingArtist(song.getArtist());
        if(getArtistId != 0){
            
            artistId = getArtistId;
        }
        else
        {
            artistId = setArtist(song.getArtist());
        }
        
        //Determine if the album already is in the db, and get the resulting id
        int getAlbumId = getExistingAlbum(song.getAlbum());
        if(getAlbumId != 0){ 
            
            albumId = getAlbumId;
        }
        else
        {
            albumId = setAlbum(song.getAlbum(), song.getYear());
        }
        
        //Determine if the genre already is in the db, and get the resulting id
        int getGenreId = getExistingGenre(song.getGenre());
        if(getGenreId != 0){
            
            genreId = getGenreId;
            
        }
        else
        {
            genreId = setGenre(song.getGenre());
        }


        pathId = setPath(song.getSongPathName());
        
        ids.add(artistId);
        ids.add(albumId);
        ids.add(genreId);
        ids.add(pathId);
        
        return ids;
    }

    
    /**
     * If the artist already exsists in the artist table get the id
     * @param artist
     * @return
     * @throws SQLServerException
     * @throws SQLException 
     */
    private int getExistingArtist(String artist) throws SQLServerException, SQLException
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
     * @param artist
     * @return id
     * @throws SQLServerException
     * @throws SQLException
     */
    private int setArtist(String artist) throws SQLServerException, SQLException
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
     * @param album
     * @return
     * @throws SQLServerException
     * @throws SQLException 
     */
    private int getExistingAlbum(String album) throws SQLServerException, SQLException
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
     * @param album
     * @param releasedate
     * @return
     * @throws SQLServerException
     * @throws SQLException
     */
    private int setAlbum(String album, int releasedate) throws SQLServerException, SQLException
    {

        try (Connection con = db.getConnection())
        {
            int id;

            String sqlInsert = "INSERT INTO Albums (album, releasedate) VALUES (?, ?)";
            PreparedStatement preparedStatementInsert = con.prepareStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS);
            preparedStatementInsert.setString(1, album);
            preparedStatementInsert.setInt(2, releasedate);
            preparedStatementInsert.executeUpdate();

            ResultSet rsi = preparedStatementInsert.getGeneratedKeys();

            rsi.next();
            
            id = rsi.getInt(1);

            return id;
        }

    }

    /**
     * If the genre already exsists in the genre table get the id
     * @param genre
     * @return
     * @throws SQLServerException
     * @throws SQLException 
     */
    private int getExistingGenre(String genre) throws SQLServerException, SQLException
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
     * @param genre
     * @return
     * @throws SQLServerException
     * @throws SQLException
     */
    private int setGenre(String genre) throws SQLServerException, SQLException
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
     * Sets the path/name of the mp3 file and returns the id  
     * @param songPathName
     * @return
     * @throws SQLServerException
     * @throws SQLException
     */
    private int setPath(String songPathName) throws SQLServerException, SQLException
    {
        try (Connection con = db.getConnection())
        {
            int id = 0;

            String sqlInsert = "INSERT INTO Path (pathname) VALUES (?)";
            PreparedStatement preparedStatementInsert = con.prepareStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS);
            preparedStatementInsert.setString(1, songPathName);
            preparedStatementInsert.executeUpdate();

            ResultSet rsi = preparedStatementInsert.getGeneratedKeys();

            while (rsi.next())
            {
                id = rsi.getInt(1);

            }

            return id;

        }

    }

    private Music createSongFromDB(ResultSet rs)
    {
        
        Music song = new Music();
        
    
        //I create the company object and add it to my list of results:
        
        return song;
    }
    
    
    

    
    
    
}
