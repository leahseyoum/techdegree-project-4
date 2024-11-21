package com.teamtreehouse.blog.model;

public class Comment {
    private String author;
    private String content;
    private String date;

    public Comment(String author, String content, String date) {
        this.author = author;
        this.content = content;
        this.date = date;
    }

    public String getAuthor() {return author;}
    public String getContent() {return content;}
    public String getDate() {return date;}
}
