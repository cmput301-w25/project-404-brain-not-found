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

public class ViewProfileFragment extends DialogFragment {


    public static ViewProfileFragment newInstance(String username, String name){
        Bundle args = new Bundle();
        args.putSerializable("username",username);
        args.putSerializable("name", name);
        ViewProfileFragment fragment = new ViewProfileFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState){
        UserDatabaseService udb = UserDatabaseService.getInstance();
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_profile, null);
        String name = (String) getArguments().getSerializable("name");
        String username = (String) getArguments().getSerializable("username");
        TextView publicName = view.findViewById(R.id.publicName);
        TextView publicUsername = view.findViewById(R.id.publicUsername);
        publicUsername.setText(username);
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
