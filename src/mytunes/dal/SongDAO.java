package mytunes.dal;

import com.microsoft.sqlserver.jdbc.SQLServerException;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import mytunes.be.Music;
import mytunes.be.Path;


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

    public List<Music> getAllSongs()
    {
        List<Music> songs = new ArrayList<>();

        return songs;
    }
    
    public Music setMusicAlbum(Music music) throws SQLServerException, SQLException
    {
        try (Connection con = db.getConnection())
        {
            String sql = "INSERT INTO Album VALUES (?, ?);";
            PreparedStatement statement = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, music.getAlbum());
            statement.setInt(2, music.getYear());
            statement.executeUpdate();
        }
        throw new RuntimeException("Can't get album");
    }
    
    public Music setMusicArtist(Music music) throws SQLServerException, SQLException
    {
        try (Connection con = db.getConnection())
        {
            String sql = "INSERT INTO Artist VALUES (?);";
            PreparedStatement statement = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, music.getArtist());
            statement.executeUpdate();
        }
        throw new RuntimeException("Can't get album");
    }
    
    public Music setMusicGenre(Music music) throws SQLServerException, SQLException
    {
        try (Connection con = db.getConnection())
        {
            String sql = "INSERT INTO Artist VALUES (?);";
            PreparedStatement statement = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, music.getGenre());
            statement.executeUpdate();
        }
        throw new RuntimeException("Can't get music genre");
    }
    


    public Path createSongPath(String pathname) throws SQLServerException, SQLException
    {
        try (Connection con = db.getConnection())

        {
            String sql = "INSERT INTO Path VALUES (?);";

            PreparedStatement statement = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, pathname);
            statement.executeUpdate();
            
            throw new RuntimeException("Song path was not created");
        }
    }
    
    public void getIds()
    {
        
    }
    
    
    
    public List<String> checkIfIsInDatabase() throws SQLServerException, SQLException
    {
        dataBaseSongNames.clear();
        try (Connection con = db.getConnection()) 
        {
            Statement st = con.createStatement();
            String sqlGet = "SELECT * FROM Path;";

            ResultSet rs = st.executeQuery(sqlGet);

            while (rs.next()) 
            {
                rs.getInt("id");
                dataBaseSongNames.add(rs.getString("pathname"));
            }
            return dataBaseSongNames;
        }
    }
}
