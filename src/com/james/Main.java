package com.james;
import org.h2.tools.Server;
import spark.ModelAndView;
import spark.Session;
import spark.Spark;
import spark.template.mustache.MustacheTemplateEngine;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

public class Main {

    public static void createTables(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();
        stmt.execute("CREATE TABLE IF NOT EXISTS users (id IDENTITY, name VARCHAR, password VARCHAR)");
        stmt.execute("CREATE TABLE IF NOT EXISTS todoList (id IDENTITY, user_id INT, listText VARCHAR)");
    }

    public static void insertUser(Connection conn, String name, String password) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO users VALUES (NULL, ?, ?)");
        stmt.setString(1, name);
        stmt.setString(2, password);
        stmt.execute();
    }

    public static User selectUser(Connection conn, String name) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users WHERE name = ?");
        stmt.setString(1, name);
        ResultSet results = stmt.executeQuery();
        if (results.next()) {
            int id = results.getInt("id");
            String password = results.getString("password");
            return new User(id, name, password);
        }
        return null;
    }

    public static void insertList(Connection conn, int id, String listText) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO todoList VALUES (NULL, ?, ?)");
        stmt.setInt(1, id);
        stmt.setString(2, listText);
        stmt.execute();
    }

    public static ToDoItem selectEntry(Connection conn, int id) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM todoList INNER JOIN users ON todoList.user_id = users.id WHERE todoList.id = ?");
        stmt.setInt(1, id);
        ResultSet results = stmt.executeQuery();
        if (results.next()) {
            String listText = results.getString("todoList.listText");
            return new ToDoItem(id, listText);
        }
        return null;
    }


    static HashMap<String, User> users = new HashMap<>();

    public static void main(String[] args) throws SQLException {
        Server.createWebServer().start();
        Connection conn = DriverManager.getConnection("jdbc:h2:./main");
        createTables(conn);

        Spark.staticFileLocation("Public");

        Spark.init();

        Spark.get(
                "/",
                (request, response) -> {
                    HashMap map = new HashMap();
                    Session session = request.session();
                    String name = session.attribute("username");
                    User user = users.get(name);
                    if (user == null) {
                        return new ModelAndView(map, "login.html");
                    }
                    else {
                        map.put("listItems", user.toDoItemText);
                        map.put("name", user.name);
                        return  new ModelAndView(map, "home.html");
                    }
                },
                new MustacheTemplateEngine()
        );

        Spark.post(
                "/login",
                (request, response) -> {
                    String name = request.queryParams("username");
                    String password = request.queryParams("password");
                    User user = users.get(name);
                    if (user == null) {
                        user = new User(name, password);
                        users.put(name, user);
                    }
                    if (password.equals(user.password)) {
                        Session session = request.session();
                        session.attribute("username", name);
                    }

                    response.redirect("/");
                    return "";
                }
        );

        Spark.post(
                "/create-list",
                (request, response) -> {
                    Session session = request.session();
                    String name = session.attribute("username");
                    User user = users.get(name);
                    String list = request.queryParams("listText");
                    ToDoItem item = new ToDoItem(user.toDoItemText.size(), list);
                    user.toDoItemText.add(item);
                    response.redirect("/");
                    return "";
                }
        );

        Spark.post(
                "/delete",
                (request, response) -> {
                    int id = Integer.valueOf(request.queryParams("ID"));
                    Session session = request.session();
                    String name = session.attribute("username");
                    User user = users.get(name);
                    if (name == null) {
                        throw new Exception("You must log in to make changes");
                    }

                    user.toDoItemText.remove(id);

                    int index = 0;
                    for (ToDoItem item : user.toDoItemText) {
                        item.setId(index);
                        index++;
                    }
                    response.redirect("/");
                    return "";
                }
        );

        Spark.post(
                "/logout",
                (request, response) -> {
                    Session session = request.session();
                    session.invalidate();
                    response.redirect("/");
                    return "";
                }
        );
        Spark.get(
                "/edit-item",
                (request, response) -> {
                    Session session = request.session();
                    String name = session.attribute("username");
                    User user = users.get(name);
                    HashMap map = new HashMap();
                    int id = (Integer.valueOf(request.queryParams("ID")));
                    ToDoItem item = user.toDoItemText.get(id);
                    map.put("ListItem", item);

                    return new ModelAndView(map,"edit.html");
                },
                new MustacheTemplateEngine()
        );
        Spark.post(
                "/edit-item",
                (request, response) -> {
                    Session session = request.session();
                    String name = session.attribute("username");
                    User user = users.get(name);
                    int id = (Integer.valueOf(request.queryParams("ID")));
                    ToDoItem item = user.toDoItemText.get(id);
                    item.setListText(request.queryParams("UpdateItem"));
                    response.redirect("/");
                    return "";
                }
        );

    }
}
