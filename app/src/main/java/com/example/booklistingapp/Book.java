package com.example.booklistingapp;

import android.graphics.Bitmap;

public class Book {

    private String mTitle;
    private String mPublishedDate;
    private String mDescription;
    private String mPageCount;
    private Bitmap mBookImage;
    private String mAuthors;

    public Book(String title, String publishedDate, String description, String pageCount, Bitmap bookImage, String authors) {
        mTitle = title;
        mPublishedDate = publishedDate;
        mDescription = description;
        mPageCount = pageCount;
        mBookImage = bookImage;
        mAuthors = authors;
    }

    /**
     * @return Book title
     */
    public String getBookTitle() {
        return mTitle;
    }

    /**
     * @return Book PublishedDate
     */
    public String getBookPublishedDate() {
        return mPublishedDate;
    }

    /**
     * @return Book Description
     */
    public String getBookDescription() {
        return mDescription;
    }

    /**
     * @return Book PageCount
     */
    public String getBookPageCount() {
        return mPageCount;
    }

    /**
     * @return Book thumbnail
     */
    public Bitmap getBookThumbnail() {
        return mBookImage;
    }

    /**
     * @return Book authors
     */
    public String getBookAuthors() {
        return mAuthors;
    }
}
