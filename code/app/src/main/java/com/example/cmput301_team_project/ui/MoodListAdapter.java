package com.example.cmput301_team_project.ui;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.example.cmput301_team_project.R;
import com.example.cmput301_team_project.db.MoodDatabaseService;
import com.example.cmput301_team_project.db.UserDatabaseService;
import com.example.cmput301_team_project.enums.MoodSocialSituationEnum;
import com.example.cmput301_team_project.model.Mood;
import com.example.cmput301_team_project.utils.ImageUtils;
import com.google.android.material.button.MaterialButton;


/**
 * MoodListAdapter is a custom ArrayAdapter used to display a list of Mood objects in a ListView.
 * It inflates a custom layout for each mood and binds the mood details to the corresponding UI elements.
 */
public class MoodListAdapter extends ArrayAdapter<Mood> {

    private Context context;
    private ArrayList<Mood> moodList;
    private MoodDatabaseService moodDatabaseService; // Reference to the database service
    private UserDatabaseService userDatabaseService;
    private Fragment parentFragment;
    private boolean isOwned;

    interface CommentButtonListener {
        void onCommentButtonClicked(int position);
    }
    private CommentButtonListener commentButtonListener;

    /**
     * Constructs a new MoodListAdapter.
     * @param context the current context.
     * @param moodList the list of Mood objects to display.
     */
    public MoodListAdapter(Context context, ArrayList<Mood> moodList, Fragment parentFragment, boolean isOwned) {
        super(context, 0, moodList);
        this.context = context;
        this.moodList = moodList;
        this.moodDatabaseService = MoodDatabaseService.getInstance(); // Get a singleton instance
        this.userDatabaseService = UserDatabaseService.getInstance();
        this.parentFragment = parentFragment;
        this.isOwned = isOwned;

        if(!(parentFragment instanceof CommentButtonListener)) {
            throw new IllegalArgumentException("parentFragment is not an instance of CommentButtonListener");
        }
        commentButtonListener = (CommentButtonListener) parentFragment;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
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
        TextView moodPrefix = view.findViewById(R.id.mood_prefix);
        TextView moodClass = view.findViewById(R.id.emotionName);
        TextView emoji = view.findViewById(R.id.moodEmoji);
        TextView moodDate = view.findViewById(R.id.dateAns);
        TextView socialSituation = view.findViewById(R.id.socialSituation);
        TextView triggerName = view.findViewById(R.id.triggerName);
        TextView moodTime = view.findViewById(R.id.timeAns);
        ImageView moodImage = view.findViewById(R.id.ImageBase64);
        androidx.cardview.widget.CardView cardView = view.findViewById(R.id.cardView);

        MaterialButton commentButton = view.findViewById(R.id.comments_button);
        commentButton.setOnClickListener(v -> commentButtonListener.onCommentButtonClicked(position));

        ImageView menuButton = null;
        if(isOwned) {
            ViewStub menuStub = view.findViewById(R.id.edit_delete_stub);
            if(menuStub == null) {
                menuButton = view.findViewById(R.id.mood_menu_button);
            }
            else {
                menuButton = (ImageView) menuStub.inflate();
            }
        }

        if (mood != null) {
            if(isOwned) {
                moodPrefix.setText(R.string.i_am);
            }
            else {
                moodPrefix.setText(String.format("@%s %s", mood.getAuthor(), getContext().getString(R.string.is)));
            }

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
            SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm");
            moodClass.setText(mood.getDisplayName());
            emoji.setText(mood.getEmoji());
            moodDate.setText(dateFormat.format(mood.getDate()));
            if (mood.getSocialSituation() != MoodSocialSituationEnum.NONE){
                socialSituation.setText(mood.getSocialSituation().getDropdownDisplayName(context).toLowerCase());
            }
            triggerName.setText(mood.getTrigger());
            moodTime.setText(timeFormat.format(mood.getDate()));
            moodImage.setImageBitmap(ImageUtils.decodeBase64(mood.getImageBase64()));

            cardView.setCardBackgroundColor(getContext().getResources().getColor(mood.getColour(), getContext().getTheme()));

            if(menuButton != null) {
                // If the three-dot menu icon is clicked, a pop-up menu shows up
                menuButton.setOnClickListener(v -> openPopupMenu(v, position));
            }
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
        MoodFormFragment editFragment = MoodFormFragment.newInstance(mood);
        editFragment.populateFields(mood);
        if (context instanceof FragmentActivity) {
            FragmentManager fragmentManager = parentFragment.getChildFragmentManager();
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
        userDatabaseService.deleteMentions(mood.getId());
        // Notify the adapter to refresh the list view
        notifyDataSetChanged();
    }
}
