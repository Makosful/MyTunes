package mytunes.dal;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import mytunes.be.Music;

/**
 *
 * @author Storm
 */
public class MockMusic
{

    public ObservableList<Music> getAllSongs() throws FileNotFoundException,
                                                      IOException
    {
        ObservableList<Music> data = FXCollections.observableArrayList();

        try (BufferedReader CSVFile = new BufferedReader(
                new FileReader("./res/songs/mock.csv")))
        {
            CSVFile.readLine(); // Skips the header
            String dataRow = CSVFile.readLine();
            while (dataRow != null)
            {
                String[] dataArray = dataRow.split(",");
                data.add(new Music(
                        Integer.parseInt(dataArray[0]), // Number
                        dataArray[1], // Title
                        dataArray[2], // Album
                        dataArray[3], // Artist
                        Integer.parseInt(dataArray[4])) // Year
                );
                dataRow = CSVFile.readLine();
            }
            return data;
        }
    }
}
