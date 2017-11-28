package mytunes.gui.controller;

import com.jfoenix.controls.JFXButton;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Axl
 */
public class CreatePlaylistWindowController implements Initializable
{

    @FXML
    private TextField txtPlaylistName;
    @FXML
    private JFXButton btnPlaylistCreate;
    @FXML
    private JFXButton btnPlaylistQuit;
    @FXML
    private Label lblError;

    private String title;
    private Stage stage;
    private String error;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb)
    {
        // TODO
        error = "Please chose a name or cancel the proccess";
    }

    /**
     * Saves the name of the playlist and passes it on to the main window
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
        stage = (Stage) btnPlaylistQuit.getScene().getWindow();
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
}
