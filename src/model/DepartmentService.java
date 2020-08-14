package model;

import entities.Department;

import java.util.ArrayList;
import java.util.List;

public class DepartmentService {

     public List<Department> findAll(){
         List<Department> list = new ArrayList<>();
         list.add(new Department(1, "Livros"));
         list.add(new Department(2, "Culinaria"));
         list.add(new Department(3, "Banho"));

         return list;
     }
}
