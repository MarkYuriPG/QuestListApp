package com.example.questlistapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.Manifest;
import android.os.VibrationEffect;
import android.os.Vibrator;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.example.questlistapp.Model.ToDoModel;
import com.example.questlistapp.Utils.DatabaseHandler;

public class DeadlineNotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        int questId = intent.getIntExtra("questId", -1);

        if(questId==-1)
            return;

        DatabaseHandler db = new DatabaseHandler(context);
        ToDoModel item = db.getAllTasks().get(questId);

        if(item == null || item.getDeadline() == null)
            return;

        long deadlineInMillis = item.getDeadline().getTime();

        if (System.currentTimeMillis() >= deadlineInMillis) {
            // The deadline has passed, show the notification
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "default")
                    .setSmallIcon(R.drawable.ic_settings)
                    .setContentTitle("Quest Notification")
                    .setContentText("Quest deadline has passed")
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setAutoCancel(true);

            String permission = Manifest.permission.VIBRATE;
            int result = ContextCompat.checkSelfPermission(context, permission);

            if (result == PackageManager.PERMISSION_GRANTED) {
                // Vibrate the device
                Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                if (vibrator != null) {
                    vibrator.vibrate(1000);
                }
            }

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            notificationManager.notify(questId, builder.build());
        }
    }
}
