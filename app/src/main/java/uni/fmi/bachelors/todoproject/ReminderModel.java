package uni.fmi.bachelors.todoproject;

import java.util.Date;

public class ReminderModel {
    private int id, idToDo;
    private Date date;

    public ReminderModel(int id, Date date, int idToDo) {
        this.id = id;
        this.date = date;
        this.idToDo = idToDo;
    }

    public ReminderModel(Date date) {
        this.date = date;
    }

    public int getIdToDo() {
        return idToDo;
    }

    public void setIdToDo(int idToDo) {
        this.idToDo = idToDo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
