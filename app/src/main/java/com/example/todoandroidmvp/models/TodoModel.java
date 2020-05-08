package com.example.todoandroidmvp.models;

public class TodoModel {
    private String text;
    private Long ts;
    private boolean completed;

    public TodoModel(String text, Long ts, boolean completed) {
        this.text = text;
        this.ts = ts;
        this.completed = completed;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Long getTs() {
        return ts;
    }

    public void setTs(Long ts) {
        this.ts = ts;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
}
