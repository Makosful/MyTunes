package mytunes.gui.model;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import mytunes.be.Music;
import mytunes.bll.BLLManager;

/**
 *
 * @author Storm
 */
public class MainWindowModel
{

    private ObservableList<Music> list;

    // Objects
    private final BLLManager bllManager;

    public MainWindowModel()
    {
        this.bllManager = new BLLManager();
        this.list = FXCollections.observableArrayList();
    }

    public ObservableList<Music> getSongList()
    {
        return list;
    }

    public void loadSongList()
    {
        list.clear();
        list.addAll(bllManager.getSongList());
    }

    public void setPathAndName(List<File> chosenFiles) throws IOException {
        
        writeMusicFolderPath(chosenFiles.get(0).getAbsolutePath());
        
        for(int i = 0; i < chosenFiles.size(); i++){
            
            System.out.println(chosenFiles.get(i).getName());
        }
    }
    
    
    public void writeMusicFolderPath(String path) throws IOException
    {
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("path.txt")))
        {
            writer.write(path);
        }
    }
}
