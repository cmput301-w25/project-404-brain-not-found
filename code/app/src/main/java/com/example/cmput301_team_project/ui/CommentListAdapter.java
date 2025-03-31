package com.example.cmput301_team_project.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.cmput301_team_project.R;
import com.example.cmput301_team_project.db.MoodDatabaseService;
import com.example.cmput301_team_project.model.Comment;

import java.util.List;

/**
 * A custom ArrayAdapter for displaying Comment objects in a ListView.
 *This adapter handles the comment data including the username of the commenter and the comment text.
 */
public class CommentListAdapter extends ArrayAdapter<Comment> {
    private final MoodDatabaseService moodDatabaseService;
    public CommentListAdapter(@NonNull Context context, @NonNull List<Comment> objects) {
        super(context, 0, objects);

        moodDatabaseService = MoodDatabaseService.getInstance();
    }

    /**
     * Gets a View that displays the data at the specified position in the database.
     *
     * @param position The position of the item within the adapter's data set
     * @param convertView The old view to reuse, if possible
     * @param parent The parent that this view will eventually be attached to
     * @return A View corresponding to the data at the specified position
     */
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

    /**
     * Updates the adapter's data with new comments and refreshes the view.
     *
     * @param items The new list of Comment objects to display
     */
    public void displayComments(List<Comment> items){
        clear();
        addAll(items);
        notifyDataSetChanged();
    }
}


