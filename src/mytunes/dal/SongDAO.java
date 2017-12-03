package mytunes.dal;

import com.microsoft.sqlserver.jdbc.SQLServerException;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
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

            String sql = "SELECT Songs.title, Artist.artist, Albums.album, Albums.releasedate, Genre.genre, Path.pathname"
                       + "FROM Songs "
                       + "INNER JOIN Artist ON Songs.artistid = Artist.id "
                       + "INNER JOIN Albums ON Songs.albumid = Albums.id "
                       + "INNER JOIN Genre ON Songs.genreid = Genre.id "
                       + "INNER JOIN Path ON Songs.pathid = path.id ";

            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(sql); 


            while (rs.next())
            {
                Music song = createSongFromDB(rs);

                System.out.println(song.getAlbum());
                allSongs.add(song);
            }


            return allSongs;

        }
    }

    /**
     * Gets the ids and inserts the song parameters/ creates the song in the songs table
     * @param song
     * @param relationIds
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

            String sqlInsert = "INSERT INTO Songs VALUES (?, ?, ?, ?, ?)";
            PreparedStatement preparedStatementInsert = con.prepareStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS);
            preparedStatementInsert.setString(1, song.getTitle());
            preparedStatementInsert.setInt(2, artistId);
            preparedStatementInsert.setInt(3, albumId);
            preparedStatementInsert.setInt(4, genreId);
            preparedStatementInsert.setInt(5, pathId);
            preparedStatementInsert.executeUpdate();

        }
    }
    
    

    
    /**
     * If the artist already exsists in the artist table get the id
     * @param artist
     * @return
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
     * @param artist
     * @return id
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
     * @param album
     * @return
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
     * @param album
     * @param releasedate
     * @return
     * @throws SQLServerException
     * @throws SQLException
     */
    public int setAlbum(String album, int releasedate) throws SQLServerException, SQLException
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
     * @param genre
     * @return
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
     * Sets the path/name of the mp3 file and returns the id  
     * @param songPathName
     * @return
     * @throws SQLServerException
     * @throws SQLException
     */
    public int setPath(String songPathName) throws SQLServerException, SQLException
    {
        try (Connection con = db.getConnection())
        {
            int id = 0;

            String sqlInsert = "INSERT INTO Path (pathname) VALUES (?)";
            PreparedStatement preparedStatementInsert = con.prepareStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS);
            preparedStatementInsert.setString(1, songPathName);
            preparedStatementInsert.executeUpdate();

            ResultSet rsi = preparedStatementInsert.getGeneratedKeys();

            rsi.next();
            
            id = rsi.getInt(1);


            return id;

        }

    }

    private Music createSongFromDB(ResultSet rs) throws SQLException
    {
        
        Music song = new Music();
        
        song.setTitle(rs.getString("title"));
        song.setArtist(rs.getString("artist"));
        song.setAlbum(rs.getString("album"));
        song.setGenre(rs.getString("genre"));
        song.setYear(rs.getInt("releasedate"));
        //song.setSongPathName(rs.getString("pathname"));
        
        return song;
    }
    
    
    
    /**
     * 
     * @param id
     * @return
     * @throws SQLServerException
     * @throws SQLException
     */
    public Music getSong(int id) throws SQLServerException, SQLException
    {
     
        
        try (Connection con = db.getConnection())
        {
       
        String sql = "SELECT Songs.title, Artist.artist, Albums.album, Albums.releasedate, Genre.genre, Path.pathname"
                   + "FROM Songs "
                   + "INNER JOIN Artist ON Songs.artistid = Artist.id "
                   + "INNER JOIN Albums ON Songs.albumid = Albums.id "
                   + "INNER JOIN Genre ON Songs.genreid = Genre.id "
                   + "INNER JOIN Path ON Songs.pathid = path.id "
                   + "WHERE id = ?";

            PreparedStatement preparedStatement = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setInt(1, id);
            ResultSet rs = preparedStatement.executeQuery();


            
            if(rs.next()){
                Music song = createSongFromDB(rs);

                System.out.println(song.getArtist());
                
                return song;
                
            }else
            {
                return null;
            }
            
            
        }
        
    }  
 
    
    /**
     * 
     * @param id
     * @throws SQLServerException
     * @throws SQLException 
     */
    public void deleteSong(int id) throws SQLServerException, SQLException
    {
     
        
        try (Connection con = db.getConnection())
        {
       
        String sql = "SELECT Songs.title, Artist.artist, Albums.album, Albums.releasedate, Genre.genre, Path.pathname"
                   + "FROM Songs "
                   + "INNER JOIN Artist ON Songs.artistid = Artist.id "
                   + "INNER JOIN Albums ON Songs.albumid = Albums.id "
                   + "INNER JOIN Genre ON Songs.genreid = Genre.id "
                   + "INNER JOIN Path ON Songs.pathid = path.id "
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
     * @return list of songs
     * @throws SQLServerException
     * @throws SQLException 
     */
    public List<Music> getSongsFromSearch(int length, String sqlSearch, String searchText) throws SQLServerException, SQLException
    {
        
        try (Connection con = db.getConnection())
        {
            
       
        String sql = "SELECT Songs.title, Artist.artist, Albums.album, Albums.releasedate, Genre.genre, Path.pathname"
                   + "FROM Songs "
                   + "INNER JOIN Artist ON Songs.artistid = Artist.id "
                   + "INNER JOIN Albums ON Songs.albumid = Albums.id "
                   + "INNER JOIN Genre ON Songs.genreid = Genre.id "
                   + "INNER JOIN Path ON Songs.pathid = path.id "
                   + "WHERE "+sqlSearch;

            PreparedStatement preparedStatement = con.prepareStatement(sql);
            for(int index = 1; index <= length; index++) 
            {
               preparedStatement.setString(index, searchText);
            }
            ResultSet rs = preparedStatement.executeQuery();
            
            
            List<Music> songs = new ArrayList();
            
            while(rs.next())
            {
                
                Music song = createSongFromDB(rs);
                songs.add(song);
                System.out.println(song.getArtist());
            }
            
            return songs;
        }
    }
    

    
}
