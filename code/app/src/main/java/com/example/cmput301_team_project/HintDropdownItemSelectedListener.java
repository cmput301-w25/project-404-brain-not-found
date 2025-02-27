package com.example.cmput301_team_project;

import android.graphics.Color;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

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
