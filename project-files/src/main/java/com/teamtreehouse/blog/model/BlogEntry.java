package com.teamtreehouse.blog.model;
import com.github.slugify.Slugify;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class BlogEntry {
    private String title;
    private String content;
    private String author;
    private LocalDateTime unformattedDate;
    private String date;
    private List<Comment> comments;
    private String slug;
    private Set<String> tags;

    public BlogEntry(String title, String content, String author, String ...tags) {
        this.title = title;
        this.content = content;
        this.author = author;
        unformattedDate = LocalDateTime.now();
        this.date = unformattedDate.format(DateTimeFormatter.ofPattern("MMMM dd, yyyy h:mm"));
        this.comments = new ArrayList<>();
        if (tags.length == 0) {
            this.tags = new HashSet<>();
        } else {
            this.tags = new HashSet<>();
            this.tags.addAll(Arrays.asList(tags));
        }
        try {
            Slugify slugify = new Slugify();
            slug = slugify.slugify(title);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getTitle() {return title;}
    public String getContent() {return content;}
    public String getAuthor() {return author;}
    public String getDate() {return date;}
    public Set<String> getTags() {return tags;}

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean addComment(Comment comment) {
        return comments.add(comment);
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setTags(String tags) {
        String[] tagsArray = tags.split(",");
        this.tags = new HashSet<>();
        this.tags.addAll(Arrays.asList(tagsArray));
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        try {
            Slugify slugify = new Slugify();
            slug = slugify.slugify(title);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
