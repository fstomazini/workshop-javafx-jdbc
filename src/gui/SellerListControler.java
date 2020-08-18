package gui;

import db.DbException;
import db.DbIntegrityException;
import gui.SellerFormControler;
import gui.listners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Utils;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.DepartmentService;
import model.SellerService;
import model.entities.Seller;
import sample.Main;

import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class SellerListControler implements Initializable, DataChangeListener {

    private SellerService service;

    @FXML
    private TableView<Seller> tableViewSellers;

    @FXML
    private TableColumn<Seller, Integer> tableColumnID;

    @FXML
    private TableColumn<Seller, String> tableColumnName;

    @FXML
    private TableColumn<Seller, String> tableColumnEmail;

    @FXML
    private TableColumn<Seller, Date> tableColumnBirthDate;

    @FXML
    private TableColumn<Seller, Double> tableColumnBaseSalary;

    @FXML
    private TableColumn<Seller, Seller> tableColumnEDIT;

    @FXML
    private TableColumn<Seller, Seller> tableColumnREMOVE;

    @FXML
    private Button btNew;

    private ObservableList<Seller> obsList;


    @FXML
    public void onBtNewAction(ActionEvent event){
        Stage parentStage = Utils.currentStage(event);
        Seller obj = new Seller();
        createDialogForm(obj, "/gui/SellerForm.fxml", parentStage);
    }

    private void initializeNodes(){
        tableColumnID.setCellValueFactory(new PropertyValueFactory<>("id"));
        tableColumnName.setCellValueFactory(new PropertyValueFactory<>("name"));
        tableColumnEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        tableColumnBirthDate.setCellValueFactory(new PropertyValueFactory<>("birthDate"));
        Utils.formatTableColumnDate(tableColumnBirthDate, "dd/MM/yyyy");
        tableColumnBaseSalary.setCellValueFactory(new PropertyValueFactory<>("baseSalary"));
        Utils.formatTableColumnDouble(tableColumnBaseSalary, 2);
    }


    public void setSellerService(SellerService service) {
        this.service = service;
    }

    public void updateTableView(){
        if(service == null){
            throw new IllegalStateException("Service was null!");
        }
        List<Seller> list = service.findAll();
        obsList = FXCollections.observableList(list);
        tableViewSellers.setItems(obsList);
        initEditButtons();
        initRemoveButtons();

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeNodes();

        Stage stage = (Stage) Main.getMainScene().getWindow();
        tableViewSellers.prefHeightProperty().bind(stage.heightProperty());

    }

    private void createDialogForm(Seller obj,String absoluteName, Stage parentStage){
       try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
            Pane pane = loader.load();

            SellerFormControler controller = loader.getController();
            controller.setEntity(obj);
            controller.setService(new SellerService(), new DepartmentService());
            controller.loadAssociatedObjects();
            controller.subscribeDataChangeListener(this);
            controller.updateFormData();
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Enter Seller Data");
            dialogStage.setScene(new Scene(pane));
            dialogStage.setResizable(false);
            dialogStage.initOwner(parentStage);
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.showAndWait();

        }catch(IOException e){
            Alerts.showAlert("IO Exception", "Error loading the view", e.getMessage(), Alert.AlertType.ERROR );
        }
    }

    private void initEditButtons() {
        tableColumnEDIT.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
        tableColumnEDIT.setCellFactory(param -> new TableCell<Seller, Seller>() {
            private final Button button = new Button("edit");
            @Override
            protected void updateItem(Seller obj, boolean empty) {
                super.updateItem(obj, empty);
                if (obj == null) {
                    setGraphic(null);
                    return;
                }
                setGraphic(button);
                button.setOnAction(
                        event -> createDialogForm(
                                obj, "/gui/SellerForm.fxml",Utils.currentStage(event)));
            }
        });
    }

    private void initRemoveButtons() {
        tableColumnREMOVE.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
        tableColumnREMOVE.setCellFactory(param -> new TableCell<Seller, Seller>() {
            private final Button button = new Button("remove");
            @Override
            protected void updateItem(Seller obj, boolean empty) {
                super.updateItem(obj, empty);
                if (obj == null) {
                    setGraphic(null);
                    return;
                }
                setGraphic(button);
                button.setOnAction(event -> removeEntity(obj));
            }
        });
    }

    private void removeEntity(Seller obj) {
        Optional<ButtonType> result = Alerts.showConfirmation("Confirmation", "are you sure to delete?");
        if(result.get() == ButtonType.OK){
            if(service == null){
                throw new IllegalStateException("Service was null");
            }
            try{
                service.remove(obj);
                updateTableView();
            }catch (DbIntegrityException e){
                Alerts.showAlert("Error Removing Object", null, e.getMessage(), Alert.AlertType.ERROR);
            }catch (DbException e){
                Alerts.showAlert("Error Removing Object", null, e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    @Override
    public void onDataChange() {
        updateTableView();
    }
}
