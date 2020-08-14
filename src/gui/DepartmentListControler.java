package gui;

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
    public void onBtNewAction(){
        System.out.println("onBtNewAction");
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


}
