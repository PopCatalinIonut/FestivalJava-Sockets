package persistence.repository.jdbc;

import model.Artist;
import model.Show;
import model.validator.ShowValidator;
import model.validator.ValidationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import persistence.repository.ShowsRepositoryInterface;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;


public class ShowsDBRepository implements ShowsRepositoryInterface {

    private JdbcUtils dbUtils;

    private ShowValidator vali;
    private static final Logger logger= LogManager.getLogger(JdbcUtils.class);

    public ShowsDBRepository(Properties props) {

        this.dbUtils = new JdbcUtils(props);
        this.vali = new ShowValidator();
    }

    public boolean hasAvailableSeats(int showID, int seats) throws SQLException {
        Connection con = dbUtils.getConnection();
        try(PreparedStatement preStmt = con.prepareStatement("select totalSeats,soldSeats from Shows where ID = ?")){
            preStmt.setInt(1,showID);
            try (ResultSet result = preStmt.executeQuery()){
                if(!result.next())
                    logger.error("Show not found");
                else{
                    int totalSeats = result.getInt("totalSeats");
                    int soldSeats = result.getInt("soldSeats");
                    if(soldSeats + seats > totalSeats)
                        return false;
                    return true;

                }
            }
        }
        return false;
    }
    public void updateSeats(int showID, int seats){
        Connection con = dbUtils.getConnection();
        try(PreparedStatement preStmt = con.prepareStatement("update Shows set soldSeats = soldSeats+ ? where Id = ? ")) { {
                preStmt.setInt(2,showID);
                preStmt.setInt(1,seats);
                preStmt.executeUpdate();
            }
        } catch (SQLException e) {
            logger.error("Error DB "+ e);
        }
    }
    public List<Show> findShowsByDate(LocalDateTime date) throws SQLException {

        Connection con = dbUtils.getConnection();
        List<Show> shows = new ArrayList<>();

        try (PreparedStatement preStmt = con.prepareStatement("select * from Shows where date between ? and ?")) {

            Date date2 = Date.valueOf(date.toLocalDate());
            preStmt.setString(1, date2.toString());
            Date date3 = Date.valueOf(date.toLocalDate().plusDays(1));
            preStmt.setString(2, date3.toString());
            try (ResultSet result = preStmt.executeQuery()) {
                while (result.next()) {
                    int showID = result.getInt("id");
                    String showName = result.getString("name");
                    String location = result.getString("location");
                    LocalDateTime Sdate = result.getTimestamp("date").toLocalDateTime();
                    int totalSeats = result.getInt("totalSeats");
                    int soldSeats = result.getInt("soldSeats");
                    int artistID = result.getInt("artistID");
                    try (PreparedStatement preStmt2 = con.prepareStatement("select name from Artists where ID= ?")) {
                        preStmt2.setInt(1, artistID);
                        try (ResultSet result2 = preStmt2.executeQuery()) {
                            ;
                            String artistName = result2.getString("name");
                            Artist artist = new Artist(artistID, artistName);
                            Show newShow = new Show(showID, showName, location, Sdate, artist, totalSeats, soldSeats);
                            shows.add(newShow);
                        } catch (SQLException e) {
                            logger.error("Artist not found");
                        }
                    }
                }
            } catch (SQLException e) {
                logger.error("Error DB" + e);
            }
            return shows;
        }
    }
    @Override
    public void add(Show show) throws SQLException {

        try{
            vali.validate(show);
            Connection con = dbUtils.getConnection();
            try (PreparedStatement preStmt = con.prepareStatement("insert into Shows (ID, name, location, date, artistId, totalSeats, soldSeats) values (?,?,?,?,?,?,?)")) {

                preStmt.setInt(1, show.getID());
                preStmt.setString(2, show.getName());
                preStmt.setString(3, show.getLocation());
                preStmt.setObject(4,show.getDate().toString().replace("T"," "));
                preStmt.setInt(5,show.getArtist().getID());
                preStmt.setInt(6,show.getTotalSeats());
                preStmt.setInt(7,show.getSoldSeats());
                preStmt.executeUpdate();
            }
            catch (SQLException ex) {
                logger.error("Error DB " + ex);
            }
        con.close();
        }catch (ValidationException val){
            logger.error("Show is not valid");
        }
    }

    @Override
    public List<Show> findAll() throws SQLException {
        Connection con=dbUtils.getConnection();
        List<Show> shows = new ArrayList<>();
        try(PreparedStatement preStmt=con.prepareStatement("select * from Shows")){
            try(ResultSet result=preStmt.executeQuery()){
                while(result.next()){
                    int showID=result.getInt("id");
                    String showName = result.getString("name");
                    String location= result.getString("location");
                    LocalDateTime date = result.getTimestamp("date").toLocalDateTime();
                    int totalSeats = result.getInt("totalSeats");
                    int soldSeats = result.getInt("soldSeats");
                    int artistID = result.getInt("artistID");
                    try(PreparedStatement preStmt2=con.prepareStatement("select name from Artists where ID= ?")){
                        preStmt2.setInt(1,artistID);
                        try(ResultSet result2=preStmt2.executeQuery()){ ;
                            String artistName = result2.getString("name");
                            Artist artist = new Artist(artistID,artistName);
                            Show newShow = new Show(showID,showName,location, date,artist,totalSeats,soldSeats);
                            shows.add(newShow);
                        }
                        catch(SQLException e){
                            Show newShow = new Show(showID,showName,location,date,null,totalSeats,soldSeats);
                            shows.add(newShow);
                        }
                    }
                }
            }
        }catch(SQLException e){
            logger.error("Error DB"+e);
        }
        con.close();
        return shows;
    }
}
