package mytunes.dal;

import com.microsoft.sqlserver.jdbc.SQLServerException;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
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
    
    public ObservableList<Playlist> getPlaylists()
    {
        ObservableList<Playlist> list = FXCollections.observableArrayList();

        Playlist fav = new Playlist("My favorites");
        list.add(fav);

        for (int i = 0; i < 10; i++)
        {
            Playlist pl = new Playlist("test" + i);
            list.add(pl);
        }

        return list;
    }

    /**
     * Adds a new playlist to the database
     * @param playlistTitle
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
    
    
    
}
