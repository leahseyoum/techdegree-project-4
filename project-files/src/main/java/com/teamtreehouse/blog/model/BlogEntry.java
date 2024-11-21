package com.teamtreehouse.blog.model;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class BlogEntry {
    private String title;
    private String content;
    private String author;
    private LocalDateTime date;
    private List<Comment> comments;

    public BlogEntry(String title, String content, String author) {
        this.title = title;
        this.content = content;
        this.author = author;
        this.date = LocalDateTime.now();
        this.comments = new ArrayList<>();
    }

    public boolean addComment(Comment comment) {
       return comments.add(comment);
    }

    public List<Comment> getComments() {
        return comments;
    }
}
