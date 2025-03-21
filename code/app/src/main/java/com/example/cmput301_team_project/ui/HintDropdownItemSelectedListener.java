package com.example.cmput301_team_project.ui;

import android.graphics.Color;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

/**
 * A custom listener for handling item selections in dropdown (Spinner) menus.
 * This listener ensures that the hint (first item) is displayed in gray when selected.
 */
public class HintDropdownItemSelectedListener implements AdapterView.OnItemSelectedListener{
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(position == 0) {
            ((TextView)view).setTextColor(Color.GRAY);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
