package com.teamtreehouse.blog;

import com.teamtreehouse.blog.dao.BlogDao;
import com.teamtreehouse.blog.dao.SimpleBlogDao;
import com.teamtreehouse.blog.model.BlogEntry;
import com.teamtreehouse.blog.model.Comment;
import spark.ModelAndView;
import spark.Request;
import spark.template.handlebars.HandlebarsTemplateEngine;

import java.util.HashMap;
import java.util.Map;

import static spark.Spark.*;

public class Main {
    private static final String FLASH_MESSAGE_KEY = "flashMessage";

    public static void main(String[] args) {

        staticFileLocation("/public");
        BlogDao dao = new SimpleBlogDao();
        Map<String, Object> indexModel = new HashMap<>();
        BlogEntry blog1 = new BlogEntry("The best day I’ve ever had", "Today was the best day ever! I spent it with great friends, exploring new places, and making memories I'll cherish forever!", "test_author1", "exploring", "friends", "memories");
        BlogEntry blog2 = new BlogEntry("The absolute worst day I’ve ever had", "Today was the worst day ever. Everything went wrong from missing the bus to dealing with unexpected setbacks at work.", "test_author2");
        BlogEntry blog3 = new BlogEntry("That time at the mall", "That time at the mall was full of unexpected fun. We got lost in the maze of stores, but ended up discovering a hidden cafe with the best pastries.", "test_author3", "croissants");
        dao.addEntry(blog1);
        dao.addEntry(blog2);
        dao.addEntry(blog3);

        before((req, res) -> {
            if(req.cookie("password") != null) {
                req.attribute("password", req.cookie("password"));
            }
        });

        //before filter for the edit page
        before("/entries/:slug/edit", (req, res) -> {
            if(req.cookie("password") == null || !req.cookie("password").equals("admin")) {
                setFlashMessage(req,"Please sign in first");
                res.redirect("/password");
                halt();
            }
        });

        //before filter for the new entry creation page
        before("/new", (req, res) -> {
            if(req.cookie("password") == null || !req.cookie("password").equals("admin")) {
                setFlashMessage(req,"Please sign in first");
                res.redirect("/password");
                halt();
            }
        });

        get("/password", (req, res) -> {
            Map<String, String> model = new HashMap<>();
            model.put("flashMessage", captureFlashMessage(req));
            return new ModelAndView(model, "password.hbs");
        }, new HandlebarsTemplateEngine());

        post("/password", (req, res) -> {
           String password = req.queryParams("password");
           if (password.equals("admin")) {
               res.cookie("password", password);
               res.redirect("/");
           } else {
               setFlashMessage(req, "Invalid password. Try again.");
               res.redirect("/password");
           }
           return null;
        });

        //route for index page
        get("/", (req, res) -> {
            indexModel.put("password", req.attribute("password"));
            indexModel.put("entries", dao.findAllEntries());
            return new ModelAndView(indexModel, "index.hbs");
        }, new HandlebarsTemplateEngine());

        //route for details page
        get("entries/:slug", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            BlogEntry entry = dao.findEntryBySlug(req.params("slug"));
            model.put("entry", entry);
            return new ModelAndView(model, "detail.hbs");
        }, new HandlebarsTemplateEngine());

        //route for new entry page
        get("/new", (req, res) -> {
            return new ModelAndView(null, "new.hbs");
        }, new HandlebarsTemplateEngine());

        //route for entry creation
        post("/entries", (req, res) -> {
           String title = req.queryParams("title");
           String content = req.queryParams("content");
           String author = req.queryParams("author");
           String tags = req.queryParams("tags");
           String[] tagsArray = tags.split(",");
           BlogEntry newEntry = new BlogEntry(title, content, author, tagsArray);
           dao.addEntry(newEntry);
           res.redirect("/");
           return null;
        });

        //route for edit page
        get("/entries/:slug/edit", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
           BlogEntry entry = dao.findEntryBySlug(req.params("slug"));
           model.put("entry", entry);
           return new ModelAndView(model, "edit.hbs");
        }, new HandlebarsTemplateEngine());

        //route for entry edit
        post("/entries/:slug/edit", (req, res) -> {
           String newTitle = req.queryParams("title");
           String newContent = req.queryParams("content");
           String tags = req.queryParams("tags");
           BlogEntry updatedEntry = dao.updateEntry(req.params("slug"), newTitle, newContent, tags);
           res.redirect("/entries/" + updatedEntry.getSlug());
           return null;
        });

        //route for new comment creation
        post("/entries/:slug/comments", (req, res) -> {
            BlogEntry entry = dao.findEntryBySlug(req.params("slug"));
            String name = req.queryParams("name");
            String comment = req.queryParams("comment");
            Comment newComment = new Comment(name, comment);
            entry.addComment(newComment);
            res.redirect("/entries/" + req.params("slug"));
            return null;
        });

        //route for entry deletion
        post("/entries/:slug/delete", (req, res) -> {
           dao.deleteEntryBySlug(req.params("slug"));
           res.redirect("/");
           return null;
        });

    }

    //Flash Message Static methods
    private static void setFlashMessage(Request req, String message) {
        req.session().attribute(FLASH_MESSAGE_KEY, message);
    }

    private static String getFlashMessage(Request req) {
        if (req.session(false) == null) {
            return null;
        }
        if (!req.session().attributes().contains(FLASH_MESSAGE_KEY)) {
            return null;
        }
        return (String) req.session().attribute(FLASH_MESSAGE_KEY);
    }

    private static String captureFlashMessage(Request req) {
        String message = getFlashMessage(req);
        if (message != null) {
            req.session().removeAttribute(FLASH_MESSAGE_KEY);
        }
        return message;
    }
}
