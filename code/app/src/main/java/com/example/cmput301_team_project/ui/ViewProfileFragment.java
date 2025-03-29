package com.example.cmput301_team_project.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.cmput301_team_project.R;
import com.example.cmput301_team_project.db.MoodDatabaseService;
import com.example.cmput301_team_project.db.UserDatabaseService;
import com.example.cmput301_team_project.enums.MoodEmotionEnum;
import com.example.cmput301_team_project.model.Mood;

/**
 * A DialogFragment that displays the user's profile with their name, username, follower count,
 * following count, and most recent mood emoji.
 */
public class ViewProfileFragment extends DialogFragment {

    /**
     * Factory method to create a new instance of ViewProfileFragment.
     *
     * @param username The username of the user whose profile is being viewed.
     * @param name The name of the user whose profile is being viewed.
     * @return A new instance of ViewProfileFragment.
     */
    public static ViewProfileFragment newInstance(String username, String name){
        Bundle args = new Bundle();
        args.putSerializable("username",username);
        args.putSerializable("name", name);
        ViewProfileFragment fragment = new ViewProfileFragment();
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Called when the fragment is created. It inflates the view and sets up the profile information.
     *
     * @param savedInstanceState A bundle containing any saved state from a previous instance of the fragment.
     * @return A dialog with the user's profile information.
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState){
        UserDatabaseService udb = UserDatabaseService.getInstance();
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_profile, null);
        String name = (String) getArguments().getSerializable("name");
        String username = (String) getArguments().getSerializable("username");
        TextView publicName = view.findViewById(R.id.publicName);
        TextView publicUsername = view.findViewById(R.id.publicUsername);
        publicUsername.setText("@"+username);
        publicName.setText(name);

        TextView followerCount = view.findViewById(R.id.followerCount);
        udb.followerCount(username).addOnSuccessListener(count -> {
            followerCount.setText(String.valueOf(count));
        });
        TextView followingCount = view.findViewById(R.id.followingCount);
        udb.followingCount(username).addOnSuccessListener(count -> {
            followingCount.setText(String.valueOf(count));
        });
        TextView emoji = view.findViewById(R.id.recentEmoji);
        MoodDatabaseService mdb = MoodDatabaseService.getInstance();
        mdb.getMostRecentMood(username).addOnSuccessListener(emotion->{
            if (emotion != null){
                Mood tempMood = Mood.createMood(MoodEmotionEnum.valueOf(emotion), null, null, false, null, null, null, null);
                emoji.setText(tempMood.getEmoji());
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        return builder
                .setView(view)
                .setNegativeButton("Close", null)
                .create();
    }
}
