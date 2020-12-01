package uni.fmi.bachelors.todoproject;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.CalendarView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.O)
public class CalendarActivity extends AppCompatActivity {

    CalendarView calendarView;
    DataBaseHelper dataBaseHelper;

    public static Context context;

    String date;
    Date todayDate;

    public static List<ToDoModel> toDos;
    ArrayList<String> titles = new ArrayList<>();
    ArrayList<String> descriptions = new ArrayList<>();
    ArrayList<Boolean> checks = new ArrayList<>();

    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
    String dateString = simpleDateFormat.format(new Date());

    public DialogPopUps dialogToDo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        context = CalendarActivity.this;

        toDos = new ArrayList<>();
        calendarView = findViewById(R.id.calendarView);
        dataBaseHelper = new DataBaseHelper(CalendarActivity.this);

        try {
            todayDate = simpleDateFormat.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                date = dayOfMonth + "/" + (month+1) +"/"+year;
                Date selectedDate = null;
                try {
                    selectedDate = simpleDateFormat.parse(date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                if(todayDate.after(selectedDate)){
                    //Toast.makeText(CalendarActivity.this, "You should show the Activities", Toast.LENGTH_SHORT).show();

                    dialogToDo = new DialogPopUps("toDoPopup",getSupportFragmentManager(), date);
                    dialogToDo.show(getSupportFragmentManager(),null);
                    /*try {
                        initTitlesAndDescriptionsForDay();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }*/

                    try {
                        dialogToDo.initTitlesAndDescriptionsForDay(date);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                }
                else{
                    //Toast.makeText(CalendarActivity.this, "You should show the AddToDoActivity", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(CalendarActivity.this, AddToDoActivity.class);
                    intent.putExtra("date", date);
                    startActivity(intent);
                }

                /*Toast.makeText(CalendarActivity.this, date, Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(CalendarActivity.this, AddToDoActivity.class);
                intent.putExtra("date", date);
                startActivity(intent);*/
            }
        });
    }

    private void initTitlesAndDescriptionsForDay() throws ParseException {

        toDos = dataBaseHelper.getAllToDoByDay(date);

        for (int i = 0; i<toDos.size();i++){
            titles.add(toDos.get(i).getTitle());
            descriptions.add(toDos.get(i).getDescription());
            checks.add(toDos.get(i).isChecked());
        }

        //initRecyclerView();
    }

    private void initRecyclerView(){
        RecyclerView recyclerView = findViewById(R.id.toDoRecyclerViewPopup);
        RecyclerViewAdapterPopUp adapter = new RecyclerViewAdapterPopUp(this, titles, descriptions,checks,this.getSupportFragmentManager());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        DialogPopUps dialogPopUps = new DialogPopUps("toDoPopup");
        dialogPopUps.show(getSupportFragmentManager(),null);

    }

    private FragmentManager getFragment(){
        return this.getSupportFragmentManager();
    }
}