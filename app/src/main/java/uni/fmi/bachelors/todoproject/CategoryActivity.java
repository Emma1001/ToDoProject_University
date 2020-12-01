package uni.fmi.bachelors.todoproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.text.ParseException;
import java.util.ArrayList;

public class CategoryActivity extends AppCompatActivity {

    RecyclerView categoriesRecyclerView;
    Button addButton, editButton, deleteButton,showToDosButton;

    DataBaseHelper dataBaseHelper;

    public static ArrayList<CategoryModel> categoriesList;
    ArrayList<String> categoriesNames;
    ArrayList<Integer> categoriesIds;

    static Context context;
    int id=-1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        context = CategoryActivity.this;

        categoriesRecyclerView = findViewById(R.id.categoriesRecyclerView);
        addButton = findViewById(R.id.addButton);
        editButton = findViewById(R.id.editButton);
        deleteButton = findViewById(R.id.deleteButton);
        showToDosButton = findViewById(R.id.showToDoButton);

        addButton.setOnClickListener(onClick);
        editButton.setOnClickListener(onClick);
        deleteButton.setOnClickListener(onClick);
        showToDosButton.setOnClickListener(onClick);

        dataBaseHelper = new DataBaseHelper(this);
        categoriesNames = new ArrayList<>();
        categoriesIds = new ArrayList<>();

        initializeRecyclerView();
    }

    View.OnClickListener onClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(v.getId() == R.id.addButton){
                DialogPopUps dialogCategory = new DialogPopUps("Category");
                dialogCategory.show(getSupportFragmentManager(),null);

            }else if(v.getId() == R.id.editButton){
                if(id == -1)
                    return;

                dataBaseHelper = new DataBaseHelper(CategoryActivity.context);
                String categoryName = dataBaseHelper.getCategoryName(id);

                DialogPopUps d = new DialogPopUps("editCategory");
                Bundle b = new Bundle();
                b.putString("CategoryName", categoryName);
                b.putInt("CategoryId", id);

                d.setArguments(b);
                d.show(getSupportFragmentManager(),null);


            }else if(v.getId() == R.id.deleteButton){
                if(id == -1)
                    return;

                dataBaseHelper = new DataBaseHelper(CategoryActivity.this);
                dataBaseHelper.deleteCategory(id);
                initializeRecyclerView();
            }else if(v.getId()==R.id.showToDoButton){
                if(id == -1)
                    return;

                DialogPopUps dialogToDo = new DialogPopUps("toDosFromCategory",id);
                dialogToDo.show(getSupportFragmentManager(),null);
            }
        }
    };

    public void initializeRecyclerView(){

        categoriesList = dataBaseHelper.getAllCategories();

        for (int i = 0; i<categoriesList.size();i++){
            categoriesNames.add(categoriesList.get(i).getName());
            categoriesIds.add(categoriesList.get(i).getId());
        }

        initRecyclerView();
    }

    private void initRecyclerView(){
        RecyclerView recyclerView = findViewById(R.id.categoriesRecyclerView);
        RecyclerViewAdapterCategory adapter = new RecyclerViewAdapterCategory(this, categoriesList,this.getSupportFragmentManager());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    public void initializeId(int id){
        this.id = id;
    }

    @Override
    protected void onResume() {
        super.onResume();
        CategoryActivity.activityResumed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        CategoryActivity.activityPaused();
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