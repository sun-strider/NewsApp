/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.newsapp;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Helper methods related to requesting and receiving earthquake data from USGS.
 */
public final class QueryUtils {

    /**
     * Tag for the log messages
     */
    private static final String LOG_TAG = QueryUtils.class.getSimpleName();

    /**
     * Status when JSON response is ok
     */
    private static final String JSON_STATUS_OK = "ok";

    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private QueryUtils() {
    }

    /**
     * Query the USGS dataset and return a list of {@link Story} objects.
     */
    public static List<Story> fetchStoryData(String requestUrl) {
        Log.e("Utils", "after call of fetchStoryData");
        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        // Extract relevant fields from the JSON response and create a list of {@link Story}s
        List<Story> stories = extractStoryFeatureFromJson(jsonResponse);

        // Return the list of {@link Story}s
        return stories;
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the earthquake JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // Closing the input stream could throw an IOException, which is why
                // the makeHttpRequest(URL url) method signature specifies than an IOException
                // could be thrown.
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }


    /**
     * Return a list of {@link Story} objects that has been built up from
     * parsing the given JSON response.
     */
    private static List<Story> extractStoryFeatureFromJson(String storyJSON) {
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(storyJSON)) {
            return null;
        }

        // Create an empty ArrayList that we can start adding stories to
        ArrayList<Story> stories = new ArrayList<>();

        // Try to parse the JSON response string. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {

            // Create a JSONObject from the JSON response string
            JSONObject baseJsonResponse = new JSONObject(storyJSON).getJSONObject("response");

            // Check if json response is ok
            if (!baseJsonResponse.getString("status").equals(JSON_STATUS_OK)) {
                Log.e(LOG_TAG, "Bad status of json response");
                return stories;
            }

            // Extract the JSONArray associated with the key called "results",
            // which represents an array of news stories.
            JSONArray storyArray = baseJsonResponse.getJSONArray("results");

            // For each story in the storyArray, create an {@link Story} object
            for (int i = 0; i < storyArray.length(); i++) {

                // Get a single story at position i within the list of stories
                JSONObject currentStory = storyArray.getJSONObject(i);

                //Extract the story title
                String title = currentStory.getString("webTitle");

                //Extract the story date
                String publicationDate = currentStory.getString("webPublicationDate");
                Date date = null;
                try {
                    date  = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").parse(publicationDate);
                }
                catch (Exception e){
                    Log.e(LOG_TAG, "Error getting publication date: " + e);
                }

                //Extract the story's url
                String storyUrl = currentStory.getString("webUrl");

                //Extract the story's section
                String section = currentStory.getString("sectionName");

                //Extract the story's image
                String thumbnailUrl = null;
                if (currentStory.has("fields")) {
                    JSONObject fields = currentStory.getJSONObject("fields");
                    if (fields.has("thumbnail"))
                        thumbnailUrl = fields.getString("thumbnail");
                }

                // Extract author names
                ArrayList<String> authors = new ArrayList<>();
                if (currentStory.has("tags")){
                    JSONArray tags = currentStory.getJSONArray("tags");
                    JSONObject tag;
                    for (int j = 0; j < tags.length(); j++){
                        tag = tags.getJSONObject(j);
                        if (tag.has("webTitle"))
                            authors.add(tag.getString("webTitle"));
                    }
                }


                // Create a new {@link Story} object
                Story story = new Story(title, authors, date, storyUrl, section, thumbnailUrl);

                // Add the new {@link Story} to the list of stories.
                stories.add(story);
            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e(LOG_TAG, "Problem parsing the story JSON results", e);
        }

        // Return the list of stories
        return stories;
    }


    // This is the Earthquake extract method. To be deleted when switched to Story
    /**
     * Return a list of {@link Story} objects that has been built up from
     * parsing the given JSON response.
     */
    private static List<Story> extractFeatureFromJson(String earthquakeJSON) {
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(earthquakeJSON)) {
            return null;
        }

        // Create an empty ArrayList that we can start adding stories to
        List<Story> stories = new ArrayList<>();

        // Try to parse the JSON response string. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {

            // Create a JSONObject from the JSON response string
            JSONObject baseJsonResponse = new JSONObject(earthquakeJSON);

            // Extract the JSONArray associated with the key called "features",
            // which represents a list of features (or stories).
            JSONArray earthquakeArray = baseJsonResponse.getJSONArray("features");

            // For each earthquake in the earthquakeArray, create an {@link Story} object
            for (int i = 0; i < earthquakeArray.length(); i++) {

                // Get a single story at position i within the list of stories
                JSONObject currentEarthquake = earthquakeArray.getJSONObject(i);

                // For a given story, extract the JSONObject associated with the
                // key called "properties", which represents a list of all properties
                // for that story.
                JSONObject properties = currentEarthquake.getJSONObject("properties");

                // Extract the value for the key called "mag"
                double magnitude = properties.getDouble("mag");

                // Extract the value for the key called "place"
                String location = properties.getString("place");

                // Extract the value for the key called "time"
                long time = properties.getLong("time");

                // Extract the value for the key called "url"
                String url = properties.getString("url");

                // Create a new {@link Story} object with the magnitude, location, time,
                // and url from the JSON response.
                Story story = new Story(magnitude, location, time, url);

                // Add the new {@link Story} to the list of stories.
                stories.add(story);
            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the earthquake JSON results", e);
        }

        // Return the list of stories
        return stories;
    }

}