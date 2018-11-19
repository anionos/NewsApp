package com.example.user.newsapp;

/**
 * Created by user on 11/17/2018.
 */

public class News {

    String title;
    String category;
    String date;
    String url;

    public News(String title, String category, String date, String url) {
        this.title = title;
        this.category = category;
        this.date = date;
        this.url = url;
    }

    public News() {
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
