package uni.fmi.bachelors.todoproject;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class AddToDoActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener {

    Button addButton, addCategoryButton, setReminderButton;
    EditText titleEditText, descriptionEditText;

    DataBaseHelper dataBaseHelper;
    String date;
    static Spinner categorySpinner;
    String selectedCategory;

    TextView testText;

    static Context context;

    private Calendar c;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_to_do);

        addButton = findViewById(R.id.addButton);
        titleEditText = findViewById(R.id.titleEditText);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        addCategoryButton = findViewById(R.id.addCategory);
        categorySpinner = findViewById(R.id.categorySpinner);
        setReminderButton = findViewById(R.id.setReminderButton);
        testText = findViewById(R.id.testText);

        AddCategoriesToSpinner();

        dataBaseHelper = new DataBaseHelper(AddToDoActivity.this);

        Intent incomingIntent = getIntent();
        date = incomingIntent.getStringExtra("date");

        addButton.setOnClickListener(onClick);
        addCategoryButton.setOnClickListener(onClick);
        setReminderButton.setOnClickListener(onClick);

        context = getApplicationContext();
    }

    public void AddCategoriesToSpinner() {
        dataBaseHelper = new DataBaseHelper(this);
        ArrayList<CategoryModel> categories = dataBaseHelper.getAllCategories();

        ArrayAdapter<CategoryModel> arrayAdapter = new ArrayAdapter<CategoryModel>(this, android.R.layout.simple_spinner_item, categories);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(arrayAdapter);

        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCategory = parent.getItemAtPosition(position).toString();
                Toast.makeText(parent.getContext(), "Selected: " + selectedCategory, Toast.LENGTH_LONG).show();
            }
            @Override
            public void onNothingSelected(AdapterView <?> parent) {
            }
        });

    }

    View.OnClickListener onClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if(v.getId() == R.id.addButton){

                dataBaseHelper = new DataBaseHelper(AddToDoActivity.this);
            Intent intent = new Intent(AddToDoActivity.this, MainActivity.class);
            intent.putExtra("title", titleEditText.getText().toString());
            intent.putExtra("description", descriptionEditText.getText().toString());

            int categoryId = dataBaseHelper.getCategoryId(selectedCategory);

            ToDoModel toDoModel = new ToDoModel(-1,categoryId,titleEditText.getText().toString(),descriptionEditText.getText().toString(),date,false);
            dataBaseHelper.addToDo(toDoModel);

            if(c!=null){
            int idToDo = dataBaseHelper.getLastToDoId();
            ReminderModel reminderModel = new ReminderModel(-1, c.getTime(), idToDo);
            dataBaseHelper.addReminder(reminderModel);}

            startActivity(intent);
            }

            if(v.getId() == R.id.addCategory){
                openDialog();
            }

            if(v.getId() == R.id.setReminderButton){
                DialogFragment timePicker = new TimePickerFragment();
                timePicker.show(getSupportFragmentManager(),"time picker");
            }
        }
    };

    public void openDialog(){
        DialogPopUps dialogCategory = new DialogPopUps("Category");
        dialogCategory.show(getSupportFragmentManager(),null);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, hourOfDay);
        c.set(Calendar.MINUTE, minute);
        c.set(Calendar.SECOND, 0);

        updateText(c);
        //startAlarm(c);

        MainActivity mainActivity = (MainActivity) MainActivity.context;
        mainActivity.startAlarms();
    }

    private void updateText(Calendar c) {
        String timeText = "Alarm set for: ";
        timeText+= DateFormat.getTimeInstance(DateFormat.SHORT).format(c.getTime());

        testText.setText(timeText);
    }

    private void startAlarm(Calendar c){
        String title = titleEditText.getText().toString();
        String desc = descriptionEditText.getText().toString();
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlertReceiver.class);
        intent.putExtra("Title", title);
        intent.putExtra("Description", desc);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this,1,intent,0);

        if(c.before(Calendar.getInstance())){
            c.add(Calendar.DATE,1);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        AddToDoActivity.activityResumed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        AddToDoActivity.activityPaused();
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