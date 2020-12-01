package uni.fmi.bachelors.todoproject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DataBaseHelper extends SQLiteOpenHelper {

    public static final String TODO_TABLE = "TODO_TABLE";
    public static final String REMINDER_TABLE = "REMINDER_TABLE";
    public static final String CATEGORY_TABLE = "CATEGORY_TABLE";

    DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

    public DataBaseHelper(@Nullable Context context) {
        super(context, "toDo1.db", null, 2);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createCategoryTableStatement = "CREATE TABLE "+ CATEGORY_TABLE +" (ID INTEGER PRIMARY KEY AUTOINCREMENT, NAME TEXT)";
        String createReminderTableStatement = "CREATE TABLE "+REMINDER_TABLE+" (ID INTEGER PRIMARY KEY AUTOINCREMENT, DATE DATE, ID_TODO INTEGER, FOREIGN KEY(ID_TODO) REFERENCES TODO_TABLE(ID))";
        String createToDoTableStatement = "CREATE TABLE "+TODO_TABLE+" (ID INTEGER PRIMARY KEY AUTOINCREMENT, TITLE TEXT, DESCRIPTION TEXT, DATE DATE, IS_CHECKED BOOL,ID_CATEGORY INTEGER, FOREIGN KEY(ID_CATEGORY) REFERENCES CATEGORY_TABLE(ID))";

        db.execSQL(createCategoryTableStatement);
        db.execSQL(createToDoTableStatement);
        db.execSQL(createReminderTableStatement);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public void addCategory(CategoryModel categoryModel){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put("NAME", categoryModel.getName());

        db.insert(CATEGORY_TABLE, null, cv);
    }


    public void addReminder(ReminderModel reminderModel){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put("DATE", dateFormat.format(reminderModel.getDate()));
        cv.put("ID_TODO", reminderModel.getIdToDo());

        db.insert(REMINDER_TABLE, null, cv);
    }

    public void addReminder(Date date, int id){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put("DATE", dateFormat.format(date));
        cv.put("ID_TODO", id);

        db.insert(REMINDER_TABLE, null, cv);
    }

    public int getLastToDoId(){
        String queryString = "SELECT * FROM " + TODO_TABLE +" ORDER BY ID DESC LIMIT 1";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(queryString,null);
        int id = -1;
        if(cursor.moveToFirst()){
            id = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return id;
    }

    public void addToDo(ToDoModel toDoModel){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put("TITLE", toDoModel.getTitle());
        cv.put("DESCRIPTION", toDoModel.getDescription());
        cv.put("IS_CHECKED", toDoModel.isChecked());
        cv.put("ID_CATEGORY",toDoModel.getIdCategory());
        cv.put("DATE", toDoModel.getDate());

        db.insert(TODO_TABLE, null, cv);

    }

    public int getCategoryId(String categoryWanted){

        String queryString = "SELECT ID FROM "+ CATEGORY_TABLE+" WHERE name = \""+categoryWanted+"\"";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(queryString,null);
        int id = -1;
        if(cursor.moveToFirst()){
                id = cursor.getInt(0);
        }

        cursor.close();
        db.close();
        return id;
    }

    public String getCategoryName(int id){

        String queryString = "SELECT NAME FROM "+ CATEGORY_TABLE+" WHERE ID = "+id;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(queryString,null);
        String name = null;

        if(cursor.moveToFirst()){
            name = cursor.getString(0);
        }

        cursor.close();
        db.close();
        return name;
    }


    public List<ToDoModel> getAllToDo() throws ParseException {
        List<ToDoModel> returnList = new ArrayList<>();

        String queryString = "SELECT * FROM "+ TODO_TABLE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(queryString,null);

        if(cursor.moveToFirst()){
            do {
                int toDoId = cursor.getInt(0);
                String title = cursor.getString(1);
                String desc = cursor.getString(2);
                String date = cursor.getString(3);
                boolean isChecked = cursor.getInt(4) == 1 ? true : false;
                int idCategory = cursor.getInt(5);

                ToDoModel toDoModel = new ToDoModel(toDoId,idCategory,title,desc,date,isChecked);
                returnList.add(toDoModel);

            } while(cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return returnList;
    }

    public List<ToDoModel> getAllToDoByDay(String dateWanted) throws ParseException {
        List<ToDoModel> returnList = new ArrayList<>();

        String queryString = "SELECT * FROM "+ TODO_TABLE + " WHERE date = '" + dateWanted +"'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(queryString,null);

        if(cursor.moveToFirst()){
            do {
                int toDoId = cursor.getInt(0);
                String title = cursor.getString(1);
                String desc = cursor.getString(2);
                String date = cursor.getString(3);
                boolean isChecked = cursor.getInt(4) == 1 ? true : false;
                int idCategory = cursor.getInt(5);

                ToDoModel toDoModel = new ToDoModel(toDoId,idCategory,title,desc,date,isChecked);
                returnList.add(toDoModel);

            } while(cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return returnList;
    }

    public void updateIsCheckedOnToDo(ToDoModel toDoModel){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put("IS_CHECKED", toDoModel.isChecked());

        db.update(TODO_TABLE, cv,"ID = "+ toDoModel.getId(),null);
    }

    public ArrayList<CategoryModel> getAllCategories(){
        ArrayList<CategoryModel> returnList = new ArrayList<>();

        String queryString = "SELECT * FROM "+ CATEGORY_TABLE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(queryString,null);

        if(cursor.moveToFirst()){
            do {
                int id = cursor.getInt(0);
                String category = cursor.getString(1);

                CategoryModel model = new CategoryModel(id,category);
                returnList.add(model);

            } while(cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return returnList;
    }

    public Object[] getAllReminders(String todayDate) throws ParseException {
        ArrayList<ReminderModel> reminderModels = new ArrayList<>();
        ArrayList<ToDoModel> toDoModels = new ArrayList<>();

        String queryString = "SELECT TODO_TABLE.TITLE, TODO_TABLE.DESCRIPTION,REMINDER_TABLE.DATE,TODO_TABLE.ID FROM REMINDER_TABLE " +
                "INNER JOIN TODO_TABLE " +
                "on REMINDER_TABLE.ID_TODO = TODO_TABLE.ID WHERE REMINDER_TABLE.DATE" +
                " BETWEEN '"+ todayDate+" 00:00:00' AND "+
                "'"+ todayDate+" 23:59:59'";

        String queryString1 = "SELECT * FROM "+ REMINDER_TABLE+" WHERE DATE" +
                " BETWEEN '"+ todayDate+" 00:00:00' AND "+
                "'"+ todayDate+" 23:59:59'";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(queryString,null);

        if(cursor.moveToFirst()){
            do {
                String title = cursor.getString(0);
                String desc = cursor.getString(1);
                Date date = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(cursor.getString(2));
                int id = cursor.getInt(3);

                ReminderModel model = new ReminderModel(date);
                reminderModels.add(model);

                ToDoModel toDoModel = new ToDoModel(title,desc,id);
                toDoModels.add(toDoModel);

            } while(cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return new Object[]{reminderModels, toDoModels};
    }

    public void deleteToDo(int id){
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TODO_TABLE, "ID = "+id,null);
    }

    public ToDoModel getToDoById(int idWanted){

        String queryString = "SELECT * FROM "+ TODO_TABLE+" WHERE ID = "+idWanted;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(queryString,null);
        ToDoModel toDoModel= null;
        if(cursor.moveToFirst()){
            int toDoId = cursor.getInt(0);
            String title = cursor.getString(1);
            String desc = cursor.getString(2);
            String date = cursor.getString(3);
            boolean isChecked = cursor.getInt(4) == 1 ? true : false;
            int idCategory = cursor.getInt(5);

            toDoModel = new ToDoModel(toDoId,idCategory,title,desc,date,isChecked);
        }

        cursor.close();
        db.close();

        return toDoModel;
    }

    public void updateToDo(ToDoModel toDoModel){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put("TITLE", toDoModel.getTitle());
        cv.put("DESCRIPTION", toDoModel.getDescription());
        cv.put("ID_CATEGORY",toDoModel.getIdCategory());
        cv.put("DATE", toDoModel.getDate());

        db.update(TODO_TABLE, cv,"ID = "+ toDoModel.getId(),null);
    }

    public void deleteCategory(int id){
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(CATEGORY_TABLE, "ID = "+id,null);
    }

    public void updateCategory(String name, int id){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put("NAME", name);

        db.update(CATEGORY_TABLE, cv,"ID = "+ id,null);
    }

    public void updateReminder(ReminderModel reminderModel, int idToDo){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put("DATE", dateFormat.format(reminderModel.getDate()));

        db.update(REMINDER_TABLE, cv,"ID_TODO = "+ idToDo,null);
    }

    public ArrayList<ToDoModel> getAllToDoByCategory(int idCategory){
        ArrayList<ToDoModel> returnList = new ArrayList<>();

        String queryString = "SELECT * FROM TODO_TABLE " +
                "INNER JOIN CATEGORY_TABLE " +
                "ON TODO_TABLE.ID_CATEGORY = CATEGORY_TABLE.ID " +
                "WHERE CATEGORY_TABLe.ID = "+idCategory;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(queryString,null);

        if(cursor.moveToFirst()){
            do {
                int toDoId = cursor.getInt(0);
                String title = cursor.getString(1);
                String desc = cursor.getString(2);
                String date = cursor.getString(3);
                boolean isChecked = cursor.getInt(4) == 1 ? true : false;

                ToDoModel toDoModel = new ToDoModel(toDoId,title,desc,date,isChecked);
                returnList.add(toDoModel);

            } while(cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return returnList;
    }
}
