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

    public ObservableList<Music> getAllSongsCSV() throws FileNotFoundException,
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
                        Integer.parseInt(dataArray[4]), // Year
                        "The sky") // Location
                );
                dataRow = CSVFile.readLine();
            }
            return data;
        }
    }

    public ObservableList<Music> getAllSongsLocal()
    {
        ObservableList<Music> data = FXCollections.observableArrayList();

        Music track;
        String album = "Golden Eye 007 OST";
        String artist = "Graeme Norgate & Grant Kirkhope";

        track = new Music(1, "James Bond Theme", album, artist, 0, "./res/songs/01 - James Bond Theme.mp3");
        data.add(track);
        track = new Music(2, "James Bond Theme", album, artist, 0, "./res/songs/02 - Mission Select.mp3");
        data.add(track);
        track = new Music(3, "James Bond Theme", album, artist, 0, "./res/songs/03 - 007 Watch Theme.mp3");
        data.add(track);
        track = new Music(4, "James Bond Theme", album, artist, 0, "./res/songs/04 - Dam.mp3");
        data.add(track);
        track = new Music(5, "James Bond Theme", album, artist, 0, "./res/songs/05 - Dam X.mp3");
        data.add(track);
        track = new Music(6, "James Bond Theme", album, artist, 0, "./res/songs/06 - Bungeee!.mp3");
        data.add(track);
        track = new Music(7, "James Bond Theme", album, artist, 0, "./res/songs/07 - Unsquare Water Dance.mp3");
        data.add(track);
        track = new Music(8, "James Bond Theme", album, artist, 0, "./res/songs/08 - Facility.mp3");
        data.add(track);
        track = new Music(9, "James Bond Theme", album, artist, 0, "./res/songs/09 - Facility X.mp3");
        data.add(track);
        track = new Music(10, "James Bond Theme", album, artist, 0, "./res/songs/10 - Runway.mp3");
        data.add(track);
        track = new Music(11, "James Bond Theme", album, artist, 0, "./res/songs/11 -  Runway X.mp3");
        data.add(track);
        track = new Music(12, "James Bond Theme", album, artist, 0, "./res/songs/12 - Plane Takeoff.mp3");
        data.add(track);

        return data;
    }
}
