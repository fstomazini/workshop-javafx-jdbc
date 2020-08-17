package model;

import model.entities.Department;
import model.dao.DaoFactory;
import model.dao.DepartmentDao;

import java.util.ArrayList;
import java.util.List;

public class DepartmentService {

    private DepartmentDao dao = DaoFactory.creatDepartmentDao();

    public List<Department> findAll() {
        return dao.findAll();
    }

    public void saveOrUpdate(Department obj) {
        if(obj.getId() == null){
            dao.insert(obj);
        }else{
             dao.update(obj);
        }
    }
}