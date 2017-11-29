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
    
    
    /**
     * Not finished yet, ...but if the artist already exsists in the artist table get the id, else insert the artist and get the artistId
     * @param artist
     * @return id
     * @throws SQLServerException
     * @throws SQLException 
     */
    public int setArtist(String artist) throws SQLServerException, SQLException
    {
        
        
        try (Connection con = db.getConnection())
        {
            int id = 0;

            String sqlSelect = "SELECT id FROM Artist WHERE artist = ?";
            PreparedStatement preparedStatement = con.prepareStatement(sqlSelect);
            preparedStatement.setString(1, artist);
            ResultSet rs = preparedStatement.executeQuery();

            if(rs.next()) 
            {
                id = rs.getInt("id");
            }
            else
            {
                String sqlInsert = "INSERT INTO Artist (artist) VALUES (?)";
                PreparedStatement preparedStatementInsert = con.prepareStatement(sqlInsert);
                preparedStatementInsert.setString(1, artist);
                preparedStatementInsert.executeUpdate();

                ResultSet rsi = preparedStatementInsert.getGeneratedKeys();

                while(rsi.next())
                {
                    id = rsi.getInt("id");

                }
            } 

            return id;
        }
        
        
    }


    /**
     * 
     * @param album
     * @return
     * @throws SQLServerException
     * @throws SQLException 
     */
    public int setAlbum(String album) throws SQLServerException, SQLException
    {
        
        
        try (Connection con = db.getConnection())
        {
            int id = 0;

            String sqlSelect = "SELECT id FROM Album WHERE album = ?";
            PreparedStatement preparedStatement = con.prepareStatement(sqlSelect);
            preparedStatement.setString(1, album);
            ResultSet rs = preparedStatement.executeQuery();

            if(rs.next()) 
            {
                id = rs.getInt("id");
            }
            else
            {
                String sqlInsert = "INSERT INTO Album (album) VALUES (?)";
                PreparedStatement preparedStatementInsert = con.prepareStatement(sqlInsert);
                preparedStatementInsert.setString(1, album);
                preparedStatementInsert.executeUpdate();

                ResultSet rsi = preparedStatementInsert.getGeneratedKeys();

                while(rsi.next())
                {
                    id = rsi.getInt("id");

                }
            } 

            return id;
        }
        
        
    }
    
    
}
