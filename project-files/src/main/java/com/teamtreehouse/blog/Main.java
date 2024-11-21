package com.teamtreehouse.blog;

import com.teamtreehouse.blog.dao.BlogDao;
import com.teamtreehouse.blog.dao.SimpleBlogDao;
import com.teamtreehouse.blog.model.BlogEntry;
import com.teamtreehouse.blog.model.Comment;
import spark.ModelAndView;
import spark.template.handlebars.HandlebarsTemplateEngine;

import java.util.HashMap;
import java.util.Map;

import static spark.Spark.*;

public class Main {
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
            if(req.cookie("username") != null) {
                req.attribute("username", req.cookie("username"));
            }
        });

        get("/", (req, res) -> {
            indexModel.put("username", req.attribute("username"));
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

    }
}
