package com.example.questlistapp.Model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ToDoModel {

    private int id, status;
    private String todo;

    private Date deadline;

    public  ToDoModel(){}

    public ToDoModel(int status, String todo, Date deadline) {
        this.status = status;
        this.todo = todo;
        this.deadline = deadline;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getTask() {
        return todo;
    }

    public void setTask(String task) {
        this.todo = task;
    }

    public Date getDeadline() {
        return deadline;
    }

    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }
}