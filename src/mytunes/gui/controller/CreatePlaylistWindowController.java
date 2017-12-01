package mytunes.gui.controller;

import com.jfoenix.controls.JFXListView;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener.Change;
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
    private ObservableList<Music> playlistBackup;
    private ObservableList<Music> songlist;
    private ObservableList<Music> songlistBackup;
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
        // Static error message for when trying to save a playlist without a name
        error = "Please chose a name or cancel the proccess";

        // Creates the two lists in this window
        this.playlist = FXCollections.observableArrayList();
        this.playlistBackup = FXCollections.observableArrayList();
        this.songlist = FXCollections.observableArrayList();
        this.songlistBackup = FXCollections.observableArrayList();

        // Loads the song list
        wm.loadSongList();
        songlist.addAll(wm.getSongList());
        playlistBackup.addAll(playlist);
        songlistBackup.addAll(songlist);

        // Assigns the two lists
        listPlaylist.setItems(playlist);
        listSonglist.setItems(songlist);

        // Allows for multiple items in each list to be selected
        listPlaylist.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        listSonglist.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        // Makes Bob Ross do what Bob Roos does best
        bobRoss(txtSongSearch, // The text field to use
                songlist, // A list of filtered songs
                songlistBackup);  // A list of unfiltered songs

        bobRoss(txtPlaylistSearch, // The text field to use
                playlist, // The list of filtered songs
                playlistBackup);    //A list of unfiltered songs

        setupPlaylistListener();
    }

    private void setupPlaylistListener()
    {
        if (playlist.isEmpty())
        {
            listPlaylist.setDisable(true);
        }
        playlist.addListener((Change<? extends Music> c) ->
        {
            if (playlist.isEmpty())
            {
                listPlaylist.setDisable(true);
            }
            else
            {
                listPlaylist.setDisable(false);
            }
        });
    }

    //<editor-fold defaultstate="collapsed" desc="Buttons">
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
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Move Song Buttons">
    @FXML
    private void moveAllToPlaylist(ActionEvent event)
    {
        this.playlist.addAll(songlist);
        this.playlistBackup.addAll(songlist);
    }

    @FXML
    private void moveSelectedToPlaylist(ActionEvent event)
    {
        ObservableList<Music> selectedItems = listSonglist
                .getSelectionModel().getSelectedItems();

        this.playlist.addAll(selectedItems);
        this.playlistBackup.addAll(selectedItems);
    }

    @FXML
    private void removeSelectedFromPlaylist(ActionEvent event)
    {
        ObservableList<Music> selectedItems = listPlaylist
                .getSelectionModel().getSelectedItems();

        this.playlist.removeAll(selectedItems);
        this.playlistBackup.removeAll(selectedItems);
    }

    @FXML
    private void removeAllFromPlaylist(ActionEvent event)
    {
        this.playlistBackup.removeAll(playlist);
        this.playlist.clear();
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="External Connectors">
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
    //</editor-fold>

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

    /**
     * It's bob Ross. No documentation needed.
     *
     * @param txt
     * @param fullList
     * @param changedList
     */
    private void bobRoss(TextField txt,
                         ObservableList<Music> fullList,
                         ObservableList<Music> changedList)
    {
        txt.textProperty().addListener(
                (ObservableValue<? extends String> observable,
                 String oldText,
                 String newValue) ->
        {
            search(txt.getText(), fullList, changedList);
        });
    }

    private void search(String txt,
                        ObservableList<Music> fullList,
                        ObservableList<Music> changedList)
    {
        fullList.clear();
        List<Music> result = getSearchResult(txt, changedList);
        fullList.addAll(result);
    }

    private List<Music> getSearchResult(String txt, ObservableList<Music> list)
    {
        List<Music> searchResult = new ArrayList();

        list.forEach((music) ->
        {
            if (music.getTitle().toLowerCase().contains(txt.toLowerCase())
                || music.getArtist().toLowerCase().contains(txt.toLowerCase())
                || music.getAlbum().toLowerCase().contains(txt.toLowerCase()))
            {
                searchResult.add(music);
            }
        });
        return searchResult;
    }
}
