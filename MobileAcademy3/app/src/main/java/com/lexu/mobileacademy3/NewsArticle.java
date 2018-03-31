package com.lexu.mobileacademy3;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by lexu on 31.03.2018.
 */

public final class NewsArticle implements Serializable {

    private interface NewsArticleUtils {
        String SOURCE_KEY = "source";
        String SOURCE_ID_KEY = "id";
        String SOURCE_NAME_KEY = "name";
        String ARTICLE_TITLE_KEY = "title";
        String ARTICLE_AUTHOR_KEY = "author";
        String ARTICLE_DESCRIPTION_KEY = "description";
        String ARTICLE_URL_KEY = "url";
        String ARTICLE_IMG_KEY = "urlToImage";
        String ARTICLE_DATE_KEY = "publishedAt";
    }

    private String sourceId = null;
    private String sourceName = null;
    private String title = null;
    private String author = null;
    private String description = null;
    private String url = null;
    private String imageSrc = null;
    private Date date = null;

    NewsArticle(String title, long dateInMillis) {
        this.title = title;
        this.date = new Date(dateInMillis);
    }

    private NewsArticle(String title, Date date) {
        this.title = title;
        this.date = date;
    }

    public NewsArticle(String sourceId, String sourceName, String title, String author, String description, String url, String imageSrc, String date) throws ParseException {
        this.sourceId = sourceId;
        this.sourceName = sourceName;
        this.title = title;
        this.author = author;
        this.description = description;
        this.url = url;
        this.imageSrc = imageSrc;
        this.date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault()).parse(date);
    }

    public String getTitle() {
        return title;
    }

    public String getSourceId() {
        return sourceId;
    }

    public String getSourceName() {
        return sourceName;
    }

    public String getAuthor() {
        return author;
    }

    public String getDescription() {
        return description;
    }

    public String getUrl() {
        return url;
    }

    public String getImageSrc() {
        return imageSrc;
    }

    public String getDate() {
        return new SimpleDateFormat("dd-MMM-yyyy HH:MM", Locale.getDefault()).format(this.date);
    }

    public NewsArticle cloneArticle() {
        return new NewsArticle(this.title, this.date);
    }

    public static NewsArticle parseFromJson(JSONObject json) throws JSONException, ParseException {
        JSONObject jsonSource = json.getJSONObject(NewsArticleUtils.SOURCE_KEY);

        String sourceId = jsonSource.getString(NewsArticleUtils.SOURCE_ID_KEY);
        String sourceName = jsonSource.getString(NewsArticleUtils.SOURCE_NAME_KEY);
        String title = json.getString(NewsArticleUtils.ARTICLE_TITLE_KEY);
        String author = json.getString(NewsArticleUtils.ARTICLE_AUTHOR_KEY);
        String description = json.getString(NewsArticleUtils.ARTICLE_DESCRIPTION_KEY);
        String url = json.getString(NewsArticleUtils.ARTICLE_URL_KEY);
        String imgUrl = json.getString(NewsArticleUtils.ARTICLE_IMG_KEY);
        String publishedAt = json.getString(NewsArticleUtils.ARTICLE_DATE_KEY);
        return new NewsArticle(sourceId, sourceName, title, author, description, url, imgUrl, publishedAt);
    }
}
