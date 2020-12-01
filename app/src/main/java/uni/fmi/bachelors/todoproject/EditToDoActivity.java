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

public class EditToDoActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener{

    Button editButton, addCategoryButton, setReminderButton;
    EditText titleEditText, descriptionEditText;
    static Spinner categorySpinner;
    TextView alarmTextView;

    DataBaseHelper dataBaseHelper;
    String selectedCategory;
    static Context context;

    int id, idCategory;
    String title,description,date;

    Calendar c;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_to_do);

        editButton = findViewById(R.id.editButton);
        titleEditText = findViewById(R.id.titleEditTextEdit);
        descriptionEditText = findViewById(R.id.descriptionEditTextEdit);
        addCategoryButton = findViewById(R.id.addCategoryEdit);
        categorySpinner = findViewById(R.id.categorySpinnerEdit);
        setReminderButton = findViewById(R.id.setReminderButtonEdit);
        alarmTextView = findViewById(R.id.alarmTextView);

        context = EditToDoActivity.this;

        Intent incomingIntent = getIntent();
        id = incomingIntent.getIntExtra("id",0);
        idCategory = incomingIntent.getIntExtra("idCategory",0);
        title = incomingIntent.getStringExtra("title");
        description = incomingIntent.getStringExtra("description");
        date = incomingIntent.getStringExtra("date");

        titleEditText.setText(title);
        descriptionEditText.setText(description);

        dataBaseHelper = new DataBaseHelper(EditToDoActivity.this);

        AddCategoriesToSpinner(idCategory);

        editButton.setOnClickListener(onClick);
        addCategoryButton.setOnClickListener(onClick);
        setReminderButton.setOnClickListener(onClick);

    }

    View.OnClickListener onClick = new View.OnClickListener() {
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onClick(View v) {
            if(v.getId() == R.id.editButton) {
                dataBaseHelper = new DataBaseHelper(EditToDoActivity.this);
                String title = titleEditText.getText().toString();
                String description = descriptionEditText.getText().toString();
                int idCategory = dataBaseHelper.getCategoryId(selectedCategory);

                ToDoModel toDoModel = new ToDoModel(id, title, description, idCategory,date);
                dataBaseHelper.updateToDo(toDoModel);

                if(c != null){
                    dataBaseHelper = new DataBaseHelper(EditToDoActivity.this);
                    ReminderModel reminderModel = new ReminderModel(c.getTime());
                    dataBaseHelper.updateReminder(reminderModel,id);
                    MainActivity mainActivity = (MainActivity) MainActivity.context;
                    mainActivity.startAlarms();
                }

                Intent intent = new Intent(EditToDoActivity.this, MainActivity.class);
                startActivity(intent);
            } else if(v.getId() == R.id.addCategoryEdit) {
                DialogPopUps dialogCategory = new DialogPopUps("Category");
                dialogCategory.show(getSupportFragmentManager(),null);
            }else if(v.getId() == R.id.setReminderButtonEdit){
                DialogFragment timePicker = new TimePickerFragment();
                timePicker.show(getSupportFragmentManager(),"time picker");
            }
        }
    };

    public void AddCategoriesToSpinner(int idCategory) {
        dataBaseHelper = new DataBaseHelper(this);
        ArrayList<CategoryModel> categories = dataBaseHelper.getAllCategories();

        ArrayAdapter<CategoryModel> arrayAdapter = new ArrayAdapter<CategoryModel>(this, android.R.layout.simple_spinner_item, categories);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        categorySpinner.setAdapter(arrayAdapter);
        categorySpinner.setSelection(idCategory-1);

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

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, hourOfDay);
        c.set(Calendar.MINUTE, minute);
        c.set(Calendar.SECOND, 0);

        updateText(c);


    }

    private void updateText(Calendar c) {
        String timeText = "Alarm set for: ";
        timeText+= DateFormat.getTimeInstance(DateFormat.SHORT).format(c.getTime());

        alarmTextView.setText(timeText);
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
}