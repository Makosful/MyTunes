/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mytunes.gui.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import mytunes.gui.model.EditSongModel;

/**
 * FXML Controller class
 *
 * @author Hussain
 */
public class EditSongController implements Initializable
{

    //<editor-fold defaultstate="collapsed" desc="oldStrings">
    String oldTitle;
    String oldFile;
    String oldArtist;
    String oldGenre;
//</editor-fold>

    // The genre the user has selects for his song,
    // that he wants to edit.
    private String userSelectedGenres;
    
    // SongID from MWController.
    private int songIdFromTable;
    
    // Stage of this controller's window.
    Stage stage;
    
    // Model of the controller.
    EditSongModel esModel;
    
    //<editor-fold defaultstate="collapsed" desc="FXML variables">
    @FXML
    private JFXTextField txtTime;
    @FXML
    private JFXTextField txtFile;
    @FXML
    private JFXComboBox<String> comboBoxCategory;
    @FXML
    private JFXButton btnSave;
    @FXML
    private JFXButton btnCancel;
    @FXML
    private JFXTextField txtFieldTitle;
    @FXML
    private JFXTextField txtFieldArtist;
    @FXML
    private JFXTextField selectedGenres;
    @FXML
    private Label lblError;
    @FXML
    private Label lblError2;
    @FXML
    private AnchorPane anchorPane;
//</editor-fold>

    public EditSongController() throws IOException
    {
        esModel = new EditSongModel();
    }

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb)
    {
        setComboBoxData();
        noEditableTime();
    }

    //<editor-fold defaultstate="collapsed" desc="Setters">
    // Addings genres to the list, and making it observable.
    // Initializing the combobox with the obsv. list.
    private void setComboBoxData()
    {
       esModel.setComboBoxData(comboBoxCategory);
    }

    // Method to get genre from song, and place it first in combobox.
    private void setSongGenreFirstInComboBox(String genre)
    {
        esModel.setSongGenreFirstInComboBox(comboBoxCategory, genre);
    }

    // Method to set data in textfields and combobox.
    public void setData(String title, String artist, int time, String file, String genre)
    {
        // UserSelectedgenres to null everytime user wants to edit a new song.
        userSelectedGenres = null;

        setSongGenreFirstInComboBox(genre);
        String timeString = Integer.toString(time);
        // Puts the text of the song into the textfields.
        txtFieldTitle.setText(title);
        txtFieldArtist.setText(artist);
        txtTime.setText(timeString);
        txtFile.setText(file);

        // Getting the data from the current song
        // Before the values might change, based on users choices.
        oldGenre = genre;
        oldTitle = title;
        oldArtist = artist;
        oldFile = file;
    }
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Getters">
    // Goes through mainWindowModel, and then controller, takes the ID of the song from the table.
    public int getSongIdFromMainController(int id)
    {
        songIdFromTable = id;
        return id;
    }

    /**
     * Returns the new title, artist, etc.
     *
     * @return
     */
    public String getTitle()
    {
        return txtFieldTitle.getText();
    }

    public String getArtist()
    {
        return txtFieldArtist.getText();
    }

    public String getFile()
    {
        return txtFile.getText();
    }

    public String getGenre()
    {
        return userSelectedGenres;
    }
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="gettersOldStrings">
    // Gets the old / current strings, before user might change them.
    public String getOldFile()
    {
        return oldFile;
    }

    public String getOldTitle()
    {
        return oldTitle;
    }

    public String getOldArtist()
    {
        return oldArtist;
    }

    public String getOldGenre()
    {
        return oldGenre;
    }

    // Returns the song id from tableview.
    public int getSongId()
    {
        return songIdFromTable;
    }
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="FXML methods">
    // Edits the song, after user input, genre, etc.
    @FXML
    private void saveChanges(ActionEvent event) throws SQLException
    {
        if (!TxtFieldsFilled())
        {
            return;
        }
        if (TxtFieldsFilled() && !genreNotEmpty())
        {
            return;
        }
        confirmationDialog();

        userSelectedGenres = null;
    }

    // Cancels the changes / No changes made to song.
    @FXML
    private void cancelChanges(ActionEvent event)
    {
        stage = (Stage) anchorPane.getScene().getWindow();
        stage.close();
    }

    /**
     * If user regrets the genre he added, he can delete it again, by selecting
     * it in the combobox, and press delete.
     *
     * @param event
     */
    @FXML
    private void deleteSpecificGenre(ActionEvent event)
    {
        String selectedGenre = comboBoxCategory.getSelectionModel().getSelectedItem();
        userSelectedGenres = userSelectedGenres.replace(selectedGenre, "");

        selectedGenres.setText(userSelectedGenres);
    }

    /**
     * Saves changes to song, based on users changes.
     *
     * @param event
     */
    @FXML
    private void saveGenre(ActionEvent event)
    {
        slashInGenresOrNot();
    }
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Command">
    private void slashInGenresOrNot()
    {
        if (userSelectedGenres == null)
        {
            userSelectedGenres = comboBoxCategory.getSelectionModel().getSelectedItem();
        }
        else
        {
            userSelectedGenres += "/" + comboBoxCategory.getSelectionModel().getSelectedItem();
        }
    }
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Methods from model">
    /**
     * A confirmation dialog, on whether you want to save songs changes or not
     */
    private void confirmationDialog() throws SQLException
    {
        esModel.confirmationDialog(anchorPane, getOldTitle(), getTitle(), getOldArtist(),
                                   getArtist(), getSongId(), getOldFile(), getFile(), getOldGenre(), getGenre());
    }

    /**
     * Checks whether the textfields are empty or not.
     * Only the ones that can be modified obv.
     *
     * @return
     */
    private boolean TxtFieldsFilled()
    {
        return esModel.TxtFieldsFilled(txtFieldArtist, txtFieldTitle, txtFile, lblError);
    }

    /**
     * Checks whether genres string is empty or not.
     *
     * @return
     */
    private boolean genreNotEmpty()
    {
        return esModel.genreNotEmpty(userSelectedGenres, lblError);
    }

    /**
     * Gets the genrelist for the combobox.
     *
     * @return
     */
    private List<String> genreList()
    {
        return esModel.genreList();
    }

    /**
     * So that the time textfield cannot be editted.
     */
    public void noEditableTime()
    {
        esModel.noEditableTime(txtTime);
    }
//</editor-fold>

}
