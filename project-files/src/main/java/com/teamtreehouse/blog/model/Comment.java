package com.teamtreehouse.blog.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Comment {
    private String author;
    private String content;
    private LocalDateTime unformattedDate;
    private String date;

    public Comment(String author, String content) {
        this.author = author;
        this.content = content;
        unformattedDate = LocalDateTime.now();
        this.date = unformattedDate.format(DateTimeFormatter.ofPattern("MMMM dd, yyyy h:mm"));
    }

    public String getAuthor() {return author;}
    public String getContent() {return content;}
    public String getDate() {return date;}

}
