package network.utils;

import network.rpcprotocol.FestivalClientRpcWorker;
import services.IFestivalServices;

import java.net.Socket;


public class ChatRpcConcurrentServer extends AbsConcurrentServer {
    private IFestivalServices chatServer;
    public ChatRpcConcurrentServer(int port, IFestivalServices chatServer) {
        super(port);
        this.chatServer = chatServer;
        System.out.println("Chat- ChatRpcConcurrentServer");
    }

    @Override
    protected Thread createWorker(Socket client) {
        FestivalClientRpcWorker worker=new FestivalClientRpcWorker(chatServer, client);

        Thread tw=new Thread(worker);
        return tw;
    }

    @Override
    public void stop(){
        System.out.println("Stopping services ...");
    }
}
