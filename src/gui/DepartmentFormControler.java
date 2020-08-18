package gui;

import com.sun.javafx.property.adapter.PropertyDescriptor;
import db.DbException;
import gui.listners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.DepartmentService;
import model.entities.Department;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class DepartmentFormControler implements Initializable {

    private Department entity;

    private DepartmentService service;

    private List<DataChangeListener> dataChangeListeners= new ArrayList<>();


    @FXML
    private TextField txtId;
    @FXML
    private TextField txtName;
    @FXML
    private Label labelErroName;
    @FXML
    private Button btSave;
    @FXML
    private Button btCancel;

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
        }
        }

    private void notifyDataChangeListeners() {
        dataChangeListeners.forEach(DataChangeListener::onDataChange);
            
        }


    @FXML
    public void onBtCancelAction(ActionEvent event){
        Utils.currentStage(event).close();
    }

    private Department getFormDate() {
        Department obj = new Department();
        obj.setId(Utils.tryParseToInt(txtId.getText()));
        obj.setName(txtName.getText());
        return obj;
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeNodes();
    }

    public void setEntity(Department entity) {
        this.entity = entity;
    }

    public void setService(DepartmentService service) {
        this.service = service;
    }

    private void initializeNodes(){
        Constraints.setTextFieldInteger(txtId);
        Constraints.setTextFieldMaxLength(txtName,30);
    }
     public void updateFormData(){
        if( entity == null){
            throw new IllegalStateException("Entity was null");
        }
        txtId.setText(String.valueOf(entity.getId()));
        txtName.setText(entity.getName());
     }

}
