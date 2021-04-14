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
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;


public class FestivalServicesRpcProxy implements IFestivalServices {
    private String host;
    private int port;

    private IFestivalObserver client;

    private ObjectInputStream input;
    private ObjectOutputStream output;
    private Socket connection;

    private BlockingQueue<Response> qresponses;
    private volatile boolean finished;
    public FestivalServicesRpcProxy(String host, int port) {
        this.host = host;
        this.port = port;
        qresponses=new LinkedBlockingQueue<Response>();
    }

    public void login(User user, IFestivalObserver client) throws FestivalException {
        initializeConnection();
        Request req=new Request.Builder().type(RequestType.LOGIN).data(user).build();
        sendRequest(req);
        Response response=readResponse();
        if (response.type()== ResponseType.OK){
            this.client=client;
            return;
        }
        if (response.type()== ResponseType.ERROR){
            String err=response.data().toString();
            closeConnection();
            throw new FestivalException(err);
        }
    }


    public void logout(User user, IFestivalObserver client) throws FestivalException {
        Request req=new Request.Builder().type(RequestType.LOGOUT).data(user).build();
        sendRequest(req);
        Response response=readResponse();
        closeConnection();
        if (response.type()== ResponseType.ERROR){
            String err=response.data().toString();
            throw new FestivalException(err);
        }
    }

    @Override
    public Show[] findAllShows() throws SQLException, FestivalException {
        Request req=new Request.Builder().type(RequestType.GET_ALL_SHOWS).data(null).build();
        sendRequest(req);
        Response response=readResponse();
        if (response.type()== ResponseType.ERROR){
            String err=response.data().toString();
            throw new FestivalException(err);
        }
        Show[] shows = (Show[]) response.data();
        return shows;
    }

    @Override
    public Show[] findShowsByDate(LocalDateTime date) throws SQLException, FestivalException {
        Request req=new Request.Builder().type(RequestType.GET_SHOWS_BY_DATE).data(date).build();
        sendRequest(req);
        Response response=readResponse();
        if (response.type()== ResponseType.ERROR){
            String err=response.data().toString();
            throw new FestivalException(err);
        }
        Show[] shows = (Show[]) response.data();
        return shows;
    }

    @Override
    public void buyTickets(int showID, String buyerName, int seats) throws FestivalException {
        Ticket ticket = new Ticket(showID,buyerName,seats);
        Request req=new Request.Builder().type(RequestType.BUY_TICKETS).data(ticket).build();
        sendRequest(req);
        Response response=readResponse();
        if (response.type()== ResponseType.ERROR){
            String err=response.data().toString();
            throw new FestivalException(err);
        }
    }

    private void closeConnection() {
        finished=true;
        try {
            input.close();
            output.close();
            connection.close();
            client=null;
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void sendRequest(Request request)throws FestivalException {
        try {
            output.writeObject(request);
            output.flush();
        } catch (IOException e) {
            throw new FestivalException("Error sending object "+e);
        }

    }

    private Response readResponse() throws FestivalException {
        Response response=null;
        try{

            response=qresponses.take();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return response;
    }
    private void initializeConnection() throws FestivalException {
        try {
            connection=new Socket(host,port);
            output=new ObjectOutputStream(connection.getOutputStream());
            output.flush();
            input=new ObjectInputStream(connection.getInputStream());
            finished=false;
            startReader();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void startReader(){
        Thread tw=new Thread(new ReaderThread());
        tw.start();
    }


    private void handleUpdate(Response response){
        if (response.type()== ResponseType.REFRESH_DATA){
            System.out.println("Handling ticket bought update from proxy");
            client.ticketBought();
        }
    }

    private boolean isUpdate(Response response){
        return response.type()==ResponseType.REFRESH_DATA;

    }

    private class ReaderThread implements Runnable{
        public void run() {
            while(!finished){
                try {
                    Object response=input.readObject();
                    System.out.println("response received "+response);
                    if (isUpdate((Response)response)){
                        handleUpdate((Response)response);
                    }else{

                        try {
                            qresponses.put((Response)response);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (IOException e) {
                    System.out.println("Reading error "+e);
                } catch (ClassNotFoundException e) {
                    System.out.println("Reading error "+e);
                }
            }
        }
    }
}
