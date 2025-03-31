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

public class UserListAdapter extends ArrayAdapter<PublicUser> {
    private final String buttonText;
    private final UserButtonActionEnum buttonAction;
    private final UserDatabaseService userDatabaseService;
    public UserListAdapter(@NonNull Context context, @NonNull List<PublicUser> objects, String buttonText, UserButtonActionEnum buttonAction) {
        super(context, 0, objects);
        this.buttonText = buttonText;
        this.buttonAction = buttonAction;
        userDatabaseService = UserDatabaseService.getInstance();
    }

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

    private void setButtonView(Button button, PublicUser user) {
        button.setText(getButtonText(user));
        boolean enabled = buttonAction != UserButtonActionEnum.FOLLOW || user.getFollowRelationshipWithCurrUser() == FollowRelationshipEnum.NONE;
        button.setEnabled(enabled);
    }

    private int getButtonText(PublicUser user) {
        return switch(buttonAction) {
            case FOLLOW -> getFollowText(user.getFollowRelationshipWithCurrUser());
            case REMOVE -> R.string.remove;
            case UNFOLLOW -> R.string.unfollow;
        };
    }

    private int getFollowText(FollowRelationshipEnum relationship) {
        if (relationship == FollowRelationshipEnum.NONE) {
            return R.string.follow;
        } else if (relationship == FollowRelationshipEnum.REQUESTED) {
            return R.string.requested;
        } else {
            return R.string.following;
        }
    }

    public void replaceItems(List<PublicUser> items) {
        clear();
        addAll(items);
        notifyDataSetChanged();
    }
}
