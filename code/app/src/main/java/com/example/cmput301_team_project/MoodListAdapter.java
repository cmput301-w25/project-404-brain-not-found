package com.example.cmput301_team_project;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;


/**
 * MoodListAdapter is a custom ArrayAdapter used to display a list of Mood objects in a ListView.
 * It inflates a custom layout for each mood and binds the mood details to the corresponding UI elements.
 */
public class MoodListAdapter extends ArrayAdapter<Mood> implements MoodFormFragment.MoodFormDialogListener {

    private Context context;
    private ArrayList<Mood> moodList;
    private MoodDatabaseService moodDatabaseService; // Reference to the database service

    public MoodListAdapter(Context context, ArrayList<Mood> moodList) {
        super(context, 0, moodList);
        this.context = context;
        this.moodList = moodList;
        this.moodDatabaseService = MoodDatabaseService.getInstance(); // Get a singleton instance
    }

    /**Don't want to use the addMood method in this class.
     * Just need to implement the MoodFormDialogListener interface to be able
     * to show Edit Mood Event dialog fragment.*/
    @Override
    public void addMood(Mood mood){
        return;
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
        ImageView menuButton = convertView.findViewById(R.id.mood_menu_button);

        if (mood != null) {
            moodClass.setText(mood.getDisplayName().toString());
            emoji.setText(mood.getEmoji());
            moodDate.setText(mood.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().toString());
            socialSituation.setText(mood.getSocialSituation().toString());
            triggerName.setText(mood.getTrigger());
            moodTime.setText(mood.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm")).toString());
            moodImage.setImageBitmap(ImageUtils.decodeBase64(mood.getImageBase64()));

            // If the three-dot menu icon is clicked, a pop-up menu shows up
            menuButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openPopupMenu(v, position);
                }
            });
        }

        return convertView;
    }

    // Inflates the popup mood menu as defined in res/menu/mood_menu.xml
    private void openPopupMenu(View view, int position){
        PopupMenu popup = new PopupMenu(getContext(), view);
        popup.getMenuInflater().inflate(R.menu.mood_menu, popup.getMenu());

        // Describes what happens when the edit or delete option in the menu is clicked
        popup.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            if (id == R.id.edit) {
                editMoodEvent(getItem(position));
                return true;
            } else if (id == R.id.delete) {
                deleteMoodEvent(getItem(position), position);
                return true;
            }
            return false;
        });
        popup.show();
    }

    public void editMoodEvent(Mood mood) {
        MoodFormFragment editFragment = MoodFormFragment.newInstance(moodList);
        editFragment.populateFields(mood);
        if (context instanceof FragmentActivity) {
            FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
            editFragment.show(fragmentManager, "Edit Mood Event");
        } else {
            Log.e("MoodListAdapter", "Context is not an instance of FragmentActivity");
        }
    }

    private void deleteMoodEvent(Mood mood, int position) {
        // Remove from the local mood listview list
        moodList.remove(position);
        // Remove from database
        moodDatabaseService.deleteMood(mood);
        // Notify the adapter to refresh the list view
        notifyDataSetChanged();
    }
}
