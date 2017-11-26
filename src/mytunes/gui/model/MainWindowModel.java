package mytunes.gui.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import mytunes.be.Music;

/**
 *
 * @author Storm
 */
public class MainWindowModel
{

    private ObservableList<Music> list;

    public ObservableList<Music> getSongList()
    {
        list = FXCollections.observableArrayList();

        return list;
    }

}
