package com.james;

import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

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

}