package com.james;

import com.sun.org.apache.xpath.internal.operations.Mod;
import spark.ModelAndView;
import spark.Session;
import spark.Spark;
import spark.template.mustache.MustacheTemplateEngine;

import java.util.HashMap;

public class Main {

    static HashMap<String, User> users = new HashMap<>();

    public static void main(String[] args) {
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
                    ToDoItem item = new ToDoItem(list, user.toDoItemText.size());
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

                    return new ModelAndView(user,"edit.html");
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
