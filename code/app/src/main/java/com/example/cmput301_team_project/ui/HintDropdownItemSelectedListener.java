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
     * Called for when an item is selected in the dropdown. If the first item (hint) is selected
     * then the text colour is changed to gray.
     *
     * @param parent    The adapterView with the selection taking place.
     * @param view      The view within the AdapterView.
     * @param position  The position of the selected item.
     * @param id        The row Id of the selected item.
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(position == 0) {
            ((TextView)view).setTextColor(Color.GRAY);
        }
    }

    /**
     * Called when no item is selected
     *
     * @param parent The AdapterView with no current selection.
     */
    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
