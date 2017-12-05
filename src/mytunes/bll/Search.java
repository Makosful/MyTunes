/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mytunes.bll;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import mytunes.be.Music;
import mytunes.dal.SongDAO;

/**
 *
 * @author B
 */
public class Search
{
    private SongDAO sDAO;
    public Search() throws IOException
    {
        this.sDAO = new SongDAO();
    
    }
    
    /**
     * 
     * @param criterias
     * @param searchText
     * @return
     * @throws SQLException 
     */
    public List<Music> prepareSearch(List<String> criterias, String searchText) throws SQLException
    {   
        
        List<String> searchTables = new ArrayList();
        
       
        for(String criteria : criterias){
            
            if(isNumeric(searchText))
            {

               if(criteria.equalsIgnoreCase("year"))
               {

                   searchTables.add("Songs.releasedate");

               }


            }
            else
            { 
                if(criteria.equalsIgnoreCase("artist"))
                {
                    searchTables.add("Artist.artist");

                }else if(criteria.equalsIgnoreCase("title"))
                {

                    searchTables.add("Songs.title");

                }else if(criteria.equalsIgnoreCase("album"))
                {

                    searchTables.add("Albums.album");

                }else if(criteria.equalsIgnoreCase("Genre"))
                {

                    searchTables.add("Genre.genre");

                }
        
            }
        }
     
        
        String sqlSearch = "";
        boolean firstQm = true;
        for(int index = 0; index < searchTables.size(); index++)
        {
     
            if(firstQm == false)
            {
                sqlSearch += " OR ";
            }

            sqlSearch += searchTables.get(index)+" LIKE ?";

            firstQm = false;

        }
        
        List<Music> songs = sDAO.getSongsFromSearch(searchTables.size(), sqlSearch, searchText);
        return songs;
    }
    
    
     /**
     * Checks if a given string is numeric 
     * @param str
     * @return 
     */
    public boolean isNumeric(String str)  
    {  
      try  
      {  
        double d = Double.parseDouble(str);  
      }  
      catch(NumberFormatException nfe)  
      {  
        return false;  
      }  
      return true;  
    }
    
}