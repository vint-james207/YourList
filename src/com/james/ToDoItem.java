package com.james;

/**
 * Created by jamesyburr on 6/10/16.
 */
public class ToDoItem {
    String listText;
    int id;

    public String getListText() {
        return listText;
    }

    public void setListText(String listText) {
        this.listText = listText;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ToDoItem(String listText, int id) {
        this.listText = listText;
        this.id = id;


    }
}
