/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mytunes.gui.model;

import java.io.IOException;
import java.sql.SQLException;
import mytunes.bll.BLLManager;

/**
 *
 * @author Hussain
 */
public class EditSongModel
{ 
    BLLManager bllManager;

    public EditSongModel() throws IOException
    {
        this.bllManager = new BLLManager();
    }
    
    public void editSongDatabase(String oldTitle, String newTitle, String oldArtist, String newArtist, int songId,
    String oldFile, String newFile, String oldGenre, String newGenre) throws SQLException
    {
        bllManager.editSongDataBase(oldTitle, newTitle, oldArtist, newArtist, songId, oldFile, newFile, oldGenre, newGenre);
    }

}
