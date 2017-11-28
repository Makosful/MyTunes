/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mytunes.gui.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

/**
 * FXML Controller class
 *
 * @author Hussain
 */
public class EditSongController implements Initializable {

    @FXML
    private JFXTextField comboBoxTitle;
    @FXML
    private JFXTextField comboBoxArtist;
    @FXML
    private JFXTextField txtTime;
    @FXML
    private JFXTextField txtFile;
    @FXML
    private JFXComboBox<?> comboBoxCategory;
    @FXML
    private JFXComboBox<?> comboBoxCategory2;
    @FXML
    private JFXButton btnSave;
    @FXML
    private JFXButton btnCancel;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
}
