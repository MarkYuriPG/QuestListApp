package com.example.questlistapp.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.recyclerview.widget.RecyclerView;

import com.example.questlistapp.MainActivity;
import com.example.questlistapp.Model.ToDoModel;
import com.example.questlistapp.R;

import java.util.List;

public class ToDoAdapter extends RecyclerView.Adapter<ToDoAdapter.ViewHolder>
{

    private List <ToDoModel> toDoList;
    private MainActivity activity;

    public ToDoAdapter(MainActivity activity)
    {
        this.activity = activity;
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.quest_layout, parent, false);

        return new ViewHolder(itemView);
    }


    public void onBindViewHolder(ViewHolder holder, int position)
    {
        ToDoModel item = toDoList.get(position);
        holder.quest.setText(item.getQuest());
        holder.quest.setChecked(toBoolean(item.getStatus()));
    }

    public int getItemCount()
    {
        return toDoList.size();
    }

    private boolean toBoolean(int n)
    {
        return n!=0;
    }

    public void setQuest(List<ToDoModel> toDoList)
    {
        this.toDoList = toDoList;
        notifyDataSetChanged();
    }

    public  static class  ViewHolder extends  RecyclerView.ViewHolder
    {
        CheckBox quest;

        ViewHolder(View view)
        {
            super(view);
            quest = view.findViewById(R.id.todoCheckBox);
        }

    }


}
