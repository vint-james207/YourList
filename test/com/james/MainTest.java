package com.james;

import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * Created by jamesyburr on 6/14/16.
 */
public class MainTest {
    public Connection startConnection() throws SQLException {
        Connection conn = DriverManager.getConnection("jdbc:h2:mem:test");
        Main.createTables(conn);
        return conn;
    }

    @Test
    public void testUser() throws SQLException {
        Connection conn = startConnection();
        Main.insertUser(conn, "James", "");
        User user = Main.selectUser(conn, "James");
        conn.close();
        assertTrue(user != null);
    }

    @Test
    public void testEntry() throws SQLException {
        Connection conn = startConnection();
        Main.insertUser(conn, "James", "");
        User user = Main.selectUser(conn, "James");
        Main.insertList(conn, user.id, "Item");
        ToDoItem item = Main.selectEntry(conn, 1);
        assertTrue(item != null);
        assertTrue(item.getListText().equals("Item"));
    }

    @Test
    public void testEntries() throws SQLException {
        Connection conn = startConnection();
        Main.insertUser(conn, "Jack", "");
        User jack = Main.selectUser(conn, "Jack");
        Main.insertList(conn, jack.id, "Item1");
        Main.insertList(conn, jack.id, "Item2");
        Main.insertList(conn, jack.id, "Item3");
        ArrayList<ToDoItem> items = Main.selectEntries(conn, 1);
        conn.close();
        assertTrue(items.size() == 3);
    }

    @Test
    public void testUpdateEntries() throws SQLException {
        Connection conn = startConnection();
        Main.insertUser(conn, "Jared", "");
        User jared = Main.selectUser(conn, "Jared");
        Main.insertList(conn, jared.id, "Item1");
        Main.updateEntries(conn, 1, "Item2");
        ToDoItem item = Main.selectEntry(conn, 1);
        conn.close();
        assertTrue(item.getListText().equals("Item2"));
    }

    @Test
    public void testDeleteEntries() throws SQLException {
        Connection conn = startConnection();
        Main.insertUser(conn, "Jason", "");
        Main.insertList(conn, 1, "Item1");
        Main.deleteEntries(conn, 1);
        ToDoItem item = Main.selectEntry(conn, 1);
        conn.close();
        assertTrue(item == null);
    }
}