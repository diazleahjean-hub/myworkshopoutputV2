package com.myworkshopoutput.model;

public class Note {

    private final String id;
    private final String title;
    private final String content;
    private final String createdAt;

    public Note(String id, String title, String content, String createdAt) {
        this.id        = id;
        this.title     = title;
        this.content   = content;
        this.createdAt = createdAt;
    }

    public String getId()        { return id; }
    public String getTitle()     { return title; }
    public String getContent()   { return content; }
    public String getCreatedAt() { return createdAt; }

    @Override
    public String toString() {
        return title;
    }
}