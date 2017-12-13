package mytunes.gui.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import mytunes.be.Music;
import mytunes.be.Playlist;
import mytunes.gui.model.PlaylistWindowModel;

/**
 * FXML Controller class
 *
 * @author Axl
 */
public class PlaylistWindowController implements Initializable
{

    // FXML Variables
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
    @FXML
    private JFXButton btnSave;
    @FXML
    private JFXButton btnClose;

    // Objects
    private PlaylistWindowModel pm;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb)
    {
        this.pm = new PlaylistWindowModel();

        //<editor-fold defaultstate="collapsed" desc="Lists">
        listPlaylist.setItems(pm.getPlaylist());
        listSonglist.setItems(pm.getSonglist());
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Bindings">
        // Labels
        lblError.textProperty().bind(pm.getErrorTextProperty());

        // Buttons
        btnSave.textProperty().bind(pm.getSaveButtonTextProperty());

        // Lists
        listPlaylist.disableProperty().bind(pm.getPlaylistDisableProperty());
        listPlaylist.selectionModelProperty().bind(pm.getPlaylistSelectionProperty());
        listSonglist.selectionModelProperty().bind(pm.getSonglistSelectionProperty());
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Listeners">
        // Makes Bob Ross do what Bob Roos does best
        pm.setupSearchFunctionality(txtSongSearch, // The text field to use
                                    pm.getSonglist(), // A list of filtered songs
                                    pm.getSonglistBackup());  // A list of unfiltered songs

        pm.setupSearchFunctionality(txtPlaylistSearch, // The text field to use
                                    pm.getPlaylist(), // The list of filtered songs
                                    pm.getPlaylistBackup());    //A list of unfiltered songs
        //</editor-fold>
    }

    public PlaylistWindowModel getModel()
    {
        return pm;
    }

    //<editor-fold defaultstate="collapsed" desc="FXML">
    /**
     * Clears the SearchBar for the Playlist
     *
     * @param event
     */
    @FXML
    private void clearPlaylistSearch(ActionEvent event)
    {
        pm.fxmlClearPlaylistSearch(txtPlaylistSearch);
    }

    /**
     * Clears the SearchBar for the Song List
     *
     * @param event
     */
    @FXML
    private void clearSonglistSearch(ActionEvent event)
    {
        pm.fxmlClearSongSearch(txtSongSearch);
    }

    /**
     * Creates a new playlist and saves it in the cache
     *
     * @param event
     */
    @FXML
    private void createPlaylist(ActionEvent event)
    {
        pm.fxmlCreatePlaylist(txtPlaylistName, btnClose);
    }

    /**
     * Closes the window
     *
     * @param event
     */
    @FXML
    private void cancel(ActionEvent event)
    {
        pm.fxmlCancel(btnClose);
    }

    /**
     * Moves all the Musics to the Playlist
     *
     * @param event
     */
    @FXML
    private void moveAllToPlaylist(ActionEvent event)
    {
        pm.fxmlMoveAllToPlaylist();
    }

    /**
     * Moves the selected Musics to the Playlist
     *
     * @param event
     */
    @FXML
    private void moveSelectedToPlaylist(ActionEvent event)
    {
        pm.fxmlMoveSelectedToPlaylist(
                listSonglist.getSelectionModel().getSelectedItems());
    }

    /**
     * Remove the selected Musics from the Playlist
     *
     * @param event
     */
    @FXML
    private void removeSelectedFromPlaylist(ActionEvent event)
    {
        pm.fxmlRemoveSelectedFromPlaylist(
                listPlaylist.getSelectionModel().getSelectedIndices());
    }

    /**
     * Removes all Music from the Playlist
     *
     * @param event
     */
    @FXML
    private void removeAllFromPlaylist(ActionEvent event)
    {
        pm.fxmlRemoveAllFromPlaylist();
    }
    //</editor-fold>

    public void setPlaylist(Playlist playlist)
    {
        txtPlaylistName.setText(playlist.getTitle());
        pm.setPlaylist(playlist.getPlaylist());
    }
}
