package client.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import model.User;
import services.FestivalException;
import services.IFestivalServices;
import java.io.IOException;
import java.sql.SQLException;

public class LoginController {

    private IFestivalServices server;
    @FXML TextField idField;
    @FXML TextField passField;
    private Stage parent;

    public void setServer(IFestivalServices server) { this.server=server; }

    public void handleLogIn(ActionEvent actionEvent) throws SQLException {

        if (!passField.getText().isEmpty() && !idField.getText().isEmpty()) {

            try{
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("/mainView.fxml"));
                AnchorPane root = loader.load();
                MainViewController mainCtrl = loader.getController();

                mainCtrl.setServer(server);
                User user = new User(idField.getText(),passField.getText());


                this.server.login(user,mainCtrl);
                mainCtrl.setCurentUser(user);
                mainCtrl.setParent(parent);
                Stage primaryStage = new Stage();
                primaryStage.setTitle("Welcome"+ user.getID());
                primaryStage.setScene(new Scene(root, 750, 400));
                primaryStage.show();
                ((Node)(actionEvent.getSource())).getScene().getWindow().hide();

                passField.clear();
                idField.clear();
                } catch(IOException | FestivalException e){
                System.err.println("Error"+ e);
            }
        }
    }


    public void setStage(Stage root) {
        this.parent=root;
    }
}
