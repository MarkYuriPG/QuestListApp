package com.example.questlistapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.questlistapp.Adapter.ToDoAdapter;
import com.example.questlistapp.Model.ToDoModel;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView questRecyclerView;
    private ToDoAdapter questAdapter;

    private List<ToDoModel> todolist;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().hide();

        todolist = new ArrayList<>();

        questRecyclerView = findViewById(R.id.questRecyclerView);
        questRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        questAdapter = new ToDoAdapter(this);
        questRecyclerView.setAdapter(questAdapter);

        ToDoModel quest = new ToDoModel();
        quest.setTask("This is a Quest!!!");
        quest.setStatus(0);
        quest.setId(1);

        todolist.add(quest);
        todolist.add(quest);
        todolist.add(quest);
        todolist.add(quest);
        todolist.add(quest);

        questAdapter.setToDoList(todolist);

    }
}