package com.example.questlistapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;

import com.example.questlistapp.Adapter.ToDoAdapter;
import com.example.questlistapp.Model.ToDoModel;
import com.example.questlistapp.Utils.DatabaseHandler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity implements DialogCloseListener{

    private RecyclerView taskRecyclerView;
    private ToDoAdapter taskAdapter;
    private DatabaseHandler db;
    private List<ToDoModel> taskList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().hide();
        db = new DatabaseHandler(this);
        db.openDatabase();
        taskList= new ArrayList<>();

        taskRecyclerView = findViewById(R.id.questRecyclerView);
        taskRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        taskAdapter = new ToDoAdapter(db,MainActivity.this);
        taskRecyclerView.setAdapter(taskAdapter);

//        ToDoModel quest = new ToDoModel();
//        quest.setTask("This is a Quest!!!");
//        quest.setStatus(0);
//        quest.setId(1);
//
//
//        taskList.add(quest);
//        taskList.add(quest);
//        taskList.add(quest);
//        taskList.add(quest);
//        taskList.add(quest);
//        taskList.add(quest);
//
//        taskAdapter.setTaskList(taskList);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new RecyclerItemTouchHelper(taskAdapter));
        itemTouchHelper.attachToRecyclerView(taskRecyclerView);
        taskList = db.getAllTasks();
        Collections.reverse(taskList);
        taskAdapter.setTaskList(taskList);

    }

    @Override
    public void handleDialogClsoe(DialogInterface dialog) {
        taskList = db.getAllTasks();
        Collections.reverse(taskList);
        taskAdapter.setTask(taskList);
        taskAdapter.notifyDataSetChanged();
    }

    @Override
    public void handleDialogClose(DialogInterface dialog) {
        taskList = db.getAllTasks();
        Collections.reverse(taskList);
        taskAdapter.setTask(taskList);
        taskAdapter.notifyDataSetChanged();
    }
}
