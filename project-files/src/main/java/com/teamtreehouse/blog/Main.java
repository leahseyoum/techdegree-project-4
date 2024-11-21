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
        BlogEntry blog1 = new BlogEntry("The best day I’ve ever had", "this is test content", "test_author1");
        BlogEntry blog2 = new BlogEntry("The absolute worst day I’ve ever had", "this is test content", "test_author2");
        BlogEntry blog3 = new BlogEntry("That time at the mall", "this is test content", "test_author3");
        dao.addEntry(blog1);
        dao.addEntry(blog2);
        dao.addEntry(blog3);

        before((req, res) -> {
            if(req.cookie("password") != null) {
                req.attribute("password", req.cookie("password"));
            }
        });

//        before("/", (req, res) -> {
//            if(req.cookie("password") == null || !req.cookie("password").equals("admin")) {
//                setFlashMessage(req,"Please sign in first");
//                res.redirect("/password");
//            }
//        });

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
               req.session().attribute("flashMessage", "Invalid password. Try again.");
               res.redirect("/password");
           }
           return null;
        });

        get("/", (req, res) -> {
            indexModel.put("password", req.attribute("password"));
            indexModel.put("entries", dao.findAllEntries());
            return new ModelAndView(indexModel, "index.hbs");
        }, new HandlebarsTemplateEngine());

        get("entries/:slug", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            BlogEntry entry = dao.findEntryBySlug(req.params("slug"));
            model.put("entry", entry);
            return new ModelAndView(model, "detail.hbs");
        }, new HandlebarsTemplateEngine());

        get("/new", (req, res) -> {
            if(req.cookie("password") == null || !req.cookie("password").equals("admin")) {
                setFlashMessage(req,"Please sign in first");
                res.redirect("/password");
                halt();
            }
            return new ModelAndView(null, "new.hbs");
        }, new HandlebarsTemplateEngine());

        post("/entries", (req, res) -> {
           String title = req.queryParams("title");
           String content = req.queryParams("content");
           String author = req.queryParams("author");
           BlogEntry newEntry = new BlogEntry(title, content, author);
           dao.addEntry(newEntry);
           res.redirect("/");
           return null;
        });

        get("/entries/:slug/edit", (req, res) -> {
            if(req.cookie("password") == null || !req.cookie("password").equals("admin")) {
                setFlashMessage(req,"Please sign in first");
                res.redirect("/password");
                halt();
            }
            Map<String, Object> model = new HashMap<>();
           BlogEntry entry = dao.findEntryBySlug(req.params("slug"));
           model.put("entry", entry);
           return new ModelAndView(model, "edit.hbs");
        }, new HandlebarsTemplateEngine());

        post("/entries/:slug/edit", (req, res) -> {
           String newTitle = req.queryParams("title");
           String newContent = req.queryParams("content");
           BlogEntry updatedEntry = dao.updateEntry(req.params("slug"), newTitle, newContent);
           res.redirect("/entries/" + updatedEntry.getSlug());
           return null;
        });

        post("/entries/:slug/comments", (req, res) -> {
            BlogEntry entry = dao.findEntryBySlug(req.params("slug"));
            String name = req.queryParams("name");
            String comment = req.queryParams("comment");
            Comment newComment = new Comment(name, comment);
            entry.addComment(newComment);
            res.redirect("/entries/" + req.params("slug"));
            return null;
        });

        post("/entries/:slug/delete", (req, res) -> {
            if(req.cookie("password") == null || !req.cookie("password").equals("admin")) {
                setFlashMessage(req,"Please sign in first");
                res.redirect("/password");
                halt();
            }
           dao.deleteEntryBySlug(req.params("slug"));
           res.redirect("/");
           return null;
        });

    }

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
