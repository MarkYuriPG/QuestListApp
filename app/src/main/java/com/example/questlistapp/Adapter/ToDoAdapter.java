package com.example.questlistapp.Adapter;

import static android.app.Activity.RESULT_OK;
import static android.app.PendingIntent.FLAG_IMMUTABLE;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.questlistapp.AddNewTask;
import com.example.questlistapp.DeadlineNotificationReceiver;
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

public abstract class ToDoAdapter extends RecyclerView.Adapter<ToDoAdapter.ViewHolder> implements OnTaskCompleteListener, OnDeleteListener {

    private static List<ToDoModel> todoList;
    private MainActivity activity;
    private static DatabaseHandler db;

    private OnTaskCompleteListener onTaskCompleteListener;
    private OnDeleteListener onDeleteListener;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_PICK = 2;

    private RecyclerView recyclerView;


    public ToDoAdapter(DatabaseHandler db, MainActivity activity, OnTaskCompleteListener onTaskCompleteListener, OnDeleteListener onDeleteListener, RecyclerView recyclerView) {
        this.db = db;
        this.activity = activity;
        this.todoList = db.getAllTasks();
        this.onTaskCompleteListener = onTaskCompleteListener;
        this.onDeleteListener = onDeleteListener;
        this.recyclerView = recyclerView;
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.todolist_layout, parent, false);

        return new ViewHolder(itemView);
    }


    public void onBindViewHolder(ViewHolder holder, @SuppressLint("RecyclerView") int position) {
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
                        item.setStatus(1);
                    } else {
                        db.updateStatus(item.getId(), 0);
                        item.setStatus(0);
                    }

                /*item.setStatus(isChecked ? 1 : 0);
                db.updateStatus(item.getId(), item.getStatus());*/
                onTaskCompleteListener.onTaskComplete(isChecked);
            }
        });
    }

    private String formatDeadline(long deadline) {
        Date date = new Date(deadline);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd h:mm a", Locale.getDefault());
        return sdf.format(date);
    }

    public int getItemCount() {
        return todoList.size();
    }

    private boolean toBoolean(int n) {
        return n != 0;
    }

    public void setTaskList(List<ToDoModel> toDoList) {
        this.todoList = toDoList;
        notifyDataSetChanged();
    }

    public Context getContext() {
        return activity;
    }

    public void deleteItem(int position) {
        db.openDatabase();
        db.deleteTask(todoList.get(position).getId());
        todoList.remove(position);
        notifyItemRemoved(position);
        onDeleteListener.OnDelete(true);
    }

    public void setTask(List<ToDoModel> todoList) {
        this.todoList = todoList;
    }

    public void editItem(int position) {
        ToDoModel item = todoList.get(position);
        Bundle bundle = new Bundle();
        bundle.putInt("id", item.getId());
        bundle.putString("task", item.getTask());
        AddNewTask fragment = new AddNewTask();
        fragment.setArguments(bundle);
        fragment.show(activity.getSupportFragmentManager(), AddNewTask.TAG);
    }

    public abstract void OnDelete(boolean isComplete);


    public class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox task;
        TextView deadline;
        ImageView image;

        ViewHolder(View view) {
            super(view);
            task = view.findViewById(R.id.todoCheckBox);
            deadline = view.findViewById(R.id.deadlineText);
            image = view.findViewById(R.id.thumbnail);

            ViewGroup.LayoutParams layoutParams = image.getLayoutParams();
            layoutParams.width = 100;
            layoutParams.height = 100;
            image.setLayoutParams(layoutParams);

            deadline.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    showDatePickerDialog(v.getContext(), getAdapterPosition());
                }
            });

            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    showImageOptions(view, position);
                }
            });
        }
        public void changePhoto(int position, Bitmap bitmap) {
            if (getAdapterPosition() == position) {
                image.setImageBitmap(bitmap);
            }
        }
        private void showImageOptions(View view, int position) {
            PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
            popupMenu.getMenuInflater().inflate(R.menu.image_source_menu, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.cameraOption:
                            // Handle the camera option
                            takePictureFromCamera(position);
                            return true;
                        case R.id.galleryOption:
                            // Handle the gallery option
                            choosePictureFromGallery(position);
                            return true;
                        default:
                            return false;
                    }
                }
            });

            popupMenu.show();
        }
        public void choosePictureFromGallery(int position) {
            Intent pickPhotoIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            pickPhotoIntent.setType("image/*");
            activity.startActivityForResult(pickPhotoIntent, REQUEST_IMAGE_PICK+ position);
        }

        public void takePictureFromCamera(int position) {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(activity.getPackageManager()) != null) {
                activity.startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE+ position);
            }
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

            //final Calendar currentTime = Calendar.getInstance();

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

                            //long deadlineInMillis = item.getDeadline().getTime();


                            //DeadlineNotificationReceiver receiver = new DeadlineNotificationReceiver(context);
                            // Set up alarm manager
                           // AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

                            // Create intent to trigger alarm
                            //Intent intent = new Intent(context, DeadlineNotificationReceiver.class);
                           // PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, FLAG_IMMUTABLE);

                            // Set up calendar object with deadline date and time
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTimeInMillis(deadline.getTime());
                            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                            calendar.set(Calendar.MINUTE, minute);

                            // Schedule alarm
                            /*alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                            db.scheduleAlarm(deadlineInMillis, item.getId());*/

                        }
                    },
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    DateFormat.is24HourFormat(context)
            );

            item.setDeadline(calendar.getTime());
            timePickerDialog.show();
            datePickerDialog.show();
        }
    }
    


}
