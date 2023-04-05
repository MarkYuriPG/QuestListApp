package com.example.questlistapp.Model;

public class ToDoModel
{

    private int id, status;
    private String quest;


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

    public String getQuest()
    {
        return quest;
    }

    public void setQuest(String task)
    {
        this.quest = task;
    }
}
