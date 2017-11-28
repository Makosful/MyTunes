package mytunes;

import java.io.File;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 *
 * @author Axl
 */
public class MyTunes extends Application
{

    @Override
    public void start(Stage stage) throws Exception
    {
        Parent root = FXMLLoader.load(getClass().getResource("gui/view/MainWindow.fxml"));

        Scene scene = new Scene(root);

        File file = new File("./res/icon/TrollTunes56x56.png");
        Image icon = new Image(file.toURI().toString());

        stage.getIcons().add(icon);
        stage.setTitle("TrollTunes - The superior way to listen to music");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        launch(args);
    }

}
