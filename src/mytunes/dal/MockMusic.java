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
                new FileReader("res/songs/mock.csv")))
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
        String album = "YouTube Audio Library";

        track = new Music(1,
                          "A Mission Scoring Action",
                          album,
                          "Kevin MacLeod",
                          0,
                          "res/songs/A_Mission_Scoring_Action.mp3");
        data.add(track);

        track = new Music(2,
                          "Baltic Levity Thatched Villagers",
                          album,
                          "Kevin MacLeod",
                          0,
                          "res/songs/Baltic_Levity_Thatched_Villagers.mp3");
        data.add(track);

        track = new Music(3,
                          "Bright Wish",
                          album,
                          "Kevin MacLeod",
                          0,
                          "res/songs/Bright_Wish.mp3");
        data.add(track);

        track = new Music(4,
                          "Circus Waltz Silent Film Light",
                          album,
                          "Kevin MacLeod",
                          0,
                          "res/songs/Circus_Waltz_Silent_Film_Light.mp3");
        data.add(track);

        track = new Music(5,
                          "Comic Hero Silent Film Light",
                          album,
                          "Kevin MacLeod",
                          0,
                          "res/songs/Comic_Hero_Silent_Film_Light.mp3");
        data.add(track);

        track = new Music(6,
                          "Happy Little Elves",
                          album,
                          "Audionautix",
                          0,
                          "res/songs/Happy_Little_Elves.mp3");
        data.add(track);

        track = new Music(7,
                          "Hat the Jazz",
                          album,
                          "Twin Musicom",
                          0,
                          "res/songs/Hat_the_Jazz.mp3");
        data.add(track);

        track = new Music(8,
                          "Honey Bee",
                          album,
                          "Kevin MacLeod",
                          0,
                          "res/songs/Honey_Bee.mp3");
        data.add(track);

        track = new Music(9,
                          "Hot Swing",
                          album,
                          "Kevin MacLeod",
                          0,
                          "res/songs/Hot_Swing.mp3");
        data.add(track);

        track = new Music(10,
                          "Odd News",
                          album,
                          "Twin Musicom",
                          0,
                          "res/songs/Odd_News.mp3");
        data.add(track);

        track = new Music(11,
                          "Officers Call",
                          album,
                          "The U.S. Marine Corps Band",
                          0,
                          "res/songs/Officers_Call.mp3");
        data.add(track);

        track = new Music(12,
                          "Prelude No 5",
                          album,
                          "Chris Zabriskie",
                          0,
                          "res/songs/Prelude_No_5.mp3");
        data.add(track);

        return data;
    }
}
