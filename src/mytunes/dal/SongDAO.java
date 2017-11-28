package mytunes.dal;

import com.microsoft.sqlserver.jdbc.SQLServerException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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

        Music song = new Music();
        song.setId(1212);
        song.setTitle("Hulla balloo");
        song.setArtist("Mikal Jaeson");
        song.setAlbum("Hits for Kids");
        song.setYear(6969);
        song.setDuration(9001);
        song.SetDescription("Look at ma horse. Ma horse is amazing");
        song.SetLocation("Ze Cloud");

        songs.add(song);

        return songs;
    }
    
    public Path createSongPath(String pathname) throws SQLServerException, SQLException 
    {    
        try (Connection con = db.getConnection()) 
        {
            String sql = "INSERT INTO Path VALUES (?);";

            PreparedStatement statement = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, pathname);

            if (statement.executeUpdate() == 1) 
            {
                ResultSet rs = statement.getGeneratedKeys();
                rs.next();
                int id = rs.getInt(1);
                Path path = new Path(pathname);
                return path;
            }
            throw new RuntimeException("Song path was not created");

        }
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
                dataBaseSongNames.add(rs.getString("pathname"));
            }
            return dataBaseSongNames;
        }
    }
}
