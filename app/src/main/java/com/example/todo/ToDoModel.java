package com.example.todo;

import java.io.Serializable;
import java.util.HashMap;

public class ToDoModel implements Serializable {

    private String id,name,description,date;


    public ToDoModel() { }


    public String getId() {
        return id;
    }

    public void setId (String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }


    public HashMap<String,String> toFirebaseObject() {
        HashMap<String,String> tasks =  new HashMap<String,String>();

        tasks.put("id",id);
        tasks.put("name", name);
        tasks.put("description", description);
        tasks.put("date", date);

        return tasks;
    }
}
