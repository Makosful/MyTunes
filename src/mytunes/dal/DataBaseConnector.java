package mytunes.dal;

import com.microsoft.sqlserver.jdbc.SQLServerDataSource;
import com.microsoft.sqlserver.jdbc.SQLServerException;
import java.io.IOException;
import java.sql.Connection;

/**
 *
 * @author Hussain
 */
public class DataBaseConnector 
{
        private SQLServerDataSource dataSource;
        /**
         * Setting the relevant info to the database connection.
         * @throws IOException 
         */
    public DataBaseConnector() throws IOException
    {
        dataSource = new SQLServerDataSource();

        dataSource.setServerName("EASV-DB2");
        dataSource.setPortNumber(1433);
        dataSource.setDatabaseName("MyTunes_Roccat");
        dataSource.setUser("CS2017A_8_java");
        dataSource.setPassword("javajava");
    }
    /**
     * Getting the connection from the database.
     * @return
     * @throws SQLServerException 
     */
    public Connection getConnection() throws SQLServerException
    {
        return dataSource.getConnection();
    }

}
