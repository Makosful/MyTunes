package mytunes.gui.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXSlider;
import java.io.File;
import java.net.URL;
import java.util.Observable;
import java.util.ResourceBundle;
import javafx.beans.InvalidationListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;

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

    //private MediaView mView;
    private MediaPlayer mPlayer;
    private boolean isPlaying;
    // </editor-fold>

    @Override
    public void initialize(URL location, ResourceBundle resources) 
    {
        isPlaying = false;

        mediaPlayerSetup();

        volumeSlider.getParent().getParent().toFront();
    }

    private void mediaPlayerSetup() {
        String musicFile = "src/mytunes/media/testmusic.mp3";
        Media song = new Media(new File(musicFile).toURI().toString());
        mPlayer = new MediaPlayer(song);
        mediaView = new MediaView(mPlayer);
        mediaPane.getChildren().add(mediaView);
    }

    @FXML
    private void progressBarSetup() 
    {
        //
    }
    @FXML
    private void songStop(ActionEvent event) 
    {
        mPlayer.stop();
        System.out.println("Music Stopped...");
    }

    @FXML
    private void musicPlayPause(ActionEvent event) 
    {
        //Status status = mPlayer.getStatus();

        if (isPlaying == false) 
        {
            mPlayer.play();
            System.out.println("Music Playing...");
            isPlaying = true;
            btnPlayPause.setText("Pause");
        } 
        else 
        {
            mPlayer.pause();
            System.out.println("Music Paused...");
            isPlaying = false;
            btnPlayPause.setText("Play");
        }
    }

    @FXML
    private void volumeMixer(MouseEvent event) 
    {
        JFXSlider volSlide = volumeSlider;
        volSlide.setValue(mPlayer.getVolume() * 100);
        
        volSlide.valueProperty().addListener(new InvalidationListener() {
            @Override
            public void invalidated(javafx.beans.Observable observable) {
                mPlayer.setVolume(volSlide.getValue() / 100);
            }
        });
    }

    @FXML
    private void progressDrag(MouseEvent event) 
    {
        //
    }
}
