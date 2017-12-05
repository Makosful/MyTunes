package mytunes.be;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author Axl
 */
public class Playlist
{

    private String title;
    private int id;


    private ObservableList<Music> playlist;
    private boolean favorite;

    public Playlist(String title)
    {
        this.title = title;
        this.playlist = FXCollections.observableArrayList();
        this.favorite = false;
    }

    public Playlist()
    {
       
    }

    @Override
    public String toString()
    {
        return this.title;
    }

    //<editor-fold defaultstate="collapsed" desc="Getters">
    public String getTitle()
    {
        return title;
    }

    public ObservableList<Music> getPlaylist()
    {
        return playlist;
    }

    public boolean isFavorite()
    {
        return favorite;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Setters">
    public void setTitle(String title)
    {
        this.title = title;
    }

    public void setPlaylist(ObservableList<Music> playlist)
    {
        this.playlist = playlist;
    }

    public void setFavorite(boolean favorite)
    {
        this.favorite = favorite;
    }
    
        public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }
    
    //</editor-fold>

}
