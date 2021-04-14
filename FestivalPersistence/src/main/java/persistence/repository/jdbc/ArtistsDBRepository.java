package persistence.repository.jdbc;

import model.Artist;
import model.validator.ArtistValidator;
import model.validator.ValidationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import persistence.repository.ArtistsRepositoryInterface;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;


public class ArtistsDBRepository implements ArtistsRepositoryInterface {

    private JdbcUtils dbUtils;
    private static final Logger logger= LogManager.getLogger(JdbcUtils.class);

    private ArtistValidator vali;
    public ArtistsDBRepository(Properties props) {
        this.dbUtils = new JdbcUtils(props);
        this.vali= new ArtistValidator();
    }

    public void add(Artist artist) throws SQLException {

        try{
            vali.validate(artist);
            Connection con = dbUtils.getConnection();
            try (PreparedStatement preStmt = con.prepareStatement("insert into Artists (ID, name) values (?,?)")) {

                preStmt.setInt(1, artist.getID());
                preStmt.setString(2, artist.getName());
                preStmt.executeUpdate();
                logger.trace("Artist added succesfully");
            }
            catch (SQLException ex) {
                logger.error("Error DB " + ex);
            }
            con.close();
        }catch (ValidationException val){
            logger.error("Artist is not valid!");
        }
    }


    @Override
    public Iterable<Artist> findAll() throws SQLException {
        Connection con=dbUtils.getConnection();
        List<Artist> artists = new ArrayList<>();
        try(PreparedStatement preStmt=con.prepareStatement("select * from Artists")){
            try(ResultSet result=preStmt.executeQuery()){
                while(result.next()){

                    int id=result.getInt("id");
                    String name = result.getString("name");
                    Artist artist = new Artist(id,name);
                    artists.add(artist);
                }
            }
        }catch(SQLException e){
            logger.error("Error DB"+e);
        }
        con.close();
        return artists;
    }
}
