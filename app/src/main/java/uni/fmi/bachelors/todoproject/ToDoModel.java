package uni.fmi.bachelors.todoproject;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class ToDoModel {
    private int id, idCategory;
    private boolean isChecked;
    private String title, description;
    private String date;

    SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");

    public ToDoModel(int id, int idCategory,String title, String description, String date, boolean isChecked) {
        this.id = id;
        this.idCategory = idCategory;
        this.isChecked = isChecked;
        this.title = title;
        this.description = description;
        this.date = date;
    }

    public ToDoModel(String title, String description, int id) {
        this.title = title;
        this.description = description;
        this.id = id;
    }

    public ToDoModel(int id, String title, String description, int idCategory,String date) {
        this.id = id;
        this.idCategory = idCategory;
        this.title = title;
        this.description = description;
        this.date = date;
    }

    public ToDoModel(int id,String title, String description, String date, boolean isChecked) {
        this.id = id;
        this.isChecked = isChecked;
        this.title = title;
        this.description = description;
        this.date = date;
    }

    public int getIdCategory() {
        return idCategory;
    }

    public void setIdCategory(int idCategory) {
        this.idCategory = idCategory;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
