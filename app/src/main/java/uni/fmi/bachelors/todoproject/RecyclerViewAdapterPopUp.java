package uni.fmi.bachelors.todoproject;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecyclerViewAdapterPopUp extends RecyclerView.Adapter<RecyclerViewAdapterPopUp.ViewHolder>  {

    ArrayList<String> titles;
    ArrayList<String> descriptions;
    ArrayList<Boolean> checks;
    Context context;
    DataBaseHelper dataBaseHelper;
    FragmentManager fm;
    android.app.FragmentManager fm1;


    public RecyclerViewAdapterPopUp(Context context, ArrayList<String> titles, ArrayList<String> descriptions, ArrayList<Boolean> checks,FragmentManager fm) {
        this.context = context;
        this.titles = titles;
        this.descriptions = descriptions;
        this.checks = checks;
        this.fm = fm;
    }

    public RecyclerViewAdapterPopUp(Context context, ArrayList<String> titles, ArrayList<String> descriptions, ArrayList<Boolean> checks) {
        this.context = context;
        this.titles = titles;
        this.descriptions = descriptions;
        this.checks = checks;
    }

    @NonNull
    @Override
    public RecyclerViewAdapterPopUp.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.one_todo_popup_recyclerview,parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull final RecyclerViewAdapterPopUp.ViewHolder holder, final int position) {

        dataBaseHelper = new DataBaseHelper(CalendarActivity.context);

        final ToDoModel toDoModel = DialogPopUps.toDos.get(position);

        holder.titleText.setText(titles.get(position));
        holder.checkBox.setChecked(checks.get(position));

        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Clicked on: "+toDoModel.getId(), Toast.LENGTH_SHORT).show();

                DialogPopUps d = new DialogPopUps("MoreInfoToDo");
                Bundle b = new Bundle();
                b.putString("Title", toDoModel.getTitle());
                b.putString("Description", toDoModel.getDescription());
                b.putString("Date", toDoModel.getDate());
                b.putBoolean("IsDone", toDoModel.isChecked());
                b.putInt("Id", toDoModel.getId());

                d.setArguments(b);
                d.show(fm,null);
            }
        });

        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    toDoModel.setChecked(true);
                    buttonView.setChecked(true);
                }
                else{
                    toDoModel.setChecked(false);
                }

                dataBaseHelper.updateIsCheckedOnToDo(toDoModel);
            }
        });
    }

    @Override
    public int getItemCount() {
        return titles.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView titleText;
        CheckBox checkBox;
        RelativeLayout parentLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            titleText = itemView.findViewById(R.id.titleTextViewOne);
            checkBox = itemView.findViewById(R.id.checkBox);
            parentLayout = itemView.findViewById(R.id.parent_layout);

        }
    }

}
