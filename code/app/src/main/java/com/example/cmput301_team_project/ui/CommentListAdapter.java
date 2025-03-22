package com.example.cmput301_team_project.ui;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.cmput301_team_project.R;
import com.example.cmput301_team_project.db.MoodDatabaseService;
import com.example.cmput301_team_project.db.UserDatabaseService;
import com.example.cmput301_team_project.enums.UserButtonActionEnum;
import com.example.cmput301_team_project.model.Comment;
import com.example.cmput301_team_project.model.PublicUser;

import java.util.List;

public class CommentListAdapter extends ArrayAdapter<Comment> {
    private final MoodDatabaseService moodDatabaseService;
    public CommentListAdapter(@NonNull Context context, @NonNull List<Comment> objects) {
        super(context, 0, objects);

        moodDatabaseService = MoodDatabaseService.getInstance();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view;
        if(convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.comment_content, parent, false);
        }
        else {
            view = convertView;
        }
        Comment moodComment = getItem(position);
        String username = moodComment.getUsername();
        String text = moodComment.getText();
        TextView usernameText = view.findViewById(R.id.comment_username);
        TextView commentText = view.findViewById(R.id.comment_text);
        commentText.setText(text);
        usernameText.setText("@"+username);
        return view;

    }

    public void displayComments(List<Comment> items){
        clear();
        addAll(items);
        notifyDataSetChanged();
    }
}


