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
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import mytunes.gui.model.EditSongModel;

/**
 * FXML Controller class
 *
 * @author Hussain
 */
public class EditSongController implements Initializable
{
    
    String oldTitle;
    String oldFile;
    String oldArtist;
    String oldGenre;
    
    private int songIdFromTable;

    @FXML
    private JFXTextField txtTime;
    @FXML
    private JFXTextField txtFile;
    @FXML
    private JFXComboBox<String> comboBoxCategory;
    @FXML
    private JFXComboBox<String> comboBoxCategory2;
    @FXML
    private JFXButton btnSave;
    @FXML
    private JFXButton btnCancel;
    @FXML
    private JFXTextField txtFieldTitle;
    @FXML
    private JFXTextField txtFieldArtist;

    
    EditSongModel esModel;
    
    List<String> genreList;
    
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
    
    private void setComboBoxData()
    {
        genreList.clear();
        genreList.add("hiphop");
        genreList.add("david");
        genreList.add("malthe THE G");
        genreList.add("malthe THE G2");

        ObservableList comboBoxList = FXCollections.observableList(genreList);
        comboBoxCategory.setItems(comboBoxList);
        comboBoxCategory2.setItems(comboBoxList);
    }

    public void setData(String title, String artist, int time, String file, String genre)
    {
        String timeString = Integer.toString(time);
        
        txtFieldTitle.setText(title);
        txtFieldArtist.setText(artist);
        txtTime.setText(timeString);
        txtFile.setText(file);
        comboBoxCategory.getSelectionModel().select(genre);
        
        oldTitle = txtFieldTitle.getText();
        oldArtist = txtFieldArtist.getText();
        oldFile = "Officers_Call.mp3";
    }
    /**
     * Returns the new title, artist, t
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
      return comboBoxCategory.getSelectionModel().getSelectedItem();
    }
    
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
        return comboBoxCategory2.getSelectionModel().getSelectedItem();
    }
    
    public int getSongId()
    {
        return songIdFromTable;  
    }
    
    @FXML
    private void saveChanges(ActionEvent event) throws SQLException
    {
        esModel.editSongDatabase(getOldTitle(), getTitle(),getOldArtist(), getArtist(),getSongId(), getOldFile(), getFile(), getOldGenre(), getGenre());
        System.out.println("test");
    }

    @FXML
    private void cancelChanges(ActionEvent event)
    {
        System.out.println("TEST");
    }
    // Goes through mainWindowModel, and then controller, takes the ID of the song from the table.
    public int getSongIdFromMainController(int id)
    {
        songIdFromTable = id;
        return id;
    }
}
