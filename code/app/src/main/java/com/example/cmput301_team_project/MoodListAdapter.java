package com.example.cmput301_team_project;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;


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
        TextView moodClass = convertView.findViewById(R.id.emotionName);
        TextView emoji = convertView.findViewById(R.id.moodEmoji);
        TextView moodDate = convertView.findViewById(R.id.dateAns);
        TextView socialSituation = convertView.findViewById(R.id.socialSituation);
        TextView triggerName = convertView.findViewById(R.id.triggerName);
        TextView moodTime = convertView.findViewById(R.id.timeAns);
        ImageView moodImage = convertView.findViewById(R.id.ImageBase64);


        if (mood != null) {
            moodClass.setText(mood.getDisplayName());
            emoji.setText(mood.getEmoji());
            moodDate.setText(mood.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().toString());
            socialSituation.setText(mood.getSocialSituation().toString());
            triggerName.setText(mood.getTrigger());
            moodTime.setText(mood.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm")).toString());
            moodImage.setImageBitmap(decodeBase64(mood.getImageBase64()));
        }

        return convertView;
    }
    private Bitmap decodeBase64(String base64Str) {
        byte[] decodedBytes = Base64.decode(base64Str, Base64.NO_WRAP);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }
}
