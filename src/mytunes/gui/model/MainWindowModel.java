package mytunes.gui.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import mytunes.be.Music;
import mytunes.bll.BLLManager;

/**
 *
 * @author Storm
 */
public class MainWindowModel
{

    private ObservableList<Music> list;

    // Objects
    private final BLLManager bllManager;

    public MainWindowModel()
    {
        this.bllManager = new BLLManager();
        this.list = FXCollections.observableArrayList();
    }

    public ObservableList<Music> getSongList()
    {
        return list;
    }

    public void loadSongList()
    {
        list.clear();
        list.addAll(bllManager.getSongList());
    }
}
