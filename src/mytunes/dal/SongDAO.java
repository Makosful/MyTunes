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
        
        String sql = "SELECT Songs.title, Artist.artist, Albums.album, Albums.releasedate, Genre.genre "
                   + "FROM Songs "
                   + "INNER JOIN Artist ON Songs.artistid = Artist.id "
                   + "INNER JOIN Albums ON Songs.albumid = Albums.id "
                   + "INNER JOIN Genre ON Songs.genreid = Genre.id ";
        
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
       
            String sql = "SELECT Songs.title, Artist.artist, Albums.album, Albums.releasedate, Genre.genre "
                       + "FROM Songs "
                       + "INNER JOIN Artist ON Songs.artistid = Artist.id "
                       + "INNER JOIN Albums ON Songs.albumid = Albums.id "
                       + "INNER JOIN Genre ON Songs.genreid = Genre.id "
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
     * @param searchText
     * @return list of songs
     * @throws SQLServerException
     * @throws SQLException 
     */
    public List<Music> getSongsFromSearch(String searchText) throws SQLServerException, SQLException
    {
        
        List<String> searchTables = new ArrayList();
        
        searchTables.add("Songs.title");
        searchTables.add("Artist.artist");
        
        String sqlSearch = "";
        boolean firstQm = true;
        for( int i = 0 ; i < searchTables.size(); i++ ) {
        
            if(firstQm == false)
            {
                sqlSearch += " OR ";
            }
            
            sqlSearch += searchTables.get(i)+" = ?";

            firstQm = false;
            
        }

        try (Connection con = db.getConnection())
        {
            List<Music> songs = new ArrayList();
       
            String sql = "SELECT Songs.title, Artist.artist, Albums.album, Albums.releasedate, Genre.genre "
                       + "FROM Songs "
                       + "INNER JOIN Artist ON Songs.artistid = Artist.id "
                       + "INNER JOIN Albums ON Songs.albumid = Albums.id "
                       + "INNER JOIN Genre ON Songs.genreid = Genre.id "
                       + "WHERE "+sqlSearch;

            PreparedStatement preparedStatement = con.prepareStatement(sql);
            for(int index = 1; index <= searchTables.size(); index++) 
            {
               preparedStatement.setString(index, searchText);
            }
            ResultSet rs = preparedStatement.executeQuery();
        
            while(rs.next())
            {
                
                Music song = createSongFromDB(rs);
                songs.add(song);
                System.out.println(song.getArtist());
            }
            
            return songs;
        }
    }
    
    public void editSong(String oldTitle, String newTitle, String oldArtist, String newArtist, int songId,
    String oldFile, String newFile, String oldGenre, String newGenre) throws SQLServerException, SQLException
    {
        try(Connection con = db.getConnection())
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
            
            
            PreparedStatement preparedStatementTitle =  con.prepareStatement(sqlTitle);
            preparedStatementTitle.setString(1, newTitle);
            preparedStatementTitle.setString(2, oldTitle);
            
            preparedStatementTitle.execute();
    }
    
    public void editArtist(String oldArtist, String newArtist, Connection con, int songId) throws SQLException
    {
                    
            // Artist
            int artistId = getExistingArtist(oldArtist);
            String sqlArtist = "";
            
            if(artistId == 0)
            {
                artistId = setArtist(newArtist);
                sqlArtist = "UPDATE Songs set artistid = ? WHERE songs.id = ?";
            }
            else
            {
                 sqlArtist = "UPDATE Songs set artistid = ? WHERE songs.id = ?";
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
            String sqlGenre = "";
            if(genreId == 0)
            {
                genreId = setGenre(newGenre);
                sqlGenre = "UPDATE Songs set genreid = ? WHERE Songs.id = ?";
            }
            else
            {
                
                sqlGenre = "UPDATE Songs SET genreid = ? WHERE Songs.id = ?";
            }
            PreparedStatement preparedStatementGenre = con.prepareStatement(sqlGenre);
            preparedStatementGenre.setInt(1, genreId);
            preparedStatementGenre.setInt(2,songId);
            preparedStatementGenre.execute();
    }
}
