package com.example.questlistapp.Model;

public class ToDoModel
{

    private int id, status;
    private String todo;


    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public int getStatus()
    {
        return status;
    }

    public void setStatus(int status)
    {
        this.status = status;
    }

    public String getTask()
    {
        return todo;
    }

    public void setTask(String task)
    {
        this.todo = task;
    }
}
