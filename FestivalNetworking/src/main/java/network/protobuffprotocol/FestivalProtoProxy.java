package network.protobuffprotocol;

import model.Show;
import model.Ticket;
import model.User;
import network.rpcprotocol.ResponseType;
import services.FestivalException;
import services.IFestivalObserver;
import services.IFestivalServices;

import java.io.*;
import java.net.Socket;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;


public class FestivalProtoProxy implements IFestivalServices {
    private String host;
    private int port;

    private IFestivalObserver client;

    private InputStream input;
    private OutputStream output;
    private Socket connection;

    private BlockingQueue<FestivalProtobufs.FestivalResponse> qresponses;
    private volatile boolean finished;
    public FestivalProtoProxy(String host, int port) {
        this.host = host;
        this.port = port;
        qresponses= new LinkedBlockingQueue<>();
    }

    public void login(User user, IFestivalObserver client) throws FestivalException {
        initializeConnection();
        sendRequest(ProtoUtils.createLoginRequest(user));
        FestivalProtobufs.FestivalResponse response=readResponse();
        if (response.getType()== FestivalProtobufs.FestivalResponse.Type.OK){
            this.client=client;
            return;
        }
        if (response.getType()== FestivalProtobufs.FestivalResponse.Type.ERROR){
            String err=ProtoUtils.getError(response);
            closeConnection();
            throw new FestivalException(err);
        }
    }


    public void logout(User user, IFestivalObserver client) throws FestivalException {
        sendRequest(ProtoUtils.createLogoutRequest(user));
        FestivalProtobufs.FestivalResponse response=readResponse();
        closeConnection();
        if (response.getType()== FestivalProtobufs.FestivalResponse.Type.ERROR){
            String err=ProtoUtils.getError(response);
            throw new FestivalException(err);
        }
    }

    @Override
    public Show[] findAllShows() throws  FestivalException {
        sendRequest(ProtoUtils.createGetAllShowsRequest());
        FestivalProtobufs.FestivalResponse response=readResponse();
        if (response.getType()== FestivalProtobufs.FestivalResponse.Type.ERROR){
            String err=ProtoUtils.getError(response);
            throw new FestivalException(err);
        }
        Show[] shows = ProtoUtils.getShows(response);
        return shows;
    }

    @Override
    public Show[] findShowsByDate(LocalDateTime date) throws FestivalException {
        sendRequest(ProtoUtils.createGetShowsByDateRequest(date));
        FestivalProtobufs.FestivalResponse response=readResponse();
        if (response.getType()== FestivalProtobufs.FestivalResponse.Type.ERROR){
            String err=ProtoUtils.getError(response);
            throw new FestivalException(err);
        }
        Show[] shows = ProtoUtils.getShows(response);
        return shows;
    }

    @Override
    public void buyTickets(int showID, String buyerName, int seats) throws FestivalException {
        Ticket ticket = new Ticket(showID,buyerName,seats);
        sendRequest(ProtoUtils.createBuyTicketsRequest(ticket));
        FestivalProtobufs.FestivalResponse response=readResponse();
        if (response.getType()== FestivalProtobufs.FestivalResponse.Type.ERROR){
            String err=ProtoUtils.getError(response);
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

    private void sendRequest(FestivalProtobufs.FestivalRequest request)throws FestivalException {
        try {
            System.out.println("Seding request...");
            //output.writeObject(request);
            request.writeDelimitedTo(output);
            output.flush();
            System.out.println("Request sent.");
        } catch (IOException e) {
            throw new FestivalException("Error sending object "+e);
        }

    }

    private FestivalProtobufs.FestivalResponse readResponse() throws FestivalException {
        FestivalProtobufs.FestivalResponse response=null;
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
            output=connection.getOutputStream();
            //output.flush();
            input=connection.getInputStream();
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


    private void handleUpdate(FestivalProtobufs.FestivalResponse response){
        if (response.getType()== FestivalProtobufs.FestivalResponse.Type.REFRESH_DATA){
            System.out.println("Handling ticket bought update from proxy");
            client.ticketBought();
        }
    }

    private boolean isUpdate(FestivalProtobufs.FestivalResponse response){
        return response.getType()==FestivalProtobufs.FestivalResponse.Type.REFRESH_DATA;

    }

    private class ReaderThread implements Runnable{
        public void run() {
            while(!finished){
                try {
                    FestivalProtobufs.FestivalResponse response=FestivalProtobufs.FestivalResponse.parseDelimitedFrom(input);
                    System.out.println("response received "+response);
                    if (isUpdate(response)){
                        handleUpdate(response);
                    }else{

                        try {
                            qresponses.put(response);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (IOException e) {
                    System.out.println("Reading error "+e);
                }
            }
        }
    }
}
