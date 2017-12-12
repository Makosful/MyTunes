package mytunes.gui.model;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import mytunes.bll.BLLManager;

/**
 *
 * @author Hussain
 */
public class EditSongModel
{

    BLLManager bllManager;
    List<String> genreList;
    ObservableList comboBoxList;

    public EditSongModel() throws IOException
    {
        this.bllManager = new BLLManager();
        genreList = new ArrayList();
    }
    // Changes the song's info.

    public void editSongDatabase(String oldTitle, String newTitle, String oldArtist, String newArtist, int songId,
                                 String oldFile, String newFile, String oldGenre, String newGenre) throws SQLException
    {
        bllManager.editSongDataBase(oldTitle, newTitle, oldArtist, newArtist, songId, oldFile, newFile, oldGenre, newGenre);
    }
    
    
    /**
     * A confirmation dialog, on whether you want to save songs changes or not.
     * If users answer is yet, the method editsongdatabase is called.
     * @param anchorPane
     * @param oldTitle
     * @param newTitle
     * @param oldArtist
     * @param newArtist
     * @param songId
     * @param oldFile
     * @param newFile
     * @param oldGenre
     * @param newGenre
     * @throws java.sql.SQLException
     */
    public void confirmationDialog(AnchorPane anchorPane, String oldTitle, String newTitle, String oldArtist, String newArtist, int songId,
    String oldFile, String newFile, String oldGenre, String newGenre) throws SQLException
    {
        
        ButtonType btnYes = new ButtonType("Yes", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnNo = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);
        
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want save?", btnYes, btnNo );
        
        DialogPane dialogPane = alert.getDialogPane();
        
        alert.setTitle("Confirmation Dialog");
        
        Optional <ButtonType> action = alert.showAndWait();
        
        if (action.get() == btnYes)
        {
            editSongDatabase(oldTitle, newTitle, oldArtist, newArtist, songId, oldFile, newFile, oldGenre, newGenre);
            Stage stage = (Stage) anchorPane.getScene().getWindow();
            stage.close();
        }
        if (action.get() == btnNo)
        {
            alert.close();
        }  
    }
    
    /**
     * Checks whether the textfields are empty or not. 
     * Only the ones that can be modified obv.
     * @param txtFieldArtist
     * @param txtFieldTitle
     * @param txtFile
     * @param lblError
     * @return 
     */
    public boolean TxtFieldsFilled(TextField txtFieldArtist, TextField txtFieldTitle, TextField txtFile, Label lblError)
    {
        boolean notEmpty = true;

        List<TextField> txtFields = new ArrayList();
        
        txtFields.add(txtFieldArtist);
        txtFields.add(txtFieldTitle);
        txtFields.add(txtFile);
        
        for (TextField textFields : txtFields)
            {
                if (textFields.getText().isEmpty())
                {
                    lblError.setText("Fill the missing TextField(s)");
                     notEmpty = false;
                    return notEmpty;
                }
            }
        return notEmpty;
    }
    /**
     * Checks whether genres string is empty or not.
     * @param genres
     * @param lblError
     * @return 
     */
    public boolean genreNotEmpty(String genres, Label lblError)
    {
        boolean genreNotEmpty = true;
        if (genres == null)
        {
            lblError.setText("Add atleast one genre");
            genreNotEmpty = false;
        }
        return genreNotEmpty;
    }
    
        /**
     * So that the time textfield cannot be editted.
     * @param txtTime
     */
    public void noEditableTime(TextField txtTime)
    {
        txtTime.setEditable(false);
    }
    /**
     * Adding genres to list.
     * @return 
     */
    public List<String> genreList()
    {
        genreList.add("HipHop");
        genreList.add("Classic");
        genreList.add("Jazz");
        genreList.add("Pop");
        genreList.add("Latin");
        genreList.add("R&B Soul");
        genreList.add("Rock");
        genreList.add("Metal");
        genreList.add("J-POP");
        genreList.add("Malthe The G");
        
        return genreList;
    }
    
    public ObservableList<String> genreListObv()
    {
        return comboBoxList;
    }
       
    // Addings genres to the list, and making it observable.
    // Initializing the combobox with the obsv. list.
    public void setComboBoxData(ComboBox comboBoxCategory)
    {
        comboBoxList = FXCollections.observableList(genreList());
        comboBoxCategory.setItems(comboBoxList);
    }
    
        // Method to get genre from song, and place it first in combobox.
    public void setSongGenreFirstInComboBox(ComboBox comboBoxCategory, String genre)
    {
        comboBoxCategory.getSelectionModel().select(genre);
    }
    
}
