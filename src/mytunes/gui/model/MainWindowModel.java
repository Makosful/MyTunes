package mytunes.gui.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import mytunes.be.Song;

/**
 *
 * @author Storm
 */
public class MainWindowModel
{

    public ObservableList<Song> getSongList()
    {
        ObservableList<Song> list = FXCollections.observableArrayList();

        list.get(0);

        return list;
    }

}
