package mytunes.gui.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXSlider;
import java.io.File;
import java.net.URL;
import java.util.Observable;
import java.util.ResourceBundle;
import javafx.beans.InvalidationListener;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
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
    @FXML
    private ComboBox<String> playbackSpeed;

    private MediaPlayer mPlayer;
    private boolean isPlaying;
    // </editor-fold>

    @Override
    public void initialize(URL location, ResourceBundle resources) 
    {
        isPlaying = false;

        mediaPlayerSetup();
        playbackSettings();
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
    private void songStop(ActionEvent event) 
    {
        mPlayer.stop();
        System.out.println("Music Stopped");
    }

    @FXML
    private void musicPlayPause(ActionEvent event) 
    {
        //Status status = mPlayer.getStatus();

        if (isPlaying == false) 
        {
            mPlayer.play();
            System.out.println("Music Playing");
            isPlaying = true;
            btnPlayPause.setText("Pause");
        } 
        else 
        {
            mPlayer.pause();
            System.out.println("Music Paused");
            isPlaying = false;
            btnPlayPause.setText("Play");
        }
    }

    private void playbackSettings()
    {   
        //creating possible choices
        playbackSpeed.getItems().addAll("50% speed", "75% speed", "100% speed", "125% speed", "150% speed", "175% speed", "200% speed");
        //setting default value of the choicebox
        playbackSpeed.setValue("Play speed");
    }
    
    @FXML
    private void playbackAction(ActionEvent event) 
    {
        ObservableList<String> items = playbackSpeed.getItems();
            for(String item : items)
                {
                    System.out.println(item.toString());
                }
    }
    
    @FXML
    private void volumeMixer(MouseEvent event) 
    {
        JFXSlider volSlide = volumeSlider;
        volSlide.setValue(50);
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
