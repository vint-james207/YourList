package com.james;

/**
 * Created by jamesyburr on 6/10/16.
 */
public class ToDoItem {
    int id;
    String listText;


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

    public ToDoItem(int id, String listText) {
        this.id = id;
        this.listText = listText;



    }
}
