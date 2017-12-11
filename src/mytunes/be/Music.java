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
    private String genre;
    private String songPathName;

    public Music(int id, String title, String album, String artist, int year, String location, String songPathName)
    {
        this.id = id;
        this.title = title;
        this.album = album;
        this.artist = artist;
        this.year = year;
        this.location = location;
        this.songPathName = songPathName;
    }

    public Music()
    {

    }

    @Override
    public String toString()
    {
        String song = this.artist
                      + " | " + this.title
                      + " | " + this.album
                      + " | " + this.year;
        return song;
    }

    //<editor-fold defaultstate="collapsed" desc="ID">
    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Title">
    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Artist">
    public String getArtist()
    {
        return artist;
    }

    public void setArtist(String artist)
    {
        this.artist = artist;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Album">
    public String getAlbum()
    {
        return album;
    }

    public void setAlbum(String album)
    {
        this.album = album;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Year">
    public int getYear()
    {
        return year;
    }

    public void setYear(int year)
    {
        this.year = year;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Duration">
    public int getDuration()
    {
        return duration;
    }

    public void setDuration(int duration)
    {
        this.duration = duration;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Desc">
    public String getDescription()
    {
        return description;
    }

    public void SetDescription(String description)
    {
        this.description = description;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Location">
    public String getLocation()
    {
        return location;
    }

    public void SetLocation(String location)
    {
        this.location = location;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Genra">
    public String getGenre()
    {
        return genre;
    }

    public void setGenre(String genre)
    {
        this.genre = genre;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Song Path Name">
    public String getSongPathName()
    {
        return songPathName;
    }

    public void setSongPathName(String songPathName)
    {
        this.songPathName = songPathName;
    }
    //</editor-fold>
}
