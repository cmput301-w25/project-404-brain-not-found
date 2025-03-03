package com.example.cmput301_team_project;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.ArrayList;

/**
 * MoodListAdapter is a custom ArrayAdapter used to display a list of Mood objects in a ListView.
 * It inflates a custom layout for each mood and binds the mood details to the corresponding UI elements.
 */
public class MoodListAdapter extends ArrayAdapter<Mood> {

    private Context context;
    private ArrayList<Mood> moodList;

    public MoodListAdapter(Context context, ArrayList<Mood> moodList) {
        super(context, 0, moodList);
        this.context = context;
        this.moodList = moodList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Inflate the layout if not already created
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.content, parent, false);
        }

        // Get the Mood item at the current position
        Mood mood = getItem(position);

        // Find views and set values
        TextView moodClass = convertView.findViewById(R.id.Mood_class);
        TextView emoji = convertView.findViewById(R.id.emoji);
        TextView moodDate = convertView.findViewById(R.id.mood_date);
        //TextView socialSituation = convertView.findViewById(R.id.social_situation);

        if (mood != null) {
            moodClass.setText(mood.getEmotion().toString());
            emoji.setText(mood.getEmoji());
            moodDate.setText(mood.getDate().toString());
            //socialSituation.setText(mood.getSocialSituation().toString());
        }

        return convertView;
    }
}
