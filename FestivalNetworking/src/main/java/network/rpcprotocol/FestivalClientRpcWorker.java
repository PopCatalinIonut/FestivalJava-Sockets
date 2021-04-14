package network.rpcprotocol;

import model.Show;
import model.Ticket;
import model.User;
import services.FestivalException;
import services.IFestivalObserver;
import services.IFestivalServices;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.SQLException;
import java.time.LocalDateTime;


public class FestivalClientRpcWorker implements Runnable, IFestivalObserver {
    private IFestivalServices server;
    private Socket connection;

    private ObjectInputStream input;
    private ObjectOutputStream output;
    private volatile boolean connected;
    public FestivalClientRpcWorker(IFestivalServices server, Socket connection) {
        this.server = server;
        this.connection = connection;
        try{
            output=new ObjectOutputStream(connection.getOutputStream());
            output.flush();
            input=new ObjectInputStream(connection.getInputStream());
            connected=true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        while(connected){
            try {
                Object request=input.readObject();
                Response response=handleRequest((Request)request);
                if (response!=null){
                    sendResponse(response);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        try {
            input.close();
            output.close();
            connection.close();
        } catch (IOException e) {
            System.out.println("Error "+e);
        }
    }

    private static Response okResponse=new Response.Builder().type(ResponseType.OK).build();
    private static Response errorResponse=new Response.Builder().type(ResponseType.ERROR).build();
    private Response handleRequest(Request request){
        Response response=null;
        if (request.type()== RequestType.LOGIN){
            System.out.println("Login request ..."+request.type());
            User user=(User)request.data();
            try {
                server.login(user, this);
                return okResponse;
            } catch (FestivalException e) {
                connected=false;
                return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
            }
        }
        if (request.type()== RequestType.LOGOUT){
            System.out.println("Logout request");
           // LogoutRequest logReq=(LogoutRequest)request;
            User user=(User)request.data();
            try {
                server.logout(user, this);
                connected=false;
                return okResponse;

            } catch (FestivalException e) {
                return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
            }
        }if (request.type()== RequestType.GET_ALL_SHOWS){
            System.out.println("Getting all shows request");
            try {
                Show[] shows = this.server.findAllShows();
                return new Response.Builder().type(ResponseType.GET_SHOWS).data(shows).build();

            } catch (SQLException | FestivalException e) {
                return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
            }
        }if (request.type()== RequestType.GET_SHOWS_BY_DATE){
            LocalDateTime date = (LocalDateTime)request.data();
            System.out.println("Getting all shows request");
            try {
                Show[] shows = this.server.findShowsByDate(date);
                return new Response.Builder().type(ResponseType.GET_SHOWS).data(shows).build();

            } catch (SQLException | FestivalException e) {
                return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
            }
        }if (request.type()== RequestType.BUY_TICKETS){
            Ticket ticket = (Ticket)request.data();
            System.out.println("Buying ticket request");
            try {
                this.server.buyTickets(ticket.getFestivalID(),ticket.getBuyerName(),ticket.getSeats());
                return new Response.Builder().type(ResponseType.TICKET_BOUGHT).data(ticket).build();
            } catch (SQLException | FestivalException e) {
                return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
            }
        }
        return  response;
    }

    private void sendResponse(Response response) throws IOException{
        System.out.println("sending response "+response);
        output.writeObject(response);
        output.flush();
    }

    @Override
    public void ticketBought() {
        Response resp=new Response.Builder().type(ResponseType.REFRESH_DATA).data(null).build();
        System.out.println("Ticket bought and now refreshing data from worker ");
        try {
            sendResponse(resp);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
