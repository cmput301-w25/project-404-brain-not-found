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
    /**
     * Called when an item is selected.
     * If it's the first item (position 0), makes its text gray.
     *
     * @param parent The dropdown list
     * @param view The selected item view
     * @param position Item position in list
     * @param id Item ID
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(position == 0) {
            ((TextView)view).setTextColor(Color.GRAY);
        }
    }
    /**
     * Called when nothing is selected.
     *
     * @param parent The dropdown list
     */
    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
