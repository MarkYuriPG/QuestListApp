package com.example.questlistapp.Utils;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.questlistapp.Model.ToDoModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class DatabaseHandler extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private static final String NAME = "toDoListDatabase";
    public static final String TODO_TABLE = "todo";
    private static final String ID = "id";
    private static final String TASK = "task";
    private static final String STATUS = "status";
    private static final String DEADLINE = "deadline";

    private static final String ORDER = "order_";
    private static final String CREATE_TODO_TABLE = "CREATE TABLE " + TODO_TABLE + "(" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + TASK + " TEXT, "
            + STATUS + " INTEGER, " + DEADLINE + " TEXT, " + ORDER + " INTEGER) ";
    private SQLiteDatabase db;

    public DatabaseHandler(Context context) {
        super(context, NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TODO_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TODO_TABLE);
        // Create tables again
        onCreate(db);
    }

    public void openDatabase() {
        db = this.getWritableDatabase();
    }

    public void insertTask(ToDoModel task) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("task", task.getTask());
        values.put("status", task.getStatus());
        values.put("deadline", String.valueOf(task.getDeadline()));

        int lastOrderValue = 0;
        Cursor cursor = db.rawQuery("SELECT MAX(order_) FROM " + TODO_TABLE, null);
        if (cursor.moveToFirst()) {
            lastOrderValue = cursor.getInt(0);
        }
        cursor.close();

        // set the new order value
        values.put("order_", lastOrderValue + 1);

        db.insert(TODO_TABLE, null, values);
    }

    @SuppressLint("Range")
    public List<ToDoModel> getAllTasks() {
        List<ToDoModel> taskList = new ArrayList<>();
        Cursor cur = null;
        db.beginTransaction();
        try {
            cur = db.query(TODO_TABLE, null, null, null, null, null, "order_ DESC", null);
            if (cur != null) {
                if (cur.moveToFirst()) {
                    do {
                        ToDoModel task = new ToDoModel();
                        task.setId(cur.getInt(cur.getColumnIndex(ID)));
                        task.setTask(cur.getString(cur.getColumnIndex(TASK)));
                        task.setStatus(cur.getInt(cur.getColumnIndex(STATUS)));
                        task.setOrder(cur.getInt(cur.getColumnIndex(ORDER)));
                        long deadlineMillis = cur.getLong(cur.getColumnIndex(DEADLINE));

                        if (deadlineMillis > 0) {
                            task.setDeadline(new Date(deadlineMillis));
                        }

                        taskList.add(task);
                    }
                    while (cur.moveToNext());
                }
            }
        } finally {
            db.endTransaction();
            assert cur != null;
            cur.close();
        }
        return taskList;
    }

    public void updateStatus(int id, int status) {
        ContentValues cv = new ContentValues();
        cv.put(STATUS, status);
        db.update(TODO_TABLE, cv, ID + "= ?", new String[]{String.valueOf(id)});
    }

    public void updateTask(int id, String task) {
        ContentValues cv = new ContentValues();
        cv.put(TASK, task);
        db.update(TODO_TABLE, cv, ID + "= ?", new String[]{String.valueOf(id)});
    }

    public void deleteTask(int id) {
        db.delete(TODO_TABLE, ID + "= ?", new String[]{String.valueOf(id)});
    }

    public void updateDeadline(int id, long deadline) {
        ContentValues values = new ContentValues();
        values.put(DEADLINE, deadline);
        db.update(TODO_TABLE, values, ID + "=?", new String[]{String.valueOf(id)});
    }

    public void updateOrder(int id, int pos)
    {
        ContentValues values = new ContentValues();
        values.put(ORDER, pos);
        db.update(TODO_TABLE, values, ID + "=?", new String[]{String.valueOf(id)});
    }

}
