package com.example.booklistingapp;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.content.Loader;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class ResultsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Book>> {


    private static final int BOOK_LOADER_ID = 1;
    private boolean mFirstLoaderCreated = false;
    private BookAdapter mAdapter;
    private String mFinalUrl;
    private TextView mEmptyStateTextView;

    // Get a reference to the LoaderManager in order to interact with Loaders
    LoaderManager loaderManager = getLoaderManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.results_activity);

        // Get intent from BookActivity.java
        Intent getMainActivityIntent = getIntent();

        // Get formatted URL to make request from BookActivity.java
        mFinalUrl = getMainActivityIntent.getStringExtra("finalUrl");

        ListView booksList = (ListView) findViewById(R.id.list);

        mEmptyStateTextView = (TextView) findViewById(R.id.empty_view);
        booksList.setEmptyView(mEmptyStateTextView);

        mAdapter = new BookAdapter(this, new ArrayList<Book>());

        booksList.setAdapter(mAdapter);

        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get details on the currently active default data network
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();


        if (networkInfo != null && networkInfo.isConnected()) {
            // Initialize the loader. Pass in the int ID constant defined above and pass in null for
            // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
            // because this activity implements the LoaderCallbacks interface).
            loaderManager.initLoader(BOOK_LOADER_ID, null, ResultsActivity.this);
        } else {
            View loadingIndicator = findViewById(R.id.loading_indicator);
            loadingIndicator.setVisibility(View.GONE);

            mEmptyStateTextView.setText(R.string.no_internet_connection);
        }

//        loaderManager.restartLoader(BOOK_LOADER_ID, null, ResultsActivity.this);
    }


    @Override
    public Loader<List<Book>> onCreateLoader(int i, Bundle bundle) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        // Go and find in SharedPreferences file a string with the name "books_shown"
        // If it finds it, then it will return its value, otherwise it will return the value in
        // "settings_books_shown_default" which is 10
        String numberOfBooksShown = sharedPrefs.getString(getString(R.string.settings_books_shown_key),
                getString(R.string.settings_books_shown_default));
        Log.v("ResultActivity.java", "The value of numberOfBooksShown is: " + numberOfBooksShown);
        Uri baseUri = Uri.parse(mFinalUrl);
        Uri.Builder uriBuilder = baseUri.buildUpon();
        uriBuilder.appendQueryParameter("maxResults", numberOfBooksShown);


        return new BookLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<Book>> loader, List<Book> books) {

        // Remove the loading indicator once the books are loaded
        View loadingIndicator = findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.GONE);

        // If the book searched doesn't exist
        // Show 'Book wasn't found"
        if (books == null) {
            mEmptyStateTextView.setText(R.string.book_wasnt_found);
        }

        if (mAdapter != null) {
            mAdapter.clear();
        }

        if (books != null && !books.isEmpty()) {
            mAdapter.addAll(books);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Book>> loader) {
        Log.v("ResultsActivity.java", "This is onLoaderReset");
    }
}
