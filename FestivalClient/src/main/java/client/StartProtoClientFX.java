package client;
import client.gui.LoginController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import network.protobuffprotocol.FestivalProtoProxy;
import network.rpcprotocol.FestivalServicesRpcProxy;
import services.IFestivalServices;

import java.io.IOException;
import java.util.Properties;


public class StartProtoClientFX extends Application {
    private Stage primaryStage;

    private static int defaultChatPort = 55555;
    private static String defaultServer = "localhost";


    public void start(Stage primaryStage) throws Exception {
        System.out.println("In start");
        Properties clientProps = new Properties();
        try {
            clientProps.load(StartRpcClientFX.class.getResourceAsStream("/festivalclient.properties"));
            System.out.println("Client properties set. ");
            clientProps.list(System.out);
        } catch (IOException e) {
            System.err.println("Cannot find festivalclient.properties " + e);
            return;
        }
        String serverIP = clientProps.getProperty("festival.server.host", defaultServer);
        int serverPort = defaultChatPort;

        try {
            serverPort = Integer.parseInt(clientProps.getProperty("festival.server.port"));
        } catch (NumberFormatException ex) {
            System.err.println("Wrong port number " + ex.getMessage());
            System.out.println("Using default port: " + defaultChatPort);
        }
        System.out.println("Using server IP " + serverIP);
        System.out.println("Using server port " + serverPort);


        IFestivalServices server = new FestivalProtoProxy(serverIP,serverPort);

        FXMLLoader loader = new FXMLLoader(
                getClass().getClassLoader().getResource("loginView.fxml"));
        Parent root=loader.load();


        LoginController ctrl =
                loader.<LoginController>getController();
        ctrl.setServer(server);


        primaryStage.setTitle("Festival chat");
        primaryStage.setScene(new Scene(root, 400, 300));
        primaryStage.show();
        ctrl.setStage(primaryStage);



    }


}


