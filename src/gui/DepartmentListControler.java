package gui;

import gui.util.Alerts;
import gui.util.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import model.entities.Department;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.DepartmentService;
import sample.Main;


import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class DepartmentListControler implements Initializable {

    private DepartmentService service;

    @FXML
    private TableView<Department> tableViewDepartments;

    @FXML
    private TableColumn<Department, Integer> tableColumnID;

    @FXML
    private TableColumn<Department, String> tableColumnName;

    @FXML
    private Button btNew;

    private ObservableList<Department> obsList;


    @FXML
    public void onBtNewAction(ActionEvent event){
        Stage parentStage = Utils.currentStage(event);
        createDialogForm("/gui/DepartmentForm.fxml", parentStage);
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

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeNodes();

        Stage stage = (Stage) Main.getMainScene().getWindow();
        tableViewDepartments.prefHeightProperty().bind(stage.heightProperty());

    }

    private void createDialogForm(String absoluteName, Stage parentStage){
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
            Pane pane = loader.load();

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


}
