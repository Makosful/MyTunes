package mytunes.bll;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import mytunes.be.Music;
import mytunes.dal.SongDAO;

/**
 *
 * @author B
 */
public class Search
{

    // Objects
    private final SongDAO sDAO;

    /**
     * Constructor
     *
     * @throws IOException
     */
    public Search() throws IOException
    {
        this.sDAO = new SongDAO();

    }

    /**
     * Prepares a search for the database
     *
     * @param criterias  The filters to apply for the search
     * @param searchText The text to search for
     *
     * @return Returns a list containing the filtered results
     *
     * @throws SQLException
     */
    public List<Music> prepareSearch(List<String> criterias, String searchText)
            throws SQLException
    {

        // Creates a new ArrayList to store the tables to search in
        List<String> searchTables = new ArrayList();

        criterias.forEach((criteria) ->
        {
            // Check if the search is of numbers
            if (isNumeric(searchText))
            {
                // If the search is only numbers

                // Then apply the year filter
                if (criteria.equalsIgnoreCase("year"))
                {
                    // Adds the years to the search queue
                    searchTables.add("Songs.releasedate");
                }
            }
            // If the search contains characters
            else
            {
                // Then first check if the artist filter has been selected
                if (criteria.equalsIgnoreCase("artist"))
                {
                    // If it is, apply the filter
                    searchTables.add("Artist.artist");
                }

                // Then check if the title is selected
                if (criteria.equalsIgnoreCase("title"))
                {
                    // If so, apply it
                    searchTables.add("Songs.title");
                }

                // Then check if the album is checked
                if (criteria.equalsIgnoreCase("album"))
                {
                    // If so, apply it
                    searchTables.add("Albums.album");
                }

                // Then check if the genre has been checked
                if (criteria.equalsIgnoreCase("Genre"))
                {
                    // If so, apply it
                    searchTables.add("Genre.genre");
                }
                
                // Then check if the Description has been checked
                if (criteria.equalsIgnoreCase("Description"))
                {
                    // If so, apply it
                    searchTables.add("Songs.description");
                }
            }
        });

        //create part of sql query from the specific criteria/tables to search in
        String sqlSearch = "";
        boolean firstQm = true;
        for (int index = 0; index < searchTables.size(); index++)
        {
            //if its the first table to search in after "where" do not include "OR"
            if (firstQm == false)
            {
                sqlSearch += " OR ";
            }
            
            //the table for instance "Songs.title LIKE ?"
            sqlSearch += searchTables.get(index) + " LIKE ?";

            firstQm = false;

        }

        List<Music> songs = sDAO.getSongsFromSearch(searchTables.size(), sqlSearch, searchText);
        return songs;
    }

    /**
     * Checks if a given string is numeric
     *
     * @param str
     *
     * @return
     */
    public boolean isNumeric(String str)
    {
        try
        {
            double d = Double.parseDouble(str);
        }
        catch (NumberFormatException nfe)
        {
            return false;
        }
        return true;
    }

}
