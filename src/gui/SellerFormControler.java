package gui;

import db.DbException;
import gui.listners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.util.Callback;
import model.DepartmentService;
import model.SellerService;
import model.entities.Department;
import model.entities.Seller;
import model.exceptions.ValidationException;

import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

public class SellerFormControler implements Initializable {

    private Seller entity;

    private SellerService service;

    private DepartmentService departamentService;

    private List<DataChangeListener> dataChangeListeners= new ArrayList<>();


    @FXML
    private TextField txtId;
    @FXML
    private TextField txtName;
    @FXML
    private TextField txtEmail;
    @FXML
    private DatePicker dpBirthDate;
    @FXML
    private TextField txtBaseSalary;
    @FXML
    private ComboBox<Department> comboBoxDepartment;
    @FXML
    private Label labelErroName;
    @FXML
    private Label labelErroEmail;
    @FXML
    private Label labelErroBirthDate;
    @FXML
    private Label labelErroBaseSalary;
    @FXML
    private Button btSave;
    @FXML
    private Button btCancel;

    private ObservableList<Department> observableList;



    public void subscribeDataChangeListener(DataChangeListener listener){
        dataChangeListeners.add(listener);
    }

    @FXML
    public void onBtSaveAction(ActionEvent event){
        if(entity == null){
            throw new IllegalStateException("Entity was null");
        }
        if(service == null){
            throw new  IllegalStateException("Service was null");
        }
        try {
            entity = getFormDate();
            service.saveOrUpdate(entity);
            notifyDataChangeListeners();
            Utils.currentStage(event).close();
        }catch(DbException e){
            Alerts.showAlert("Error Saving Objet", null, entity.getName(), Alert.AlertType.ERROR);
        }catch (ValidationException e){
            setErrorMessages(e.getErrors());
        }
        }

    private void notifyDataChangeListeners() {
        dataChangeListeners.forEach(DataChangeListener::onDataChange);
            
        }


    @FXML
    public void onBtCancelAction(ActionEvent event){
        Utils.currentStage(event).close();
    }

    private Seller getFormDate() {
        Seller obj = new Seller();

        ValidationException exception = new ValidationException("Validation error");

        obj.setId(Utils.tryParseToInt(txtId.getText()));
        if(txtName.getText() == null || txtName.getText().trim().equals("")){
            exception.addError("name", "Field can't be empty");
        }
        obj.setName(txtName.getText());
        if (exception.getErrors().size() > 0){
            throw exception;

        }
        return obj;
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeNodes();
    }

    public void setEntity(Seller entity) {
        this.entity = entity;
    }

    public void setService(SellerService service, DepartmentService departamentService) {

        this.service = service;
        this.departamentService=departamentService;
    }

    private void initializeNodes(){
        Constraints.setTextFieldInteger(txtId);
        Constraints.setTextFieldMaxLength(txtName,70);
        Constraints.setTextFieldDouble(txtBaseSalary);
        Constraints.setTextFieldMaxLength(txtEmail,60);
        Utils.formatDatePicker(dpBirthDate, "dd/MM/yyyy");
    }
     public void updateFormData(){
        if( entity == null){
            throw new IllegalStateException("Entity was null");
        }
        txtId.setText(String.valueOf(entity.getId()));
        txtName.setText(entity.getName());
        txtEmail.setText(entity.getEmail());
        Locale.setDefault(Locale.US);
        txtBaseSalary.setText(String.format("%.2f", entity.getBaseSalary()));
        if(entity.getBirthDate() != null) {
            dpBirthDate.setValue(LocalDate.ofInstant(entity.getBirthDate().toInstant(), ZoneId.systemDefault()));
        }
        if(entity.getDepartment() == null){
            comboBoxDepartment.getSelectionModel().selectFirst();
        }else {
            comboBoxDepartment.setValue(entity.getDepartment());
        }
    }

    public void loadAssociatedObjects(){
        List<Department> list = departamentService.findAll();
        observableList = FXCollections.observableArrayList(list);
        comboBoxDepartment.setItems(observableList);
    }

     private void setErrorMessages(Map<String, String> errors){
         Set<String> fields = errors.keySet();
         if(fields.contains("name")){
             labelErroName.setText(errors.get("name"));
         }
     }

    private void initializeComboBoxDepartment() {
        Callback<ListView<Department>, ListCell<Department>> factory = lv -> new ListCell<Department>() {
            @Override
            protected void updateItem(Department item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? "" : item.getName());
            }
        };
        comboBoxDepartment.setCellFactory(factory);
        comboBoxDepartment.setButtonCell(factory.call(null));
    }

}
