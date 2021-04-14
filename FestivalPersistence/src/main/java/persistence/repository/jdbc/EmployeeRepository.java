package persistence.repository.jdbc;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

public class EmployeeRepository {

    private JdbcUtils dbUtils;
    private static final Logger logger= LogManager.getLogger(JdbcUtils.class);

    public EmployeeRepository(Properties props) {
        this.dbUtils = new JdbcUtils(props);
    }
    public boolean existsEmployee(String username, String password){
        Connection con = dbUtils.getConnection();

        try (PreparedStatement preStmt = con.prepareStatement("select * from Employee where username = ? and password = ?")) {
            preStmt.setString(1, username);
            preStmt.setString(2, password);
            try (ResultSet result = preStmt.executeQuery()) {
                if(!result.next())
                    return false;
                else
                    return true;
            }
            } catch (SQLException throwables) {
                logger.error("Cannot connect to BD");
            }
        return false;
    }
}
