package server;

import model.Artist;
import model.Show;
import model.Ticket;
import model.User;
import persistence.repository.jdbc.ArtistsDBRepository;
import persistence.repository.jdbc.EmployeeRepository;
import persistence.repository.jdbc.ShowsDBRepository;
import persistence.repository.jdbc.TicketsDBRepository;
import services.FestivalException;
import services.IFestivalObserver;
import services.IFestivalServices;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class FestivalServicesImpl implements IFestivalServices {

    private ShowsDBRepository showsRepo ;
    private ArtistsDBRepository artistsRepo;
    private TicketsDBRepository ticketsRepo ;
    private EmployeeRepository employeeRepo ;
    private Map<String, IFestivalObserver> loggedClients;

    public FestivalServicesImpl(ShowsDBRepository showsRepo, ArtistsDBRepository artistsRepo,TicketsDBRepository ticketsRepo,EmployeeRepository employeeRepo) {

        this.showsRepo=showsRepo;
        this.artistsRepo=artistsRepo;
        this.ticketsRepo=ticketsRepo;
        this.employeeRepo=employeeRepo;
        loggedClients=new ConcurrentHashMap<>();
    }


    public synchronized void login(User user, IFestivalObserver client) throws FestivalException {
        boolean exists =  this.employeeRepo.existsEmployee(user.getID(),user.getPasswd());
        if (exists==true){
            if(loggedClients.get(user.getId())!=null)
                throw new FestivalException("User already logged in.");
            loggedClients.put(user.getId(), client);
        }else
            throw new FestivalException("Authentication failed.");
    }

    public synchronized void logout(User user, IFestivalObserver client) throws FestivalException {
        IFestivalObserver localClient=loggedClients.remove(user.getId());
        if (localClient==null)
            throw new FestivalException("User "+user.getId()+" is not logged in.");
    }

    @Override
    public Show[] findAllShows() throws SQLException {
        return this.showsRepo.findAll().toArray(new Show[0]);
    }

    @Override
    public Show[] findShowsByDate(LocalDateTime localDateTime) throws SQLException {
        return this.showsRepo.findShowsByDate(localDateTime).toArray(new Show[0]);
    }

    @Override
    public synchronized void buyTickets(int showID, String buyerName, int seats) throws FestivalException, SQLException {

        boolean available = this.showsRepo.hasAvailableSeats(showID,seats);
        if(available){
            Ticket ticket = new Ticket(showID,buyerName,seats);
            this.showsRepo.updateSeats(showID,seats);
            this.ticketsRepo.add(ticket);
            for(Map.Entry<String,IFestivalObserver> entry : loggedClients.entrySet()) {
                IFestivalObserver user = entry.getValue();
                System.out.println("Notyfing " + entry.getKey() + " to refresh");
                if (user != null)
                    user.ticketBought();
            }
        }else throw new FestivalException("Not so many seats available");
    }

}
