package mytunes.be;

/**
 *
 * @author Storm
 */
public class Music
{

    private int id;
    private String title;
    private String artist;
    private String album;
    private int year;
    private double duration;
    private String description;
    private String location;
    private String genre;
    private String songPathName;

    public Music(int id, String title, String album, int year, String location)
    {
        this.id = id;
        this.title = title;
        this.album = album;
        this.artist = artist;
        this.year = year;
        this.location = location;
    }
    
    public Music()
    {
    
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getArtist()
    {
        return artist;
    }

    public void setArtist(String artist)
    {
        this.artist = artist;
    }

    public String getAlbum()
    {
        return album;
    }

    public void setAlbum(String album)
    {
        this.album = album;
    }

    public int getYear()
    {
        return year;
    }

    public void setYear(int year)
    {
        this.year = year;
    }

    public double getDuration()
    {
        return duration;
    }

    public void setDuration(double duration)
    {
        this.duration = duration;
    }

    public String getDescription()
    {
        return description;
    }

    public void SetDescription(String description)
    {
        this.description = description;
    }

    public String getLocation()
    {
        return location;
    }

    public void SetLocation(String location)
    {
        this.location = location;
    }

    @Override
    public String toString()
    {
        return this.title;
    }

    public String getGenre()
    {
        return genre;
    }

    public void setGenre(String genre)
    {
        this.genre = genre;
    }

    public String getSongPathName()
    {
        return songPathName;
    }

    public void setSongPathName(String songPathName)
    {
        this.songPathName = songPathName;
    }
}
