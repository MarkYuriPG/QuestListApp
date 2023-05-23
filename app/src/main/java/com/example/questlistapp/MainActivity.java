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
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.questlistapp.Adapter.OnDeleteListener;
import com.example.questlistapp.Adapter.OnTaskCompleteListener;
import com.example.questlistapp.Adapter.ToDoAdapter;
import com.example.questlistapp.Model.ToDoModel;
import com.example.questlistapp.Utils.DatabaseHandler;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements DialogCloseListener, OnTaskCompleteListener, OnDeleteListener {

    private static final int REQUEST_CAMERA_PERMISSION = 1;
    private RecyclerView taskRecyclerView;
    private ToDoAdapter taskAdapter;
    private DatabaseHandler db;
    private List<ToDoModel> taskList;
    private FloatingActionButton fab;
    private SQLiteDatabase sqlDb;
    private ImageView star;
    private TextView starCtr;
    private int completedQuestCount, totalQuestCount,starCount =0;
    private androidx.recyclerview.widget.RecyclerView clickerforstar;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_PICK = 2;
    private static final int NOTIFICATION_ID = 1;


    @SuppressLint({"MissingPermission", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted, request it
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        }
        
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String token = task.getResult();
                        Log.d("FCM Token", token);
                        // Save or send the token to your server for later use
                    } else {
                        Log.e("FCM Token", "Failed to get token");
                    }
                });

        setContentView(R.layout.activity_main);
//        STAR SYSTEM
        star = findViewById(R.id.star);
        starCtr = findViewById(R.id.starCountText);
        star.setVisibility(View.INVISIBLE);
        clickerforstar =findViewById(R.id.questRecyclerView);

        Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.caramel)));

        db = new DatabaseHandler(this);
        db.openDatabase();
        sqlDb = db.getWritableDatabase();
        taskList = new ArrayList<>();


        taskRecyclerView = findViewById(R.id.questRecyclerView);
        taskRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        taskAdapter = new ToDoAdapter(db, MainActivity.this, this, this, taskRecyclerView) {

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
                updateStarCount();
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

//      SET STAR VISIBLE REWARD USER
        clickerforstar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateStarCount();
            }
        });
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

            if (System.currentTimeMillis() >= deadlineInMillis - (3 * 24 * 60 * 60 * 1000)) {
                Intent intent = new Intent(this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                // You can pass any necessary data to the activity using intent.putExtra()

                // Create the pending intent

                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                // The deadline has passed, show the notification
                NotificationCompat.Builder builder = new NotificationCompat.Builder(this.getApplicationContext(), "MyChannelId")
                        .setSmallIcon(R.drawable.questlist_logo)
                        .setContentTitle("HEY, DO YOUR QUEST!")
                        .setContentText(item.getTask()+" deadline is " + formatDeadline(deadlineInMillis))
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
            scheduleDeadline(item);
        }

        ItemTouchHelper itemTouchHelper1 = new ItemTouchHelper(simpleCallback);
        itemTouchHelper1.attachToRecyclerView(taskRecyclerView);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Camera permission granted, you can now proceed with camera-related operations
            } else {
                // Camera permission denied, handle this situation (e.g., show an error message)
            }
        }
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
            case R.id.Home:
                break;
            case R.id.Profile:
                Intent profile = new Intent(this, ProfileView.class);
                startActivity(profile);
                finish();
                break;
            case R.id.MapItem:
                Intent map = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:10.316720, 123.890710"));
                startActivity(map);
                finish();
                break;

            case R.id.CalendarItem:
                Intent calendar = new Intent(this, CalendarView.class);
                startActivity(calendar);
                finish();
                break;

            case R.id.Settings:
                Intent settings = new Intent(this, SettingsActivity.class);
                startActivity(settings);
                finish();
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
            taskList.get(fromPos).setOrder(toPos);
            taskList.get(toPos).setOrder(fromPos);

            db.updateOrder(taskList.get(fromPos).getId(), taskList.get(toPos).getOrder());
            db.updateOrder(taskList.get(toPos).getId(), taskList.get(fromPos).getOrder());


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
        updateStarCount ();
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

    public void updateStarCount (){
            if(completedQuestCount%5==0 && completedQuestCount!=0)
            {
                ++starCount;
                star.setVisibility(View.VISIBLE);
                starCtr.setText(Integer.toString(starCount));
                Toast.makeText(this, "You earned a star!", Toast.LENGTH_SHORT).show();
            }
    }

    private void scheduleDeadlineAlarm(long deadlineInMillis) {
        // Create an intent for the deadline notification receiver
        Intent intent = new Intent(this, DeadlineNotificationReceiver.class);
        intent.putExtra("QUEST DEADLINE", "Your deadline is in 3-days."); // Pass task information if needed

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, NOTIFICATION_ID, intent, 0);

        // Get the AlarmManager system service
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        // Schedule the alarm
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // For API level 23 and above
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, deadlineInMillis, pendingIntent);
        } else {
            // For API level below 23
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, deadlineInMillis, pendingIntent);
        }
    }

    // Call this method wherever you want to schedule the deadline alarm
    private void scheduleDeadline(ToDoModel todo) {
        long deadlineInMillis = todo.getDeadline().getTime();
        scheduleDeadlineAlarm(deadlineInMillis);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            // The image capture was successful
            // You can retrieve the captured image from the `data` intent if needed
            int position = requestCode - REQUEST_IMAGE_CAPTURE;
            Bundle extras = data.getExtras();
            if (extras != null){
                Bitmap imageBitmap = (Bitmap) extras.get("data");

            // Do something with the captured image
            ToDoAdapter.ViewHolder viewHolder = (ToDoAdapter.ViewHolder) taskRecyclerView.findViewHolderForAdapterPosition(position);
            if (viewHolder != null) {
                viewHolder.changePhoto(position, imageBitmap);
            }}


        } else if (requestCode == REQUEST_IMAGE_PICK && resultCode == RESULT_OK) {
            // The image selection was successful
            // You can retrieve the selected image from the `data` intent if needed

            // Do something with the selected image
            int position = requestCode - REQUEST_IMAGE_PICK;
            Uri selectedImageUri = data.getData();

            try {
                Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);

                // Update the photo at the specified position in the RecyclerView
                ToDoAdapter.ViewHolder viewHolder = (ToDoAdapter.ViewHolder) taskRecyclerView.findViewHolderForAdapterPosition(position);
                if (viewHolder != null) {
                    viewHolder.changePhoto(position, imageBitmap);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
