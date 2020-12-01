package uni.fmi.bachelors.todoproject;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.O)
public class MainActivity extends AppCompatActivity {

    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    DateTimeFormatter dtf1 = DateTimeFormatter.ofPattern("d/MM/yyyy");
    LocalDateTime now = LocalDateTime.now();
    String todayDate = dtf.format(now);
    String todayDateForAllToDos = dtf1.format(now);

    DataBaseHelper dataBaseHelper;

    Button calendarButton, categoriesButton;
    RecyclerView recyclerView;

    ArrayList<String> titles = new ArrayList<>();
    ArrayList<String> descriptions = new ArrayList<>();
    ArrayList<Boolean> checks = new ArrayList<>();

    ArrayList<ReminderModel> reminderModels = new ArrayList<>();
    Object[] objects = new Object[2];

    static Context context;
    public static List<ToDoModel> toDos;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        calendarButton = findViewById(R.id.calendarButton);
        recyclerView = findViewById(R.id.toDoRecyclerView);
        categoriesButton = findViewById(R.id.categoriesButton);

        calendarButton.setOnClickListener(onClick);

        context = MainActivity.this;

        dataBaseHelper = new DataBaseHelper(MainActivity.this);

        startAlarms();

        try {
            initTitlesAndDescriptionsForDay();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        categoriesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CategoryActivity.class);
                startActivity(intent);
            }
        });

    }

    View.OnClickListener onClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(MainActivity.this, CalendarActivity.class);
            startActivity(intent);
        }
    };

    private void initTitlesAndDescriptions() throws ParseException {

        List<ToDoModel> toDos = dataBaseHelper.getAllToDo();

        for (int i = 0; i<toDos.size();i++){
            titles.add(toDos.get(i).getTitle());
            descriptions.add(toDos.get(i).getDescription());
        }

        initRecyclerView();
    }

    private void initTitlesAndDescriptionsForDay() throws ParseException {

        toDos = dataBaseHelper.getAllToDoByDay(todayDateForAllToDos);

        for (int i = 0; i<toDos.size();i++){
            titles.add(toDos.get(i).getTitle());
            descriptions.add(toDos.get(i).getDescription());
            checks.add(toDos.get(i).isChecked());
        }

        initRecyclerView();
    }

    private void initRecyclerView(){
        RecyclerView recyclerView = findViewById(R.id.toDoRecyclerView);
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(this, titles, descriptions,checks,this.getSupportFragmentManager());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    public void startAlarms(){
        try {
            objects = dataBaseHelper.getAllReminders(todayDate);
            startAlarm(objects[0],objects[1]);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void startAlarm(Object reminderModels1, Object toDoModels1) throws ParseException {

        ArrayList<ReminderModel> reminderModels = (ArrayList<ReminderModel>) reminderModels1;
        ArrayList<ToDoModel> toDoModels = (ArrayList<ToDoModel>) toDoModels1;

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

        Date nowDate = new Date();

        AlarmManager[] alarmManagers = new AlarmManager[reminderModels.size()];
        List<PendingIntent> intentArray = new ArrayList<>();
        Intent[] intents = new Intent[reminderModels.size()];

        for(int i = 0; i<reminderModels.size();i++) {

            if (reminderModels.get(i).getDate().before(nowDate)) {
                continue;
            }
            int mHour = 0, mMin = 0;
            String mAlarmTime = simpleDateFormat.format(reminderModels.get(i).getDate());

            String[] timeFromDate = mAlarmTime.split(" ");
            String[] time = timeFromDate[1].split(":");
            mHour = Integer.parseInt(time[0].trim());
            mMin = Integer.parseInt(time[1].trim());

            Calendar calendar = Calendar.getInstance();
            calendar.set(calendar.HOUR_OF_DAY, mHour);
            calendar.set(calendar.MINUTE, mMin);
            calendar.set(calendar.SECOND, 0);
            calendar.set(calendar.MILLISECOND, 0);

            intents[i] = new Intent(MainActivity.this, AlertReceiver.class);
            intents[i].putExtra("Title", toDoModels.get(i).getTitle());
            intents[i].putExtra("Description", toDoModels.get(i).getDescription());
            intents[i].putExtra("Index", toDoModels.get(i).getId());

            PendingIntent pi = PendingIntent.getBroadcast(MainActivity.this, i, intents[i],0);

            alarmManagers[i] = (AlarmManager) getSystemService(ALARM_SERVICE);
            alarmManagers[i].set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),pi);

            intentArray.add(pi);

        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        MainActivity.activityResumed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MainActivity.activityPaused();
    }

    private static boolean activityVisible;

    public static boolean isActivityVisible() {
        return activityVisible;
    }

    public static void activityResumed() {
        activityVisible = true;
    }

    public static void activityPaused() {
        activityVisible = false;
    }
}