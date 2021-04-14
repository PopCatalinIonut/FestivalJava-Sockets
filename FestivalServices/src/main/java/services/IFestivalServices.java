package services;


import model.Show;
import model.User;

import java.sql.SQLException;
import java.time.LocalDateTime;

public interface IFestivalServices {
     void login(User user, IFestivalObserver client) throws FestivalException;
     void logout(User user, IFestivalObserver client) throws FestivalException;

     Show[] findAllShows() throws SQLException, FestivalException;
     Show[] findShowsByDate(LocalDateTime localDateTime) throws SQLException, FestivalException;

     void buyTickets(int showID, String buyerName, int seats) throws FestivalException, SQLException;
}
