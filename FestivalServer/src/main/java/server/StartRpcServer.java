package server;

import network.utils.AbstractServer;
import network.utils.ChatRpcConcurrentServer;
import network.utils.ServerException;
import persistence.repository.jdbc.ArtistsDBRepository;
import persistence.repository.jdbc.EmployeeRepository;
import persistence.repository.jdbc.ShowsDBRepository;
import persistence.repository.jdbc.TicketsDBRepository;
import services.FestivalException;
import services.IFestivalServices;

import java.io.IOException;
import java.util.Properties;

public class StartRpcServer {
    private static int defaultPort=55555;
    public static void main(String[] args) {

        Properties serverProps=new Properties();
        try {
            serverProps.load(StartRpcServer.class.getResourceAsStream("/festivalserver.properties"));
            System.out.println("Server properties set. ");
            serverProps.list(System.out);
        } catch (IOException e) {
            System.err.println("Cannot find festivalserver.properties "+e);
            return;
        }
        ShowsDBRepository showsRepo = new ShowsDBRepository(serverProps);
        ArtistsDBRepository artistsRepo = new ArtistsDBRepository(serverProps);
        TicketsDBRepository ticketsRepo = new TicketsDBRepository(serverProps);
        EmployeeRepository employeeRepo = new EmployeeRepository(serverProps);
        IFestivalServices festivalService=new FestivalServicesImpl(showsRepo,artistsRepo,ticketsRepo,employeeRepo);

        int festivalServerPort=defaultPort;
        try {
            festivalServerPort = Integer.parseInt(serverProps.getProperty("festival.server.port"));
        }catch (NumberFormatException nef){
            System.err.println("Wrong  Port Number"+nef.getMessage());
            System.err.println("Using default port "+defaultPort);
        }
        System.out.println("Starting server on port: "+festivalServerPort);
        AbstractServer server = new ChatRpcConcurrentServer(festivalServerPort, festivalService);
        try {
            server.start();
        } catch (ServerException | FestivalException e) {
            System.err.println("Error starting the server" + e.getMessage());
        }finally {
            try {
                server.stop();
            }catch(FestivalException e){
                System.err.println("Error stopping server "+e.getMessage());
            }
        }
    }
}
