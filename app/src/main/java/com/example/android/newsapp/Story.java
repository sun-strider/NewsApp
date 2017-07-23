package com.example.android.newsapp;

import java.util.ArrayList;
import java.util.Date;

/**
 * An {@link Story} object contains information related to a single story.
 */
public class Story {

    /* Story Title */
    private String mTitle;

    /* Story Author */
    private ArrayList<String> mAuthors;

    /* Story Date */
    private Date mDate;

    /* Story Url */
    private String mUrl;

    /* Story Section */
    private String mSection;

    /* Story Image Url */
    private String mThumbnailUrl;

    /**
     * Constructs a new {@link Story} object.
     *
     * @param title    is the mTitle of the story
     * @param authors  are the authors of the story
     * @param date     is the date of the story
     * @param url      is the website URL to find out more about the story
     * @param section  is the section of the story
     * @param thumbnailUrl is the url of the story image
     */

    public Story(String title, ArrayList<String> authors, Date date, String url, String section, String thumbnailUrl) {
        this.mTitle = title;
        this.mAuthors = authors;
        this.mDate = date;
        this.mUrl = url;
        this.mSection = section;
        this.mThumbnailUrl = thumbnailUrl;
    }

    /* Getter Methods */
    public String getTitle() {
        return mTitle;
    }

    public Date getDate() {
        return mDate;
    }

    public String getUrl() {
        return mUrl;
    }

    public ArrayList<String> getAuthors() {
        return mAuthors;
    }

    public String getSection() {
        return mSection;
    }

    public String getThumnailUrl() {
        return mThumbnailUrl;
    }

    /* Helper method to return all authors in one string */
    public String getAuthorsAsString(){
        StringBuilder builder = new StringBuilder();
        if (mAuthors == null || mAuthors.isEmpty()) {
            return "";
        }

        for (String author : mAuthors){
            builder.append(author);
            builder.append(", ");
        }
        builder.delete(builder.length() - 2, builder.length() - 1);
        return builder.toString();
    }

}