package mytunes.gui.controller;

import com.jfoenix.controls.JFXListView;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.value.ObservableValue;
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
    private JFXListView<Music> listInPlaylist;
    @FXML
    private JFXListView<Music> listNotInList;

    private String title;
    private Stage stage;
    private String error;
    private ObservableList<Music> playlist;
    private ObservableList<Music> songlist;

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

        listNotInList.setItems(wm.getSongList());
        listInPlaylist.setItems(playlist);
        listNotInList.setItems(songlist);

        setAutoTextChange(txtPlaylistSearch, playlist);
        setAutoTextChange(txtSongSearch, songlist);

        listInPlaylist.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        listNotInList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
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

    @FXML
    private void moveAllToPlaylist(ActionEvent event)
    {
        playlist.addAll(wm.getSongList());
    }

    @FXML
    private void moveSelectedToPlaylist(ActionEvent event)
    {
        ObservableList<Music> selectedItems = listNotInList
                .getSelectionModel().getSelectedItems();

        this.playlist.addAll(selectedItems);
    }

    @FXML
    private void removeSelectedFromPlaylist(ActionEvent event)
    {
        ObservableList<Music> selectedItems = listInPlaylist
                .getSelectionModel().getSelectedItems();

        this.playlist.removeAll(selectedItems);
    }

    @FXML
    private void removeAllFromPlaylist(ActionEvent event)
    {
        playlist.removeAll(wm.getSongList());
    }

    @FXML
    private void playlistSearch(ActionEvent event)
    {
        txtSongSearch.getText();
    }

    @FXML
    private void songlistSearch(ActionEvent event)
    {
    }

    private void setAutoTextChange(TextField textField,
                                   ObservableList<Music> list)
    {
        textField.textProperty()
                .addListener((ObservableValue<? extends String> observable,
                              String oldText,
                              String newText) ->
                {
                    try
                    {
                        search(textField.getText(), list);
                    }
                    catch (Exception e)
                    {
                        System.out.println(e.getMessage());
                    }
                });
    }

    private void search(String text,
                        ObservableList<Music> list)
    {
        list.clear();
        List<Music> result = getSearchResult(text, wm.getSongList());
        list.addAll(result);
    }

    private List<Music> getSearchResult(String text,
                                        ObservableList<Music> list)
    {
        List<Music> searchResult = new ArrayList<>();

        list.forEach((music) ->
        {
            if ((music.getTitle().toLowerCase().contains(text.toLowerCase())
                 || music.getAlbum().toLowerCase().contains(text.toLowerCase())
                 || music.getArtist().toLowerCase().contains(text.toLowerCase())))
            {
                searchResult.add(music);
            }
        });

        return searchResult;
    }
}
