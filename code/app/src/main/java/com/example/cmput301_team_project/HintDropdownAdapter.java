package com.example.cmput301_team_project;

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

    public HintDropdownAdapter(Context context, ArrayList<HintDropdownEnumInterface> items) {
        super(context, android.R.layout.simple_spinner_dropdown_item, mapDropdownItems(context, items));
    }

    private static ArrayList<String> mapDropdownItems(Context context, ArrayList<HintDropdownEnumInterface> items) {
        return items
                .stream()
                .map(item -> item.getDisplayName(context))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public boolean isEnabled(int position) {
        return position != 0;
    }

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

    public void setError(View v, CharSequence s) {
        TextView textView = (TextView) v;
        textView.setError("");
        textView.setTextColor(Color.RED);
        textView.setText(s);
    }
}