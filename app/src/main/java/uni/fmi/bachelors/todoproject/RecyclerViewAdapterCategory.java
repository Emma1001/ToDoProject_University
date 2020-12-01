package uni.fmi.bachelors.todoproject;

import android.content.Context;
import android.graphics.Color;
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

public class RecyclerViewAdapterCategory extends RecyclerView.Adapter<RecyclerViewAdapterCategory.ViewHolder> {

    ArrayList<CategoryModel> categoriesList;
    Context context;
    DataBaseHelper dataBaseHelper;
    FragmentManager fm;
    int selectedPosition = -1;


    public RecyclerViewAdapterCategory(Context context, ArrayList<CategoryModel> categoryNames, FragmentManager fm) {
        this.context = context;
        this.categoriesList = categoryNames;
        this.fm = fm;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.one_category_recyclerview,parent,false);
        RecyclerViewAdapterCategory.ViewHolder holder = new RecyclerViewAdapterCategory.ViewHolder(view);
        return holder;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final CategoryActivity categoryActivity = (CategoryActivity) CategoryActivity.context;

        dataBaseHelper = new DataBaseHelper(CategoryActivity.context);

        final CategoryModel categoryModel = CategoryActivity.categoriesList.get(position);

        holder.categoryTextView.setText(categoriesList.get(position).getName());

        if(selectedPosition==position)
            holder.itemView.setBackgroundColor(Color.parseColor("#e6e6e6"));
        else
            holder.itemView.setBackgroundColor(Color.parseColor("#ffffff"));

        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedPosition=position;
                notifyDataSetChanged();

                categoryActivity.initializeId(categoryModel.getId());

            }
        });
    }

    @Override
    public int getItemCount() {
        return categoriesList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView categoryTextView;
        RelativeLayout parentLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            categoryTextView = itemView.findViewById(R.id.categoryNameTextView);
            parentLayout = itemView.findViewById(R.id.parent_layout);

        }
    }
}

