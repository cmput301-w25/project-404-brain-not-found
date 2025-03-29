package com.example.cmput301_team_project.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.cmput301_team_project.R;
import com.example.cmput301_team_project.db.FirebaseAuthenticationService;
import com.example.cmput301_team_project.db.UserDatabaseService;
import com.example.cmput301_team_project.enums.FollowRelationshipEnum;
import com.example.cmput301_team_project.enums.UserButtonActionEnum;
import com.example.cmput301_team_project.model.PublicUser;

import java.util.List;

/**
 * Adapter class for displaying a list of users with an associated action button.
 */
public class UserListAdapter extends ArrayAdapter<PublicUser> {
    private final String buttonText;
    private final UserButtonActionEnum buttonAction;
    private final UserDatabaseService userDatabaseService;

    /**
     * Constructor for UserListAdapter.
     *
     * @param context      The context in which the adapter is used.
     * @param objects      The list of users to display.
     * @param buttonText   The text to be displayed on the action button.
     * @param buttonAction The action to be performed when the button is clicked.
     */
    public UserListAdapter(@NonNull Context context, @NonNull List<PublicUser> objects, String buttonText, UserButtonActionEnum buttonAction) {
        super(context, 0, objects);
        this.buttonText = buttonText;
        this.buttonAction = buttonAction;
        userDatabaseService = UserDatabaseService.getInstance();
    }

    /**
     * Gets the view for each item in the list.
     *
     * @param position    The position of the item in the list.
     * @param convertView The recycled view to populate.
     * @param parent      The parent view group.
     * @return The populated view for the current list item.
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view;
        if(convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.user_list_content, parent, false);
        }
        else {
            view = convertView;
        }

        TextView username = view.findViewById(R.id.user_list_content_username);
        TextView name = view.findViewById(R.id.user_list_content_name);
        Button button = view.findViewById(R.id.user_list_content_button);

        PublicUser user = getItem(position);
        username.setText(user.getUsername());
        name.setText(user.getName());
        setButtonView(button, user);

        String currentUser = FirebaseAuthenticationService.getInstance().getCurrentUser();
        button.setOnClickListener(v -> {
            switch (buttonAction) {
                case FOLLOW -> userDatabaseService.requestFollow(currentUser, user.getUsername())
                        .addOnSuccessListener(vd -> {
                            user.setFollowRelationshipWithCurrUser(FollowRelationshipEnum.REQUESTED);
                            setButtonView(button, user);
                        });
                case UNFOLLOW -> userDatabaseService.removeFollow(currentUser, user.getUsername())
                        .addOnSuccessListener(vd -> { remove(user); notifyDataSetChanged(); });
                case REMOVE -> userDatabaseService.removeFollow(user.getUsername(), currentUser)
                        .addOnSuccessListener(vd -> { remove(user); notifyDataSetChanged(); });
            }
        });

        return view;
    }

    /**
     * Updates the button view based on the user's relationship status.
     *
     * @param button The button to update.
     * @param user   The user associated with the button.
     */
    private void setButtonView(Button button, PublicUser user) {
        button.setText(getButtonText(user));
        button.setEnabled(buttonAction != UserButtonActionEnum.FOLLOW || user.getFollowRelationshipWithCurrUser() == FollowRelationshipEnum.NONE);
    }

    /**
     * Determines the appropriate button text based on the user's follow relationship.
     *
     * @param user The user whose relationship status is being evaluated.
     * @return The resource ID for the appropriate button text.
     */
    private int getButtonText(PublicUser user) {
        return switch(buttonAction) {
            case FOLLOW -> getFollowText(user.getFollowRelationshipWithCurrUser());
            case REMOVE -> R.string.remove;
            case UNFOLLOW -> R.string.unfollow;
        };
    }


    /**
     * Returns the appropriate follow button text based on the relationship status.
     *
     * @param relationship The follow relationship status.
     * @return The resource ID for the appropriate follow text.
     */
    private int getFollowText(FollowRelationshipEnum relationship) {
        if (relationship == FollowRelationshipEnum.NONE) {
            return R.string.follow;
        } else if (relationship == FollowRelationshipEnum.REQUESTED) {
            return R.string.requested;
        } else {
            return R.string.following;
        }
    }

    /**
     * Replaces the current list of users with a new list and updates the UI.
     *
     * @param items The new list of users.
     */
    public void replaceItems(List<PublicUser> items) {
        clear();
        addAll(items);
        notifyDataSetChanged();
    }
}
