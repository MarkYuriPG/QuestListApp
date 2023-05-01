package com.example.questlistapp;

import android.content.DialogInterface;
import android.view.MenuItem;

import androidx.annotation.NonNull;

public interface DialogCloseListener {
    boolean onOptionsItemSelected(@NonNull MenuItem item);

    public void handleDialogClose(DialogInterface dialog);
}
