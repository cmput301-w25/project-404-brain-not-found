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

    /**
     * Constructs a new MoodListAdapter.
     * @param context the current context.
     * @param moodList the list of Mood objects to display.
     */
    public MoodListAdapter(Context context, ArrayList<Mood> moodList) {
        super(context, 0, moodList);
        this.context = context;
        this.moodList = moodList;
        this.moodDatabaseService = MoodDatabaseService.getInstance(); // Get a singleton instance
    }

    /**
     * Method from MoodFormDialogListener, not used in this class but necessary for interface implementation.
     * @param mood the mood to potentially add, not used here.
     */
    @Override
    public void addMood(Mood mood){
        // Not implemented as adding mood is not handled here
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Inflate the layout if not already created
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.content, parent, false);
        }

        // Get the Mood item at the current position
        Mood mood = getItem(position);
        setupMoodItemView(convertView, mood, position);

        return convertView;
    }

    /**
     * Sets up the mood item view elements and assigns them values from the mood object.
     * @param view The view for the mood item.
     * @param mood The mood object to display.
     * @param position The position of the item in the list.
     */
    private void setupMoodItemView(View view, Mood mood, int position) {
        // Find views and set values
        TextView moodClass = view.findViewById(R.id.emotionName);
        TextView emoji = view.findViewById(R.id.moodEmoji);
        TextView moodDate = view.findViewById(R.id.dateAns);
        TextView socialSituation = view.findViewById(R.id.socialSituation);
        TextView triggerName = view.findViewById(R.id.triggerName);
        TextView moodTime = view.findViewById(R.id.timeAns);
        ImageView moodImage = view.findViewById(R.id.ImageBase64);
        ImageView menuButton = view.findViewById(R.id.mood_menu_button);

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
    }

    /**
     * Inflates a popup menu for mood item interactions such as edit and delete.
     * @param view The anchor view for the popup menu.
     * @param position The position of the item in the list for which the menu is being displayed.
     */
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

    /**
     * Handles the event to edit a mood.
     * @param mood The mood object to be edited.
     */
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

    /**
     * Handles the event to delete a mood from the list and database.
     * @param mood The mood object to be deleted.
     * @param position The position of the mood in the list.
     */
    private void deleteMoodEvent(Mood mood, int position) {
        // Remove from the local mood listview list
        moodList.remove(position);
        // Remove from database
        moodDatabaseService.deleteMood(mood);
        // Notify the adapter to refresh the list view
        notifyDataSetChanged();
    }
}
