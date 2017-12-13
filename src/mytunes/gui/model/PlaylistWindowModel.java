package mytunes.gui.model;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import java.util.ArrayList;
import java.util.Collections;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import mytunes.be.Music;
import mytunes.bll.exception.BLLException;

/**
 *
 * @author Axl
 */
public class PlaylistWindowModel
{

    // FXML Variables
    private Label lblError;
    private TextField txtPlaylistName;
    private JFXListView<Music> listPlaylist;
    private JFXListView<Music> listSonglist;
    private JFXButton btnSave;

    // Instance variables
    private String title;
    private Stage stage;
    private String error;
    private ObservableList<Music> playlist;
    private ObservableList<Music> playlistBackup;
    private ObservableList<Music> songlist;
    private ObservableList<Music> songlistBackup;
    private boolean save;

    // Objects
    private MainWindowModel wm;

    public PlaylistWindowModel()
    {
        this.wm = new MainWindowModel();

        listPlaylist = new JFXListView<>();
        listPlaylist.setItems(playlist);
        listSonglist = new JFXListView<>();
        listSonglist.setItems(songlist);

        lblError = new Label();

        txtPlaylistName = new TextField();

        btnSave = new JFXButton("Create");

        // Static error message for when trying to save a playlist without a name
        error = "Please chose a name or cancel the proccess";

        // Creates the two lists in this window
        this.playlist = FXCollections.observableArrayList();
        this.playlistBackup = FXCollections.observableArrayList();
        this.songlist = FXCollections.observableArrayList();
        this.songlistBackup = FXCollections.observableArrayList();

        try
        {
            // Loads the song list
            wm.loadSongList();
        }
        catch (BLLException ex)
        {
            System.out.println(ex.getMessage());
        }
        songlist.addAll(wm.getSongList());
        playlistBackup.addAll(playlist);
        songlistBackup.addAll(songlist);

        // Assigns the two lists
        listPlaylist.setItems(playlist);
        listSonglist.setItems(songlist);

        // Allows for multiple items in each list to be selected
        listPlaylist.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        listSonglist.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        setupPlaylistListener();

        this.save = false;
    }

    //<editor-fold defaultstate="collapsed" desc="Property Getters">
    public StringProperty getErrorTextProperty()
    {
        return lblError.textProperty();
    }

    public StringProperty getSaveButtonTextProperty()
    {
        return btnSave.textProperty();
    }

    public BooleanProperty getPlaylistDisableProperty()
    {
        return listPlaylist.disableProperty();
    }

    public ObjectProperty<MultipleSelectionModel<Music>> getPlaylistSelectionProperty()
    {
        return listPlaylist.selectionModelProperty();
    }

    public ObjectProperty<MultipleSelectionModel<Music>> getSonglistSelectionProperty()
    {
        return listSonglist.selectionModelProperty();
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Getters">
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

    public ObservableList<Music> getPlaylistBackup()
    {
        return this.playlistBackup;
    }

    /**
     * Getrieves the SongList
     *
     * @return
     */
    public ObservableList<Music> getSonglist()
    {
        return songlist;
    }

    public ObservableList<Music> getSonglistBackup()
    {
        return this.songlistBackup;
    }

    public boolean shouldSave()
    {
        return save;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Setters">
    /**
     * Sets the songlist
     *
     * @param songList
     */
    public void setSongList(ObservableList<Music> songList)
    {
        songlist.addAll(songList);
    }

    public void setPlaylist(ObservableList<Music> playlist)
    {
        //this.txtPlaylistName.setText(playlist.getTitle());
        this.playlist.addAll(playlist);
        this.playlistBackup.addAll(playlist);
    }

    public void setSaveButton(String text)
    {
        btnSave.setText(text);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Setups">
    /**
     * Sets up a listener for the playlist
     */
    public void setupPlaylistListener()
    {
        // First checks if the playlist is empty
        // This is the initial state when opening the window
        if (this.listPlaylist.getItems().isEmpty())
        {
            // If it is, disable it
            this.listPlaylist.setDisable(true);

            // Otherwise leave it enabled
        }

        // Adds the listener to the playlist
        // This is how it'll change as the list is being changed
        this.listPlaylist.getItems().addListener((ListChangeListener.Change<? extends Music> c) ->
        {
            // Checks if the playlist has become empty
            if (playlist.isEmpty())
            {
                // If it has, disable it
                listPlaylist.setDisable(true);
            }
            else
            {
                // If it's not empty, reenable it
                listPlaylist.setDisable(false);
            }
        });
    }

    /**
     * Sets up search functionality
     *
     * Connects a TextField and an ObservableList to provide a realtime search
     * of the connected list.
     * The editedList and the completeList must not be the same, but instead
     * mirror two versions containing the same entries, as the later list will
     * be used to check matches while the former list will be used to display
     * the results
     *
     * @param txt          The TextField to add a listener to
     * @param editedList   The list in which the searhc results should be put in
     * @param completeList A mirror of the search list, but containing all the
     *                     entires
     */
    public void setupSearchFunctionality(TextField txt,
                                         ObservableList<Music> editedList,
                                         ObservableList<Music> completeList)
    {
        txt.textProperty().addListener(
                (ObservableValue<? extends String> observable,
                 String oldText,
                 String newValue) ->
        {
            wm.searchPlaylist(txt.getText(), editedList, completeList);
        });
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="FXML Methods">
    /**
     * Clears the SearchBar for the Playlist
     *
     * @param txt
     */
    public void fxmlClearPlaylistSearch(TextField txt)
    {
        txt.setText("");
    }

    /**
     * Clears the searchbar for the Song List
     *
     * @param txt
     */
    public void fxmlClearSongSearch(TextField txt)
    {
        txt.setText("");
    }

    /**
     * Creates or saves the Playlist
     *
     * @param name
     * @param btn
     */
    public void fxmlCreatePlaylist(TextField name, JFXButton btn)
    {
        this.title = name.getText();

        if (this.title.isEmpty())
        {
            lblError.setText(error);
        }
        else
        {
            save = true;
            fxmlCancel(btn);
        }
    }

    /**
     * Closes the window without saving
     *
     * @param btn
     */
    public void fxmlCancel(JFXButton btn)
    {
        stage = (Stage) btn.getScene().getWindow();
        stage.close();
    }

    /**
     * Adds all the Music to the Playlist
     */
    public void fxmlMoveAllToPlaylist()
    {
        this.playlist.addAll(songlist);
        this.playlistBackup.addAll(songlist);
    }

    /**
     * Add the selected Musics to the Playlist
     *
     * @param list
     */
    public void fxmlMoveSelectedToPlaylist(ObservableList<Music> list)
    {
        this.playlist.addAll(list);
        this.playlistBackup.addAll(list);
    }

    /**
     * Removes the selected Musics from the Playlist
     *
     * @param list
     */
    public void fxmlRemoveSelectedFromPlaylist(ObservableList<Integer> list)
    {
        ArrayList<Integer> writeable = new ArrayList();

        for (int i = 0; i < list.size(); i++)
        {
            writeable.add(list.get(i));
        }

        Collections.sort(writeable, Collections.reverseOrder());

        writeable.forEach((integer) ->
        {
            this.playlist.remove(integer.intValue());
        });

        listPlaylist.getSelectionModel().clearSelection();
    }

    /**
     * Removes all Music from the Playlist
     */
    public void fxmlRemoveAllFromPlaylist()
    {
        this.playlistBackup.removeAll(playlist);
        this.playlist.clear();
    }
    //</editor-fold>
}
