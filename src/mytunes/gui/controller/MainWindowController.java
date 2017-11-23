package mytunes.gui.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXSlider;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.input.MouseEvent;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.Pane;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.media.Media;
import java.io.File;
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
    @FXML
    private Pane mediaPane;
    @FXML
    private MediaView mediaView;
    private MediaView mView;
    private MediaPlayer mPlayer;

    private boolean isPlaying;
// </editor-fold>

        @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        isPlaying = true;
        
        String musicFile = "media/testmusic.mp3";
        Media song = new Media(new File(musicFile).toURI().toString());
        MediaPlayer player = new MediaPlayer(song);
        player.play();
        
        mView = new MediaView(mPlayer);
        mediaPane.getChildren().add(mView);
    }

    @FXML
    private void songStop(ActionEvent event) 
    {
        
    }

    @FXML
    private void musicPlayPause(ActionEvent event) 
    {
        //Status status = mPlayer.getStatus();
        
        if(isPlaying == true)
        {
            btnPlayPause.setText("Pause");
            isPlaying = !true;
            mPlayer.play();
        }
        else 
        {
            btnPlayPause.setText("Play");
            isPlaying = true;
            mPlayer.pause();            
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
