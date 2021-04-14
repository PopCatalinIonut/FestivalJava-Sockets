package network.utils;

import services.FestivalException;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


public abstract class AbstractServer {
    private int port;
    private ServerSocket server=null;
    public AbstractServer( int port){ this.port=port; }

    public void start() throws ServerException, FestivalException {
        try{
            server=new ServerSocket(port);
            while(true){
                System.out.println("Waiting for clients ...");
                Socket client=server.accept();
                System.out.println("Client connected ...");
                processRequest(client);
            }
        } catch (IOException e) {
            throw new ServerException("Starting server errror ",e);
        }finally {
            stop();
        }
    }

    protected abstract  void processRequest(Socket client);
    public void stop() throws FestivalException {
        try {
            server.close();
        } catch (IOException e) {
            throw new FestivalException("Closing server error ", e);
        }
    }
}
