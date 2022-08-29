package com.example.booklistingapp;

import android.preference.EditTextPreference;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.text.InputFilter;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
    }

    public static class BookPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings_main);

            // Only allow the user to input values from 1 to 40
            // in the EditTextPreference for the number of books shown
            EditText editText1 = ((EditTextPreference) findPreference(getString(R.string.settings_books_shown_key)))
                    .getEditText();
            editText1.setFilters(new InputFilter[]{ new MinMaxFilter(1, 40) });
        }
    }
}
