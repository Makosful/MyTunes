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

    /**
     *
     * @param pathname
     *
     * @return
     *
     * @throws SQLServerException
     * @throws SQLException
     */
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
