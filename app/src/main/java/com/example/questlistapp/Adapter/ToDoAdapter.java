package com.example.questlistapp.Adapter;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.recyclerview.widget.RecyclerView;

import com.example.questlistapp.AddNewTask;
import com.example.questlistapp.MainActivity;
import com.example.questlistapp.Model.ToDoModel;
import com.example.questlistapp.R;
import com.example.questlistapp.Utils.DatabaseHandler;

import java.text.BreakIterator;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ToDoAdapter extends RecyclerView.Adapter<ToDoAdapter.ViewHolder>
{

    private static List <ToDoModel> todoList;
    private MainActivity activity;
    private static DatabaseHandler db;

    public ToDoAdapter(DatabaseHandler db ,MainActivity activity)
    {
        this.db = db;
        this.activity = activity;
        this.todoList = db.getAllTasks();
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.todolist_layout, parent, false);

        return new ViewHolder(itemView);
    }


    public void onBindViewHolder(ViewHolder holder, int position)
    {
            db.openDatabase();
            ToDoModel item = todoList.get(position);
            holder.task.setText(item.getTask());


            Date deadlineValue = item.getDeadline();
            if (deadlineValue != null) {
                holder.deadline.setText(formatDeadline(deadlineValue.getTime()));
            } else {
                holder.deadline.setText("Tap to set deadline");
            }

            holder.task.setChecked(toBoolean(item.getStatus()));
            holder.task.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        db.updateStatus(item.getId(), 1);
                    } else {
                        db.updateStatus(item.getId(), 0);
                    }
                }
            });
    }
    private String formatDeadline(long deadline) {
        Date date = new Date(deadline);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd h:mm a", Locale.getDefault());
        return sdf.format(date);
    }

    public int getItemCount()
    {
        return todoList.size();
    }

    private boolean toBoolean(int n)
    {
        return n!=0;
    }

    public void setTaskList(List<ToDoModel> toDoList)
    {
        this.todoList = toDoList;
        notifyDataSetChanged();
    }
    public Context getContext(){
        return activity;
    }
    public void deleteItem(int position){
        db.openDatabase();
        db.deleteTask(todoList.get(position).getId());
        todoList.remove(position);
        notifyItemRemoved(position);
    }
    public void setTask(List<ToDoModel> todoList){
        this.todoList = todoList;
    }
    public void editItem(int position){
        ToDoModel item = todoList.get(position);
        Bundle bundle = new Bundle();
        bundle.putInt("id",item.getId());
        bundle.putString("task", item.getTask());
        AddNewTask fragment = new AddNewTask();
        fragment.setArguments(bundle);
        fragment.show(activity.getSupportFragmentManager(), AddNewTask.TAG);
    }
    public class  ViewHolder extends  RecyclerView.ViewHolder
    {
        CheckBox task;
        TextView deadline;

        ViewHolder(View view)
        {
            super(view);
            task = view.findViewById(R.id.todoCheckBox);
            deadline = view.findViewById(R.id.deadlineText);

            deadline.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDatePickerDialog(v.getContext(), getAdapterPosition());
                }
            });
        }

        // Show a date picker dialog when the user clicks on the deadline TextView
        private void showDatePickerDialog(Context context, int position) {
            Calendar calendar = Calendar.getInstance();

            // If there is already a deadline set, use that as the default date
            ToDoModel item = todoList.get(position);
           if (item.getDeadline() != null) {
                Date deadline = item.getDeadline();
                calendar.setTimeInMillis(deadline.getTime());
            }

            // Create a date picker dialog with the default date and show it
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    context,
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            // Update the deadline in the database and refresh the list
                            Calendar calendar = Calendar.getInstance();
                            calendar.set(Calendar.YEAR, year);
                            calendar.set(Calendar.MONTH, monthOfYear);
                            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                            Date deadline = calendar.getTime();
                            long deadlineInMillis = calendar.getTimeInMillis();
                            db.updateDeadline(item.getId(), deadlineInMillis);
                            todoList.get(position).setDeadline(deadline);
                            notifyDataSetChanged();
                        }
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            );

            final Calendar currentTime = Calendar.getInstance();

            // Create a TimePickerDialog and show it
            TimePickerDialog timePickerDialog = new TimePickerDialog(
                    context,
                    new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                            // Update the deadline in the database and refresh the list
                            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                            calendar.set(Calendar.MINUTE, minute);
                            Date deadline = calendar.getTime();
                            long deadlineInMillis = calendar.getTimeInMillis();
                            db.updateDeadline(item.getId(), deadlineInMillis);
                            todoList.get(position).setDeadline(deadline);
                            notifyDataSetChanged();
                        }
                    },
                    currentTime.get(Calendar.HOUR_OF_DAY),
                    currentTime.get(Calendar.MINUTE),
                    DateFormat.is24HourFormat(context)
            );

            timePickerDialog.show();
            datePickerDialog.show();
        }
    }


}
