package com.example.booklistingapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.renderscript.ScriptGroup;
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
import java.util.ArrayList;
import java.util.List;

public class QueryUtils {

    // Get name of this class
    private static final String LOG_TAG = QueryUtils.class.getSimpleName();

    // Private constructor
    private QueryUtils() {

    }

    public static List<Book> fetchBookData(String requestUrl) {
        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform http request to url and get JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making http request", e);
        }

        List<Book> books = extractFeatureFromJson(jsonResponse);

        // Return the list of books
        return books;
    }

    private static URL createUrl(String string Url) {

        URL url = null;

        // Try to create a URL object
        try {
            url = new URL(string Url);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem creating URL", e);
        }

        return url;
    }
    /**
     * Make an HTTP request to the given URL and return a JSON string
     */
    private static String makeHttpRequest (URL url) throws IOException {
        String jsonResponse = " ";

        // If url is null return early
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;

        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the Book JSON results", e);
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
     * Converts the inputStream into a String which contains
     * the whole JSON response from the server
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
     *
     * @param imageUrl String in the JSON response
     * @return the image (bitmap)
     */
    private static Bitmap getBookImage(String imageUrl) {
        URL url = createUrl(imageUrl);
        Bitmap bitmap = null;
        InputStream ins = null;

        try {
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setReadTimeout(10000);
            httpURLConnection.setConnectTimeout(15000);
            httpURLConnection.connect();

            if (httpURLConnection.getResponseCode() == 200) {
                ins = httpURLConnection.getInputStream();
                bitmap = BitmapFactory.decodeStream(ins);
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "An error occured while getting book image", e);
        }

        return bitmap;
    }


    /**
     * Return a list of {@link Book} objects that has been built up from
     * parsing the given JSON response.
     */
    private static List<Book> extractFeatureFromJson(String bookJSON) {
        // If the JSON string is empty or null, return early
        if (TextUtils.isEmpty(bookJSON)) {
            return null;
        }

        // Create an empty ArrayList that we can start adding books to
        List<Book> books = new ArrayList<>();

        // Try to parse the JSON response string. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {
            // Get a reference to the root object
            JSONObject baseJsonResponse = new JSONObject(bookJSON);

            // If the searched book doesn't exist, return early
            if (baseJsonResponse.getInt("totalItems") == 0) {
                return null;
            }

            // Get array named "items" which represent all the books in that array
            JSONArray itemsArray = baseJsonResponse.getJSONArray("items");

            for (int i = 0; i < itemsArray.length(); i++) {
                // Get the ith book in the array "items"
                JSONObject currentBook = itemsArray.getJSONObject(i);

                // Get volume info
                JSONObject volumeInfo = currentBook.getJSONObject("volumeInfo");

                // Get Book title
                String bookTitle = volumeInfo.optString("title");

                // Get published date.
                // If published date is not available returns empty String
                String publishedDate = volumeInfo.optString("publishedDate");

                // Get book description.
                // If description is not available returns empty String
                String bookDescription = volumeInfo.optString("description");

                // Get number of pages.
                // If pageCount is not available returns 0
                String pageCount = Integer.toString(volumeInfo.optInt("pageCount"));

                // Get imageLinks object
                // If imageLinks object is not available returns null
                JSONObject imageLinks = volumeInfo.optJSONObject("imageLinks");

                // Initialize thumbnail so that if there's no thumbnail
                // in the JSON response, we can add an empty string to the Book object
                String thumbnail = "";
                Bitmap bookImage = null;
                // Get image thumbnail
                if (imageLinks != null) {
                    thumbnail = imageLinks.getString("thumbnail");
                    bookImage = getBookImage(thumbnail);

                }

                // This empty string will be added to the object if the authors array is null
                String authorsString = "";

                // Get array containing all the authors.
                // If authors array is not available returns null
                JSONArray authors = volumeInfo.optJSONArray("authors");

                // Check that authors array exist in the JSON response
                if (authors != null) {
                    // Instantiate a StringBuilder to add all authours for each book
                    StringBuilder sb = new StringBuilder();

                    // Store the number of authors
                    int numberOfAuthors = authors.length();

                    // if there's only one author we just append it to the StringBuilder
                    // But if there's more than one, we add a comma and a space after their name
                    // To separate each other from the next one
                    if (numberOfAuthors == 1) {
                        sb.append(authors.get(0));
                    } else {
                        for (int j = 0; j < numberOfAuthors; j++) {
                            // Add a comma and space to all elements except the last one
                            if (j != numberOfAuthors - 1) {
                                sb.append(authors.get(j)).append(", ");
                            } else {
                                // Add last element in the array
                                sb.append(authors.get(j));
                            }

                        }
                    }

                    // Convert StringBuilder to String
                    authorsString = sb.toString();
                }

                // Create a new Book object using the constructor
                Book book = new Book(bookTitle, publishedDate, bookDescription, pageCount, bookImage, authorsString);

                // Add book to List of books
                books.add(book);

            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Problem parsing the JSON response", e);
        }

        // Return list of books
        return books;
    }
}
