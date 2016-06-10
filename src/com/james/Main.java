package com.james;

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
                    user.toDoItemText.add(new ToDoItem(list));
                    response.redirect("/");
                    return "";
                }
        );

    }
}
