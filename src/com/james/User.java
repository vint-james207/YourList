package com.james;

import java.util.ArrayList;

/**
 * Created by jamesyburr on 6/10/16.
 */
public class User {
    int id;
    String name;
    String password;
    ArrayList<ToDoItem> toDoItemText;

    public User(int id, String name, String password) {
        this.id = id;
        this.name = name;
        this.password = password;
        toDoItemText = new ArrayList<>();
    }

    public User(String name, String password) {
        this.name = name;
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public ArrayList<ToDoItem> getToDoItemText() {
        return toDoItemText;
    }

    public void setToDoItemText(ArrayList<ToDoItem> toDoItemText) {
        this.toDoItemText = toDoItemText;
    }
}
