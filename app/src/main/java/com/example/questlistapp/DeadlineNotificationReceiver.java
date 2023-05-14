package com.example.questlistapp;

import static android.content.Context.NOTIFICATION_SERVICE;
import static androidx.core.content.ContextCompat.getSystemService;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.Manifest;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.VibrationEffect;
import android.os.Vibrator;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.example.questlistapp.Model.ToDoModel;
import com.example.questlistapp.Utils.DatabaseHandler;

public class DeadlineNotificationReceiver extends BroadcastReceiver {
    private static final int ALARM_DELAY = 0 * 60 * 1000;
    private DatabaseHandler db;
    private Context context;

    public DeadlineNotificationReceiver() {}
    public DeadlineNotificationReceiver(Context context)
    {
        this.db = new DatabaseHandler(context);
        this.context = context;
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        int questId = intent.getIntExtra("questId", -1);

        if(questId==-1)
            return;

        db.setContext(context);

        ToDoModel item = db.getAllTasks().get(questId);

        if(item == null || item.getDeadline() == null)
            return;

        long deadlineInMillis = item.getDeadline().getTime();

        if (System.currentTimeMillis() >= deadlineInMillis) {
            // The deadline has passed, show the notification
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context.getApplicationContext())
                    .setSmallIcon(R.drawable.questlist_logo)
                    .setContentTitle("Quest Notification")
                    .setContentText("Quest deadline has passed")
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setAutoCancel(true);

            Intent notificationIntent = new Intent(context, MainActivity.class);
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);

            builder.setContentIntent(pendingIntent);
            builder.setAutoCancel(true);

            String permission = Manifest.permission.VIBRATE;
            int result = ContextCompat.checkSelfPermission(context, permission);

            if (result == PackageManager.PERMISSION_GRANTED) {
                // Vibrate the device
                Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                if (vibrator != null) {
                    vibrator.vibrate(1000);
                }
            }

            // Play alarm sound
            Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            if (alarmSound == null) {
                alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            }
            builder.setSound(alarmSound);

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            notificationManager.notify(questId, builder.build());
        }
    }
}
