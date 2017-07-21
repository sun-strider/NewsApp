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
    private String mImageUrl;

    /**
     * Constructs a new {@link Story} object.
     *
     * @param title    is the mTitle of the story
     * @param authors  are the authors of the story
     * @param date     is the date of the story
     * @param url      is the website URL to find out more about the story
     * @param section  is the section of the story
     * @param imageUrl is the url of the story image
     */

    public Story(String title, ArrayList<String> authors, Date date, String url, String section, String imageUrl) {
        this.mTitle = title;
        this.mAuthors = authors;
        this.mDate = date;
        this.mUrl = url;
        this.mSection = section;
        this.mImageUrl = imageUrl;
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

    public String getImageUrl() {
        return mImageUrl;
    }

    /* Helper method to return all authors in one string */
    public String getAuthorsAsString(){
        StringBuilder builder = new StringBuilder();
        if (mAuthors == null && mAuthors.isEmpty()) {
            return "";
        }

        for (String author : mAuthors){
            builder.append(author);
            builder.append(", ");
        }
        builder.delete(builder.length() - 2, builder.length() - 1);
        return builder.toString();
    }




    // Here is the old code from the earthquake app. To delete when done

    /**
     * Magnitude of the earthquake
     */
    private double mMagnitude;

    /**
     * Location of the earthquake
     */
    private String mLocation;

    /**
     * Time of the earthquake
     */
    private long mTimeInMilliseconds;

    /**
     * Website URL of the earthquake
     *     private String mUrl;
     */

    /**
     * Constructs a new {@link Story} object.
     *
     * @param magnitude          is the magnitude (size) of the earthquake
     * @param location           is the location where the earthquake happened
     * @param timeInMilliseconds is the time in milliseconds (from the Epoch) when the
     *                           earthquake happened
     * @param url                is the website URL to find more details about the earthquake
     */
    public Story(double magnitude, String location, long timeInMilliseconds, String url) {
        mMagnitude = magnitude;
        mLocation = location;
        mTimeInMilliseconds = timeInMilliseconds;
        mUrl = url;
    }

    /**
     * Returns the magnitude of the earthquake.
     */
    public double getMagnitude() {
        return mMagnitude;
    }

    /**
     * Returns the location of the earthquake.
     */
    public String getLocation() {
        return mLocation;
    }

    /**
     * Returns the time of the earthquake.
     */
    public long getTimeInMilliseconds() {
        return mTimeInMilliseconds;
    }

    /**
     * Returns the website URL to find more information about the earthquake.
     */
    public String getEUrl() {
        return mUrl;
    }
}