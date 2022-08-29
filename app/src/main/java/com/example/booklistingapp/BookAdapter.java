package com.example.booklistingapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class BookAdapter extends ArrayAdapter<Book> {

    /**
     *  Constructor
     * @param context of the app
     * @param books is the list of books which is the data source of the adapter
     */
    public BookAdapter(Context context, List<Book> books) {
        super(context, 0, books);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;

        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item, parent, false);
        }

        Book currentBook = getItem(position);

        // Set book title
        TextView bookTitle = (TextView) listItemView.findViewById(R.id.book_title);
        bookTitle.setText(currentBook.getBookTitle());

        // Set book description
        TextView bookDescription = (TextView) listItemView.findViewById(R.id.book_description);
        bookDescription.setText(getBookInfoText(currentBook.getBookDescription(), "description"));

        // Set some colors for labels "Authors", "Pages" and "Published"
        String authors = "<font color='#424242'>Authors</font><br>";
        String pages = "<font color='#424242'>Pages</font><br>";
        String published = "<font color='#424242'>Published</font><br>";

        // Set book Authors
        TextView bookAuthors = (TextView) listItemView.findViewById(R.id.book_authors);
        bookAuthors.setText(Html.fromHtml(authors +  getBookInfoText(currentBook.getBookAuthors(), "authors")));

        // Set book page count
        TextView bookPageCount = (TextView) listItemView.findViewById(R.id.book_page_count);
        bookPageCount.setText(Html.fromHtml(pages + getBookInfoText(currentBook.getBookPageCount(), "pageCount")));

        // Set book published date
        TextView bookPublishedDate = (TextView) listItemView.findViewById(R.id.book_published_date);
        bookPublishedDate.setText(Html.fromHtml(published + getBookInfoText(currentBook.getBookPublishedDate(), "published")));

        // Set book image
        ImageView bookImage = (ImageView) listItemView.findViewById(R.id.book_thumbnail);

        // If no image was provided in the JSON response
        // Use an "Image not available" image
        if (currentBook.getBookThumbnail() != null) {
            bookImage.setImageBitmap(currentBook.getBookThumbnail());
        } else {
            bookImage.setImageResource(R.drawable.image_not_available);
        }

        return listItemView;
    }

    /**
     * @param textToAdd is the text will be setting to the TextView
     */
    private String getBookInfoText(String textToAdd, String typeOfText) {
        if (textToAdd.isEmpty() || textToAdd.equals("0")) {

            if (typeOfText == "description") {
                return "Description not available";
            }

            return getContext().getString(R.string.not_available);
        } else {
            return textToAdd;
        }
    }
}
