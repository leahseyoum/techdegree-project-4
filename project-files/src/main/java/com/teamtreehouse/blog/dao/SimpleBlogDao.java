package com.teamtreehouse.blog.dao;

import com.teamtreehouse.blog.model.BlogEntry;

import java.util.ArrayList;
import java.util.List;

public class SimpleBlogDao implements BlogDao {
    private List<BlogEntry> blogEntries;

    public SimpleBlogDao() {
        this.blogEntries = new ArrayList<>();
    }

    @Override
    public boolean addEntry(BlogEntry blogEntry) {
        return blogEntries.add(blogEntry);
    }

    @Override
    public List<BlogEntry> findAllEntries() {
        return new ArrayList<>(blogEntries);
    }

    @Override
    public BlogEntry findEntryBySlug(String slug) {
        return blogEntries.stream()
                .filter(entry -> entry.getSlug().equals(slug))
                .findFirst()
                .orElseThrow(RuntimeException::new);
    }

    @Override
    public BlogEntry updateEntry(String slug, String title, String content, String tags) {
        BlogEntry entry = findEntryBySlug(slug);
        if (entry != null) {
            entry.setTitle(title);
            entry.setContent(content);
            entry.setSlug(title);
            entry.setTags(tags);
        }
        return entry;
    }

    @Override
    public void deleteEntryBySlug(String slug) {
        BlogEntry entry = findEntryBySlug(slug);
        blogEntries.remove(entry);
    }
}
