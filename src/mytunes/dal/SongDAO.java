package mytunes.dal;

import java.util.ArrayList;
import java.util.List;
import mytunes.be.Music;

/**
 *
 * @author Axl
 */
public class SongDAO
{

    public SongDAO()
    {
    }

    public List<Music> getAllSongs()
    {
        List<Music> songs = new ArrayList<>();

        Music song = new Music();
        song.setID(1212);
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
}
