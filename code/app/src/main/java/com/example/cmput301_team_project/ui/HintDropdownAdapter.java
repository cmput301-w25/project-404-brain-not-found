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
 * This  class implements a custom array adapter
 * that disables and grays out the first item of a dropdown
 * in order for it to be a hint.
 */
public class HintDropdownAdapter extends ArrayAdapter<String> {
    /**
     * Constructs a new HintDropdownAdapter.
     *
     * @param context The current context
     * @param items List of enum items implementing HintDropdownEnumInterface
     */
    public HintDropdownAdapter(Context context, ArrayList<HintDropdownEnumInterface> items) {
        super(context, android.R.layout.simple_spinner_dropdown_item, mapDropdownItems(context, items));
    }

    private static ArrayList<String> mapDropdownItems(Context context, ArrayList<HintDropdownEnumInterface> items) {
        return items
                .stream()
                .map(item -> item.getDropdownDisplayName(context))
                .collect(Collectors.toCollection(ArrayList::new));
    }
    /**
     * Determines whether the item at the specified position is selectable.
     * The first item is disabled to serve as a hint.
     *
     * @param position Position of the item to check
     * @return false if position is 0 (first item), true otherwise
     */
    @Override
    public boolean isEnabled(int position) {
        return position != 0;
    }

    /**
     * Gets a dropdown view that displays the data at the specified position.
     *
     * @param position The position of the item within the adapter's data set
     * @param convertView The old view to reuse, if possible
     * @param parent The parent that this view will eventually be attached to
     * @return A View corresponding to the data at the specified position
     */
    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = super.getDropDownView(position, convertView, parent);
        TextView tv = (TextView) view;
        if (position == 0) {
            tv.setTextColor(Color.GRAY);
        } else {
            tv.setTextColor(Color.BLACK);
        }
        return view;
    }
    /**
     * Displays an error message in red text on the specified view.
     *
     * @param v The view to display the error on
     * @param s The error message to display
     */
    public void setError(View v, CharSequence s) {
        TextView textView = (TextView) v;
        textView.setError("");
        textView.setTextColor(Color.RED);
        textView.setText(s);
    }
}