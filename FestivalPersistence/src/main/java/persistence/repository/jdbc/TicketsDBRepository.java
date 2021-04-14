package persistence.repository.jdbc;

import model.Ticket;
import model.validator.TicketValidator;
import model.validator.ValidationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import persistence.repository.TicketsRepositoryInterface;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class TicketsDBRepository implements TicketsRepositoryInterface {

    private JdbcUtils dbUtils;
    private TicketValidator vali;
    private static final Logger logger= LogManager.getLogger(JdbcUtils.class);

    public TicketsDBRepository(Properties props) {
        this.dbUtils = new JdbcUtils(props);
        vali= new TicketValidator();
    }

    public void add(Ticket ticket) throws SQLException {

        try{
            vali.validate(ticket);
            Connection con = dbUtils.getConnection();
            try (PreparedStatement preStmt = con.prepareStatement("insert into Tickets (showID, buyerName, seats) values (?,?,?)")) {

                preStmt.setInt(1, ticket.getFestivalID());
                preStmt.setString(2,ticket.getBuyerName());
                preStmt.setInt(3,ticket.getSeats());
                preStmt.executeUpdate();
                logger.trace("Ticket added succesfully");
            }
            catch (SQLException ex) {
                logger.error("Error DB " + ex);
            }
            con.close();
        }catch (ValidationException vali){
            logger.error("Ticket is invalid");
        }
    }

    @Override
    public Iterable<Ticket> findAll() throws SQLException {
        Connection con=dbUtils.getConnection();
        List<Ticket> tickets = new ArrayList<>();

        try(PreparedStatement preStmt=con.prepareStatement("select * from Tickets")){
            try(ResultSet result=preStmt.executeQuery()){
                while(result.next()){

                    int id=result.getInt("id");
                    int festivalId=result.getInt("showID");
                    String buyerName = result.getString("buyerName");
                    int seats = result.getInt("seats");
                    Ticket ticket = new Ticket(id,festivalId,buyerName,seats);
                    tickets.add(ticket);
                }
            }
        }catch(SQLException e){
            logger.error("Error DB"+e);
        }
        con.close();
        return tickets;
    }
}
