package mytunes.gui.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXSlider;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import javafx.scene.control.Slider;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
/**
 *
 * @author Axl
 */
public class MainWindowController implements Initializable
{
    // <editor-fold defaultstate="collapsed" desc=" FXML & Variables">

    @FXML
    private JFXButton btnPlayPause;
    @FXML
    private JFXSlider volumeSlider;
    @FXML
    private JFXButton btnStop;
    @FXML
    private Label lblTimer;
    @FXML
    private Slider progressSlider;
    
    private boolean isPlaying;
// </editor-fold>

    @Override
    public void initialize(URL url, ResourceBundle rb)
    {
        isPlaying = !true;
    }

    @FXML
    private void songStop(ActionEvent event) 
    {
        
    }

    @FXML
    private void musicPlayPause(ActionEvent event) throws FileNotFoundException 
    {
        if(isPlaying == true)
        {
            btnPlayPause.setText("Pause");
            isPlaying = !true;
            
            String filePath = new File("media/testmusic.mp3").toURI().toString();
            Media mp3 = new Media(filePath.toString());
            MediaPlayer mp3Player = new MediaPlayer(mp3);

        }
        else 
        {
            btnPlayPause.setText("Play");
            isPlaying = true;
            
        }
    }

    @FXML
    private void volumeMixer(MouseEvent event) 
    {
        
    }

    @FXML
    private void progressDrag(MouseEvent event) {
    }

}
