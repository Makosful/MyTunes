package mytunes.gui.controller;

import com.jfoenix.controls.JFXListView;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import mytunes.be.Music;
import mytunes.gui.model.MainWindowModel;

/**
 * FXML Controller class
 *
 * @author Axl
 */
public class CreatePlaylistWindowController implements Initializable
{

    @FXML
    private Label lblError;
    @FXML
    private TextField txtPlaylistName;
    @FXML
    private TextField txtPlaylistSearch;
    @FXML
    private TextField txtSongSearch;
    @FXML
    private JFXListView<Music> listPlaylist;
    @FXML
    private JFXListView<Music> listSonglist;

    private String title;
    private Stage stage;
    private String error;
    private ObservableList<Music> playlist;
    private ObservableList<Music> songlist;
    private boolean save = false;

    // Objects
    MainWindowModel wm = new MainWindowModel();

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb)
    {
        error = "Please chose a name or cancel the proccess";
        this.playlist = FXCollections.observableArrayList();
        this.songlist = FXCollections.observableArrayList();

        listSonglist.setItems(wm.getSongList());
        listPlaylist.setItems(playlist);
        listSonglist.setItems(songlist);

        listPlaylist.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        listSonglist.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    /**
     * Creates a new playlist and saves it in the cache
     *
     * @param event
     */
    @FXML
    private void createPlaylist(ActionEvent event)
    {
        this.title = txtPlaylistName.getText();

        if (this.title.isEmpty())
        {
            lblError.setText(error);
        }
        else
        {
            save = true;
            cancel(event);
        }
    }

    /**
     * Closes the window
     *
     * @param event
     */
    @FXML
    private void cancel(ActionEvent event)
    {
        stage = (Stage) lblError.getScene().getWindow();
        stage.close();
    }

    /**
     * Gets the title of the new playlist
     *
     * @return A string containing the title of the new playlist
     */
    public String getTitle()
    {
        return title;
    }

    /**
     * Retrieves the currently stored playlist
     *
     * @return
     */
    public ObservableList<Music> getPlaylist()
    {
        return playlist;
    }

    /**
     * Sets the songlist
     *
     * @param songList
     */
    public void setSongList(ObservableList<Music> songList)
    {
        songlist.addAll(songList);
    }

    public boolean shouldSave()
    {
        return save;
    }

    @FXML
    private void moveAllToPlaylist(ActionEvent event)
    {
        playlist.addAll(songlist);
    }

    @FXML
    private void moveSelectedToPlaylist(ActionEvent event)
    {
        ObservableList<Music> selectedItems = listSonglist
                .getSelectionModel().getSelectedItems();

        this.playlist.addAll(selectedItems);
    }

    @FXML
    private void removeSelectedFromPlaylist(ActionEvent event)
    {
        ObservableList<Music> selectedItems = listPlaylist
                .getSelectionModel().getSelectedItems();

        this.playlist.removeAll(selectedItems);
    }

    @FXML
    private void removeAllFromPlaylist(ActionEvent event)
    {
        playlist.removeAll(wm.getSongList());
    }

    //<editor-fold defaultstate="collapsed" desc="Unimplimented">
    @FXML
    private void playlistSearch(ActionEvent event)
    {
    }

    @FXML
    private void songlistSearch(ActionEvent event)
    {
    }
    //</editor-fold>
}
