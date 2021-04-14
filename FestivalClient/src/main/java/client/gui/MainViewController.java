package client.gui;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.Show;
import model.User;
import services.FestivalException;
import services.IFestivalObserver;
import services.IFestivalServices;
import javafx.application.Platform;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class MainViewController implements IFestivalObserver {

    private IFestivalServices server;
    private User curentUser;

    public void setCurentUser(User curentUser) throws SQLException, FestivalException { this.curentUser = curentUser;       initModel(); }
    ObservableList<Show> model = FXCollections.observableArrayList();
    ObservableList<Show> filteredModel = FXCollections.observableArrayList();
    @FXML TableView<Show> showsTable = new TableView<>();
    @FXML TableColumn<Show,String> artistColumn;
    @FXML TableColumn<Show,String> locationColumn;
    @FXML TableColumn<Show,String> dateColumn;
    @FXML TableColumn<Show,Integer> availableColumn;
    @FXML TableColumn<Show,Integer> soldColumn;
    @FXML TextField namePurchaseArea;
    @FXML TextField seatsPurchaseArea;

    @FXML TableView<Show> filteredShowsTable = new TableView<>();
    @FXML TableColumn<Show,String> filteredArtistColumn;
    @FXML TableColumn<Show,String > filteredLocationColumn;
    @FXML TableColumn<Show,String > filteredHourColumn;
    @FXML TableColumn<Show,Integer> filteredAvailableColumn;
    @FXML DatePicker datePicker;
    Stage parent;

    public void setParent(Stage parent) {
        this.parent = parent;
    }

    public void setServer(IFestivalServices server){
        this.server=server;
        this.curentUser=null;

    }

    private void initModel() throws SQLException, FestivalException {
        model.clear();
        Show[] shows = server.findAllShows();
        model.setAll(shows);
        showsTable.setItems(model);

    }
    @FXML
    public void initialize() {
        showsTable.setRowFactory(tv -> new TableRow<Show>() {
            @Override
            public void updateItem(Show item, boolean empty) {
                super.updateItem(item, empty) ;
                if (item == null)
                    setStyle("");
                else if (item.getTotalSeats() == item.getSoldSeats())
                    setStyle("-fx-background-color: #ff6347;");
                else setStyle("");

            }
        });
        artistColumn.setCellValueFactory(param -> {
            Show show = param.getValue();
            String name = show.getArtist().getName();
            return new SimpleStringProperty(name);
        });
        locationColumn.setCellValueFactory(new PropertyValueFactory<>("location"));
        availableColumn.setCellValueFactory(param ->{
            Show show = param.getValue();
            int available = show.getTotalSeats()-show.getSoldSeats();
            return new SimpleObjectProperty<>(available);
        });
        soldColumn.setCellValueFactory(new PropertyValueFactory<>("soldSeats"));
        dateColumn.setCellValueFactory(param -> {
            Show show = param.getValue();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String formatDateTime = show.getDate().format(formatter);
            return new SimpleStringProperty(formatDateTime);
        });
    }

    public void handleBuyTickets(ActionEvent actionEvent) throws SQLException, FestivalException {

        String name = namePurchaseArea.getText();
        String seatsStr = seatsPurchaseArea.getText();
        if(name=="" || seatsStr=="")
            MessageAlert.showErrorMessage(null,"Incomplete name or seats!");
        else{
            int seats = Integer.parseInt(seatsStr);
            Show show =showsTable.getSelectionModel().getSelectedItem();
            this.server.buyTickets(show.getID(),name,seats);
            MessageAlert.showMessage(null, Alert.AlertType.CONFIRMATION,null,"Bought succesfully!");
        }
    }

    public void handleFilterDate(ActionEvent actionEvent) throws SQLException, FestivalException {

        filteredModel.clear();
        LocalDate date = datePicker.getValue();
        System.out.println(date);
        Show[] shows = this.server.findShowsByDate(date.atStartOfDay());
        filteredModel.setAll(shows);
        filteredShowsTable.setItems(filteredModel);

        filteredArtistColumn.setCellValueFactory(param -> {
            Show show = param.getValue();
            String name = show.getArtist().getName();
            return new SimpleStringProperty(name);
        });

        filteredHourColumn.setCellValueFactory(param ->{
            Show show = param.getValue();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
            String time = show.getDate().format(formatter);
            return new SimpleStringProperty(time);
        });

        filteredLocationColumn.setCellValueFactory(new PropertyValueFactory<Show, String>("location"));
        filteredAvailableColumn.setCellValueFactory(param ->{
            Show show = param.getValue();
            int available = show.getTotalSeats()-show.getSoldSeats();
            return new SimpleObjectProperty<>(available);
        });
    }

    @Override
    public void ticketBought() {
        Platform.runLater(() -> {
            try {
                System.out.println("Initing model");
                initModel();
            } catch (SQLException | FestivalException ex) {
                System.err.println(ex.getMessage());
            }
        });
    }

    public void handleLogout(ActionEvent actionEvent) {
        try {
            server.logout(curentUser, this);
            parent.show();
        } catch (FestivalException e) {
            System.out.println("Logout error " + e);
        }
        ((Node)(actionEvent.getSource())).getScene().getWindow().hide();
    }
}
