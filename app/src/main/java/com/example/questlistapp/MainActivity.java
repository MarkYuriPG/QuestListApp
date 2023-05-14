package com.example.questlistapp;

import static com.example.questlistapp.Utils.DatabaseHandler.TODO_TABLE;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.ColorDrawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.os.Vibrator;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.questlistapp.Adapter.OnDeleteListener;
import com.example.questlistapp.Adapter.OnTaskCompleteListener;
import com.example.questlistapp.Adapter.ToDoAdapter;
import com.example.questlistapp.Model.ToDoModel;
import com.example.questlistapp.Utils.DatabaseHandler;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements DialogCloseListener, OnTaskCompleteListener, OnDeleteListener {

    private RecyclerView taskRecyclerView;
    private ToDoAdapter taskAdapter;
    private DatabaseHandler db;
    private List<ToDoModel> taskList;
    private FloatingActionButton fab;
    private SQLiteDatabase sqlDb;

    private int completedQuestCount, totalQuestCount;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);


        Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.caramel)));

        db = new DatabaseHandler(this);
        db.openDatabase();
        sqlDb = db.getWritableDatabase();
        taskList = new ArrayList<>();


        taskRecyclerView = findViewById(R.id.questRecyclerView);
        taskRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        taskAdapter = new ToDoAdapter(db, MainActivity.this, this, this) {

            @Override
            public void OnDelete(boolean isComplete) {
                if (isComplete) {
                    totalQuestCount--;
                }
                updateTotalQuestCount(totalQuestCount);
            }

            @Override
            public void onTaskComplete(boolean isCompleted) {
                if (isCompleted) {
                    completedQuestCount++;
                } else {
                    completedQuestCount--;
                }
                updateCompletedQuestCount(completedQuestCount);
            }
        };
        taskRecyclerView.setAdapter(taskAdapter);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new RecyclerItemTouchHelper(taskAdapter));
        itemTouchHelper.attachToRecyclerView(taskRecyclerView);

        fab = findViewById(R.id.floatingActionButton);

        taskList = db.getAllTasks();

        completedQuestCount = countCompletedTasks();
        updateCompletedQuestCount(completedQuestCount);

        totalQuestCount = taskList.size();
        updateTotalQuestCount(totalQuestCount);

        sortByOrder(taskList);


        if (taskAdapter != null) {
            taskAdapter.setTaskList(taskList);
        }

        taskAdapter.setTaskList(taskList);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddNewTask.newInstance().show(getSupportFragmentManager(), AddNewTask.TAG);
                totalQuestCount = taskList.size() + 1;
                updateTotalQuestCount(totalQuestCount);
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "MyChannel";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("MyChannelId", name, importance);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        for (ToDoModel item : taskList) {
            if (item == null || item.getDeadline() == null)
                continue;

            long deadlineInMillis = item.getDeadline().getTime();

            if (System.currentTimeMillis() >= deadlineInMillis) {
                // The deadline has passed, show the notification
                NotificationCompat.Builder builder = new NotificationCompat.Builder(this.getApplicationContext(), "MyChannelId")
                        .setSmallIcon(R.drawable.ic_settings)
                        .setContentTitle("Quest ")
                        .setContentText(item.getTask()+" deadline due " + formatDeadline(deadlineInMillis))
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setAutoCancel(true);

                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                notificationManager.notify(item.getId(), builder.build());
                }

            }


        ItemTouchHelper itemTouchHelper1 = new ItemTouchHelper(simpleCallback);
        itemTouchHelper1.attachToRecyclerView(taskRecyclerView);
    }

    private String formatDeadline(long deadline) {
        Date date = new Date(deadline);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd h:mm a", Locale.getDefault());
        return sdf.format(date);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId())
        {
            case R.id.Profile:
                Intent profile = new Intent(this, ProfileView.class);
                startActivity(profile);
                break;
            case R.id.MapItem:
                Intent map = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:10.316720, 123.890710"));
                startActivity(map);
                break;

            case R.id.CalendarItem:
                Intent calendar = new Intent(this, CalendarView.class);
                startActivity(calendar);
                break;

            case R.id.Settings:
                Intent settings = new Intent(this, SettingsActivity.class);
                startActivity(settings);
                break;

            case R.id.Exit:
                finish();
                System.exit(0);
                break;
        }
        return true;
    }

    @Override
    public void handleDialogClose(DialogInterface dialog) {
        taskList = db.getAllTasks();
        Collections.reverse(taskList);
        taskAdapter.setTask(taskList);
        taskAdapter.notifyDataSetChanged();
        int completedTaskCount = countCompletedTasks();
    }

    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP |
            ItemTouchHelper.DOWN | ItemTouchHelper.START | ItemTouchHelper.END, 0) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder,
                              @NonNull RecyclerView.ViewHolder target) {

            int fromPos = viewHolder.getAdapterPosition();
            int toPos = target.getAdapterPosition();

            // update the order value of the moved item
            db.updateOrder(taskList.get(fromPos).getId(), fromPos);
            db.updateOrder(taskList.get(toPos).getId(), toPos);

            Collections.swap(taskList, fromPos, toPos);
            recyclerView.getAdapter().notifyItemMoved(fromPos, toPos);

            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

        }
    };

    private int countCompletedTasks() {
        int count = 0;
        for (ToDoModel task : taskList) {
            if (task.getStatus() == 1) {
                count++;
            }
        }
        return count;
    }

    private void updateCompletedQuestCount(int completedTaskCount)
    {
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextView completedTaskCountView = findViewById(R.id.completedQuestCountTextView);
        completedTaskCountView.setText("Completed Quests: " + completedTaskCount);
    }

    private void updateTotalQuestCount(int count)
    {
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextView totalQuestCountView = findViewById(R.id.totalQuestCountTextView);
        totalQuestCountView.setText("Total Quests: " + count);
    }

    public void updateTaskOrder(int taskId, int newOrder) {
        SQLiteDatabase sqldb = db.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("order_", newOrder);
        sqldb.update(TODO_TABLE, values, "id=?", new String[] {String.valueOf(taskId)});
    }

    public static void sortByOrder(List<ToDoModel> list) {
        Collections.sort(list, new Comparator<ToDoModel>() {
            @Override
            public int compare(ToDoModel t1, ToDoModel t2) {
                return t1.getOrder() - t2.getOrder();
            }
        });
    }

    @Override
    public void onTaskComplete(boolean isCompleted) {
        if (isCompleted) {
            completedQuestCount++;
        } else {
            completedQuestCount--;
        }
        updateCompletedQuestCount(completedQuestCount);
    }

    @Override
    public void OnDelete(boolean isComplete) {
        if(isComplete) {
            if(totalQuestCount>0)
                --totalQuestCount;
            else
                totalQuestCount = 0;
        }
        updateTotalQuestCount(totalQuestCount);
    }

}
