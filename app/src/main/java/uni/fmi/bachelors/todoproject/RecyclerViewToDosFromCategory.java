package uni.fmi.bachelors.todoproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecyclerViewToDosFromCategory extends RecyclerView.Adapter<RecyclerViewToDosFromCategory.ViewHolder> {

    ArrayList<ToDoModel> toDoModels;
    DataBaseHelper dataBaseHelper;
    Context context;

    public RecyclerViewToDosFromCategory(Context context, ArrayList<ToDoModel> toDoModels) {
        this.context = context;
        this.toDoModels = toDoModels;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.one_todo_from_category_recyclerview,parent,false);
        RecyclerViewToDosFromCategory.ViewHolder holder = new RecyclerViewToDosFromCategory.ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        dataBaseHelper = new DataBaseHelper(CategoryActivity.context);

        String isDone = "Not done";

        holder.titleText.setText(toDoModels.get(position).getTitle());
        holder.dateText.setText(toDoModels.get(position).getDate());
        if(toDoModels.get(position).isChecked())
            isDone = "Done";

        holder.isDoneText.setText(isDone);
    }

    @Override
    public int getItemCount() {
        return toDoModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView titleText,dateText, isDoneText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            titleText = itemView.findViewById(R.id.titleTextViewFromCategory);
            dateText = itemView.findViewById(R.id.dateTextViewFromCategory);
            isDoneText = itemView.findViewById(R.id.isDoneTextViewFromCategory);
        }
    }
}
