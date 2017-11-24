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

        MediaPlayerMethod();

        String musicFile = "src/mytunes/media/testmusic.mp3";
        Media song = new Media(new File(musicFile).toURI().toString());
        mPlayer = new MediaPlayer(song);

        mPlayer.play();

        mediaView = new MediaView(mPlayer);
        mediaPane.getChildren().add(mediaView);
    }

    private void MediaPlayerMethod()
    {
    }

    @FXML
    private void songStop(ActionEvent event)
    {
        mPlayer.stop();
        System.out.println("Music Stopped");
    }

    @FXML
    private void musicPlayPause(ActionEvent event)
    {
        //Status status = mPlayer.getStatus();
        isPlaying = !isPlaying;

        if (isPlaying == false)
        {
            mPlayer.play();
            btnPlayPause.setText("Pause");
        }
        else
        {
            mPlayer.pause();
            btnPlayPause.setText("Play");
        }
    }

    @FXML
    private void volumeMixer(MouseEvent event)
    {

    }

    @FXML
    private void progressDrag(MouseEvent event)
    {

    }
}
