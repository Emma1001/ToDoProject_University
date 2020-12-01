package uni.fmi.bachelors.todoproject;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
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

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    ArrayList<String> titles;
    ArrayList<String> descriptions;
    ArrayList<Boolean> checks;
    Context context;
    DataBaseHelper dataBaseHelper;
    FragmentManager fm;


    public RecyclerViewAdapter(Context context, ArrayList<String> titles, ArrayList<String> descriptions, ArrayList<Boolean> checks,FragmentManager fm) {
        this.context = context;
        this.titles = titles;
        this.descriptions = descriptions;
        this.checks = checks;
        this.fm = fm;
    }

    @NonNull
    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.one_to_do_recyclerview,parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull final RecyclerViewAdapter.ViewHolder holder, final int position) {

        dataBaseHelper = new DataBaseHelper(MainActivity.context);

        final ToDoModel toDoModel = MainActivity.toDos.get(position);

        holder.titleText.setText(titles.get(position));
        holder.descriptionText.setText(descriptions.get(position));
        holder.checkBox.setChecked(checks.get(position));

        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(context, "Clicked on: "+toDoModel.getId(), Toast.LENGTH_SHORT).show();

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
                    //Toast.makeText(context, titles.get(position) + " is Checked", Toast.LENGTH_SHORT).show();
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

        TextView titleText, descriptionText;
        CheckBox checkBox;
        RelativeLayout parentLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            titleText = itemView.findViewById(R.id.titleTextViewOne);
            descriptionText = itemView.findViewById(R.id.descriptionTextViewOne);
            checkBox = itemView.findViewById(R.id.checkBox);
            parentLayout = itemView.findViewById(R.id.parent_layout);

        }
    }
}
