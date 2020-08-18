package gui;

import db.DbException;
import db.DbIntegrityException;
import gui.listners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Utils;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import model.entities.Department;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.DepartmentService;
import sample.Main;


import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class DepartmentListControler implements Initializable, DataChangeListener {

    private DepartmentService service;

    @FXML
    private TableView<Department> tableViewDepartments;

    @FXML
    private TableColumn<Department, Integer> tableColumnID;

    @FXML
    private TableColumn<Department, String> tableColumnName;

    @FXML
    private TableColumn<Department, Department> tableColumnEDIT;

    @FXML
    private TableColumn<Department, Department> tableColumnREMOVE;

    @FXML
    private Button btNew;

    private ObservableList<Department> obsList;


    @FXML
    public void onBtNewAction(ActionEvent event){
        Stage parentStage = Utils.currentStage(event);
        Department obj = new Department();
        createDialogForm(obj, "/gui/DepartmentForm.fxml", parentStage);
    }

    private void initializeNodes(){
        tableColumnID.setCellValueFactory(new PropertyValueFactory<>("id"));
        tableColumnName.setCellValueFactory(new PropertyValueFactory<>("name"));
    }


    public void setDepartmentService(DepartmentService service) {
        this.service = service;
    }

    public void updateTableView(){
        if(service == null){
            throw new IllegalStateException("Service was null!");
        }
        List<Department> list = service.findAll();
        obsList = FXCollections.observableList(list);
        tableViewDepartments.setItems(obsList);
        initEditButtons();
        initRemoveButtons();

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeNodes();

        Stage stage = (Stage) Main.getMainScene().getWindow();
        tableViewDepartments.prefHeightProperty().bind(stage.heightProperty());

    }

    private void createDialogForm(Department obj,String absoluteName, Stage parentStage){
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
            Pane pane = loader.load();

            DepartmentFormControler controller = loader.getController();
            controller.setEntity(obj);
            controller.setService(new DepartmentService());
            controller.subscribeDataChangeListener(this);
            controller.updateFormData();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Enter Department Data");
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
        tableColumnEDIT.setCellFactory(param -> new TableCell<Department, Department>() {
            private final Button button = new Button("edit");
            @Override
            protected void updateItem(Department obj, boolean empty) {
                super.updateItem(obj, empty);
                if (obj == null) {
                    setGraphic(null);
                    return;
                }
                setGraphic(button);
                button.setOnAction(
                        event -> createDialogForm(
                                obj, "/gui/DepartmentForm.fxml",Utils.currentStage(event)));
            }
        });
    }

    private void initRemoveButtons() {
        tableColumnREMOVE.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
        tableColumnREMOVE.setCellFactory(param -> new TableCell<Department, Department>() {
            private final Button button = new Button("remove");
            @Override
            protected void updateItem(Department obj, boolean empty) {
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

    private void removeEntity(Department obj) {
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
