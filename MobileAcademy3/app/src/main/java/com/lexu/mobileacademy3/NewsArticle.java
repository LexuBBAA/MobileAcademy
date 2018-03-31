package com.lexu.mobileacademy3;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by lexu on 31.03.2018.
 */

public class NewsArticle {

    private String title = null;
    private Date date = null;

    public NewsArticle(String title, long dateInMillis) {
        this.title = title;
        this.date = new Date(dateInMillis);
    }

    private NewsArticle(String title, Date date) {
        this.title = title;
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public String getDate() {
        return new SimpleDateFormat("MMM-dd", Locale.getDefault()).format(this.date);
    }

    public NewsArticle cloneArticle() {
        return new NewsArticle(this.title, this.date);
    }
}
