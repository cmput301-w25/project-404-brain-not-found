package com.example.cmput301_team_project.ui;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * This class implements a custom array adapter
 * that disables and grays out the first item of a dropdown
 * in order for it to be a hint.
 */
public class HintDropdownAdapter extends ArrayAdapter<String> {
    private static final int HINT_COLOR = Color.GRAY;
    private static final int SELECTED_COLOR = Color.BLACK;
    private static final int ERROR_COLOR = Color.RED;

    public HintDropdownAdapter(Context context, ArrayList<HintDropdownEnumInterface> items) {
        super(context, android.R.layout.simple_spinner_dropdown_item, mapDropdownItems(context, items));
    }

    private static ArrayList<String> mapDropdownItems(Context context, ArrayList<HintDropdownEnumInterface> items) {
        return items
                .stream()
                .map(item -> item.getDropdownDisplayName(context))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public boolean isEnabled(int position) {
        // Disable the first item (hint)
        return position != 0;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = super.getDropDownView(position, convertView, parent);
        TextView tv = (TextView) view;

        if (position == 0) {
            // First item (hint) is grayed out
            tv.setTextColor(HINT_COLOR);
        } else {
            // Other items are black
            tv.setTextColor(SELECTED_COLOR);
        }

        return view;
    }

    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = super.getView(position, convertView, parent);
        TextView tv = (TextView) view;

        if (position == 0) {
            // Hint color for unselected state
            tv.setTextColor(HINT_COLOR);
        } else {
            // Black color for selected item
            tv.setTextColor(SELECTED_COLOR);
        }

        return view;
    }

    /**
     * Set an error state for the spinner
     * @param v The view to set error on
     * @param s The error message
     */
    public void setError(View v, CharSequence s) {
        TextView textView = (TextView) v;
        textView.setError("");
        textView.setTextColor(ERROR_COLOR);
        textView.setText(s);
    }

    /**
     * Clear any previous error state
     * @param v The view to clear error from
     */
    public void clearError(View v) {
        TextView textView = (TextView) v;
        textView.setError(null);
        textView.setTextColor(SELECTED_COLOR);
    }
}