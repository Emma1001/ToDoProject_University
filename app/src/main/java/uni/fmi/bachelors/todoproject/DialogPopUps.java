package uni.fmi.bachelors.todoproject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DialogPopUps extends AppCompatDialogFragment {

    private  DataBaseHelper dataBaseHelper;
    private String type;
    EditText addCategoryEditText, editCategoryNameEditText;
    int id;
    int idCategory;
    Context context;

    //DataBaseHelper dataBaseHelper;
    TextView titleTextView, descriptionTextView, dateTextView, isDoneTextView;
    FragmentManager fm;

    String date;

    public static List<ToDoModel> toDos;
     ArrayList<String> titles = new ArrayList<>();
     ArrayList<String> descriptions = new ArrayList<>();
     ArrayList<Boolean> checks = new ArrayList<>();


    public DialogPopUps(String type){
        this.type = type;
        this.context = getContext();
    }

    public DialogPopUps(String type, FragmentManager fm){
        this.type = type;
        this.fm = fm;
        this.context = getContext();
    }

    public DialogPopUps(String type, FragmentManager fm,String date){
        this.type = type;
        this.fm = fm;
        this.date = date;
        this.context = getContext();
    }

    public DialogPopUps(String type, int idCategory){
        this.type = type;
        this.idCategory = idCategory;
        this.context = getContext();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final LayoutInflater inflater = getActivity().getLayoutInflater();
        
        if(type == "Category") {
            View view = inflater.inflate(R.layout.add_category_popup, null);

            builder.setView(view)
                    .setTitle("Add new category")
                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            String categoryText = addCategoryEditText.getText().toString();
                            if(CategoryActivity.isActivityVisible()){
                                dataBaseHelper = new DataBaseHelper(CategoryActivity.context);
                                dataBaseHelper.addCategory(new CategoryModel(categoryText));
                                CategoryActivity categoryActivity = (CategoryActivity) CategoryActivity.context;
                                categoryActivity.initializeRecyclerView();
                            }else if(AddToDoActivity.isActivityVisible()){
                                dataBaseHelper = new DataBaseHelper(AddToDoActivity.context);
                                dataBaseHelper.addCategory(new CategoryModel(categoryText));

                                ArrayList<CategoryModel> categories = dataBaseHelper.getAllCategories();

                                ArrayAdapter<CategoryModel> arrayAdapter = new ArrayAdapter<CategoryModel>(AddToDoActivity.context, android.R.layout.simple_spinner_item, categories);
                                arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                AddToDoActivity.categorySpinner.setAdapter(arrayAdapter);
                            }else{

                                dataBaseHelper = new DataBaseHelper(EditToDoActivity.context);
                                dataBaseHelper.addCategory(new CategoryModel(categoryText));

                                ArrayList<CategoryModel> categories = dataBaseHelper.getAllCategories();

                                ArrayAdapter<CategoryModel> arrayAdapter = new ArrayAdapter<CategoryModel>(EditToDoActivity.context, android.R.layout.simple_spinner_item, categories);
                                arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                EditToDoActivity.categorySpinner.setAdapter(arrayAdapter);
                            }
                        }
                    });

            addCategoryEditText = view.findViewById(R.id.addCategoryEditText);
        }
        else if(type == "MoreInfoToDo") {
            View view = inflater.inflate(R.layout.more_info_todo_popup, null);

            titleTextView = view.findViewById(R.id.titleTextViewMoreInfo2);
            descriptionTextView = view.findViewById(R.id.descriptionTextViewMoreInfo2);
            dateTextView = view.findViewById(R.id.dateTextViewMoreInfo2);
            isDoneTextView = view.findViewById(R.id.isDoneTextViewMoreInfo2);

            titleTextView.setText(this.getArguments().getString("Title"));
            descriptionTextView.setText(this.getArguments().getString("Description"));
            dateTextView.setText(this.getArguments().getString("Date"));
            id = this.getArguments().getInt("Id");

            if(this.getArguments().getBoolean("IsDone"))
                isDoneTextView.setText("Yes");
            else
                isDoneTextView.setText("No");

            if(MainActivity.isActivityVisible()){
            builder.setView(view)
                    .setTitle("Details")
                    .setNegativeButton("Edit", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dataBaseHelper = new DataBaseHelper(MainActivity.context);
                            ToDoModel toDoModel = dataBaseHelper.getToDoById(id);

                            Intent intent = new Intent(MainActivity.context, EditToDoActivity.class);
                            intent.putExtra("id", toDoModel.getId());
                            intent.putExtra("title", toDoModel.getTitle());
                            intent.putExtra("description", toDoModel.getDescription());
                            intent.putExtra("idCategory", toDoModel.getIdCategory());
                            intent.putExtra("date", toDoModel.getDate());

                            startActivity(intent);
                        }
                    })
                    .setNeutralButton("Delete", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dataBaseHelper = new DataBaseHelper(MainActivity.context);
                            dataBaseHelper.deleteToDo(id);

                            Intent intent = new Intent(MainActivity.context,MainActivity.class);
                            startActivity(intent);}

                    })
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dismiss();
                        }
                    });
            }else{
                builder.setView(view)
                        .setTitle("Details")
                        .setNeutralButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dataBaseHelper = new DataBaseHelper(MainActivity.context);
                                dataBaseHelper.deleteToDo(id);
                                    CalendarActivity calendarActivity = (CalendarActivity) CalendarActivity.context;
                                    calendarActivity.dialogToDo.dismiss();
                                    calendarActivity.dialogToDo = new DialogPopUps("toDoPopup",calendarActivity.getSupportFragmentManager());
                                    calendarActivity.dialogToDo.show(calendarActivity.getSupportFragmentManager(),null);
                                    try {
                                        calendarActivity.dialogToDo.initTitlesAndDescriptionsForDay(calendarActivity.date);
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }

                            }
                        })
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dismiss();
                            }
                        });
            }
        }
        else if(type=="toDoPopup"){
            View view = inflater.inflate(R.layout.todo_recyclerview_popup, null);

            Activity calendarActivity = (Activity) uni.fmi.bachelors.todoproject.CalendarActivity.context;
            RecyclerView recyclerView = view.findViewById(R.id.toDoRecyclerViewPopup);
            RecyclerViewAdapterPopUp adapter = new RecyclerViewAdapterPopUp(calendarActivity.getApplicationContext(), titles, descriptions,checks, fm);
            recyclerView.setLayoutManager(new LinearLayoutManager(calendarActivity.getApplicationContext()));

            recyclerView.setAdapter(adapter);

            builder.setView(view)
                    .setTitle("Tasks")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dismiss();
                        }
                    });
        }
        else if(type == "editCategory"){
            View view = inflater.inflate(R.layout.edit_category_popup, null);

            editCategoryNameEditText = view.findViewById(R.id.categoryEditTextPopup);
            editCategoryNameEditText.setText(this.getArguments().getString("CategoryName"));
            final int categoryId = this.getArguments().getInt("CategoryId");

            builder.setView(view)
                    .setTitle("Edit category")
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .setPositiveButton("Edit", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                                dataBaseHelper = new DataBaseHelper(CategoryActivity.context);
                                CategoryActivity categoryActivity = (CategoryActivity) CategoryActivity.context;

                                String editedName = editCategoryNameEditText.getText().toString();

                                dataBaseHelper.updateCategory(editedName,categoryId);

                                categoryActivity.initializeRecyclerView();
                        }
                    });
        }
        else if(type == "toDosFromCategory"){
            View view = inflater.inflate(R.layout.todo_recyclerview_popup, null);

            dataBaseHelper = new DataBaseHelper(CategoryActivity.context);
            ArrayList<ToDoModel> toDoModels = dataBaseHelper.getAllToDoByCategory(idCategory);

            Activity categoryActivity = (Activity) uni.fmi.bachelors.todoproject.CategoryActivity.context;
            RecyclerView recyclerView = view.findViewById(R.id.toDoRecyclerViewPopup);
            RecyclerViewToDosFromCategory adapter = new RecyclerViewToDosFromCategory(categoryActivity.getApplicationContext(), toDoModels);
            recyclerView.setLayoutManager(new LinearLayoutManager(categoryActivity.getApplicationContext()));

            recyclerView.setAdapter(adapter);

            builder.setView(view)
                    .setTitle("Tasks")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dismiss();
                        }
                    });
        }

        return builder.create();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void initTitlesAndDescriptionsForDay(String date) throws ParseException {

        dataBaseHelper = new DataBaseHelper(CalendarActivity.context);
        toDos = dataBaseHelper.getAllToDoByDay(date);

        for (int i = 0; i<toDos.size();i++){
            titles.add(toDos.get(i).getTitle());
            descriptions.add(toDos.get(i).getDescription());
            checks.add(toDos.get(i).isChecked());
        }
    }

}
