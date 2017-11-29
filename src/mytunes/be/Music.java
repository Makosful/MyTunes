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
    private int duration;
    private String description;
    private String location;

    public Music(int id, String title, String album, String artist, int year, String location)
    {
        this.id = id;
        this.title = title;
        this.album = album;
        this.artist = artist;
        this.year = year;
        this.location = location;
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

    public int getDuration()
    {
        return duration;
    }

    public void setDuration(int duration)
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
        return "Metadata " + "id: " + id + ", Title: " + title + ", artist: " + artist + ", album: " + album + ", year: " + year
               + ", duration:" + duration + ", description: " + description + ", location: " + location;
    }
}
