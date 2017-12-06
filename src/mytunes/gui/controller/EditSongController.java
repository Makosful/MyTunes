/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mytunes.gui.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
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
    
    private String genres;
    
    // SongID from MWController.
    private int songIdFromTable;
    // Stage of this controller's window.
    Stage stage;

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

    // Model of the controller.
    EditSongModel esModel;
    // List that contains all the genres.
    List<String> genreList;
    @FXML
    private Label lblError;
    @FXML
    private Label lblError2;
    @FXML
    private AnchorPane anchorPane;
    
    public EditSongController() throws IOException
    {
        esModel = new EditSongModel();
    }
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb)
    {
        genreList = new ArrayList();
        setComboBoxData();
    }
    // Addings genres to the list, and making it observable.
    // Initializing the combobox with the obsv. list.
    private void setComboBoxData()
    {
        genreList.add("hiphop");
        genreList.add("david");
        genreList.add("malthe THE G");
        genreList.add("malthe THE G2");

        ObservableList comboBoxList = FXCollections.observableList(genreList);
        comboBoxCategory.setItems(comboBoxList);
        
    }
    // Method to get genre from song.
    private void getSongsCurrentGenre(String genre)
    {
        comboBoxCategory.getSelectionModel().select(genre);
    }
    // Method to set data in textfields and combobox.
    public void setData(String title, String artist, int time, String file, String genre)
    {
        String timeString = Integer.toString(time);
        // Puts the text of the song into the textfields.
        txtFieldTitle.setText(title);
        txtFieldArtist.setText(artist);
        txtTime.setText(timeString);
        txtFile.setText(file);
        
        getSongsCurrentGenre(genre);
        // Getting the data from the current song
        // Before the values might change, based on users choices.
        oldGenre = genre;
        oldTitle = title;
        oldArtist = artist;
        oldFile = file;
    }
    /**
     * Returns the new title, artist, etc.
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
      return genres;
    }
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

        genres = null;
    }
    // Cancels the changes / No changes made to song.
    @FXML
    private void cancelChanges(ActionEvent event)
    {
        stage = (Stage) anchorPane.getScene().getWindow();
        stage.close();
    }
    // Goes through mainWindowModel, and then controller, takes the ID of the song from the table.
    public int getSongIdFromMainController(int id)
    {
        songIdFromTable = id;
        return id;
    }
    /**
     * So that the time textfield cannot be editted.
     */
    public void noEditableTime()
    {
        txtTime.setEditable(false);
    }
    /**
     * Saves changes to song, based on users changes.
     * @param event 
     */
    @FXML
    private void saveGenre(ActionEvent event)
    {
        if(genres == null)
        {
            genres = comboBoxCategory.getSelectionModel().getSelectedItem();
        }
        else
        {
        genres+="/"+comboBoxCategory.getSelectionModel().getSelectedItem();
        System.out.println(genres);
        }
    }
    /**
     * IF user has added genres, and decides to not save changes
     * Then the genres string are put to null.
     */
    public void closeWindow()
    {
        
        stage = (Stage) anchorPane.getScene().getWindow();
                
        stage.setOnHiding(new EventHandler<WindowEvent>()
        {

            @Override
            public void handle(WindowEvent event)
            {
                Platform.runLater(new Runnable()
                {

                    @Override
                    public void run()
                    {
                        genres = null;
                        stage.close();
                    }
                });
            }
        });
    }
    
    /**
     * A confirmation dialog, on whether you want to save songs changes or not.
     */
    private void confirmationDialog() throws SQLException
    {
        
        ButtonType btnYes = new ButtonType("Yes", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnNo = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);
        
        Alert alert = new Alert(AlertType.CONFIRMATION, "Are you sure you want save?", btnYes, btnNo );
        
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStyleClass().add("myunes/css/stylesheet.css");
        
        alert.setTitle("Confirmation Dialog");
        
        Optional <ButtonType> action = alert.showAndWait();
        
        if (action.get() == btnYes)
        {
            esModel.editSongDatabase(getOldTitle(), getTitle(), getOldArtist(), getArtist(), getSongId(), getOldFile(), getFile(), getOldGenre(), getGenre());
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
     * @return 
     */
    private boolean TxtFieldsFilled()
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
     * @return 
     */
    private boolean genreNotEmpty()
    {
        boolean genreNotEmpty = true;
        if (genres == null)
        {
            lblError.setText("Add atleast one genre");
            genreNotEmpty = false;
        }
        return genreNotEmpty;
    }

}   
