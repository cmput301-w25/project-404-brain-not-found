package com.example.cmput301_team_project.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.cmput301_team_project.R;
import com.example.cmput301_team_project.db.FirebaseAuthenticationService;
import com.example.cmput301_team_project.db.MoodDatabaseService;
import com.example.cmput301_team_project.db.UserDatabaseService;
import com.example.cmput301_team_project.model.Comment;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.auth.User;

import java.util.ArrayList;
import java.util.List;

public class CommentListFragment extends DialogFragment {
    MoodDatabaseService moodDatabaseService = MoodDatabaseService.getInstance();
    UserDatabaseService userDatabaseService = UserDatabaseService.getInstance();
    private CommentListAdapter commentAdapter;

    FirebaseAuthenticationService firebaseAuthenticationService = FirebaseAuthenticationService.getInstance();

    public CommentListFragment(){
    }

    public static CommentListFragment newInstance(String id){
        Bundle args = new Bundle();
        args.putSerializable("id", id);
        CommentListFragment fragment = new CommentListFragment();
        fragment.setArguments(args);
        return fragment;
    }


    public void parseComment(String comment, String moodId){
        String[] commentArray = comment.split(" ");
        for (String i:commentArray){
            if (i.charAt(0) == '@'){
                String mentionedUser = i.substring(1);
                userDatabaseService.addMention(moodId, mentionedUser);
            }
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = getLayoutInflater().inflate(R.layout.fragment_comment_list, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        AlertDialog dialog = builder
                .setView(view)
                .setNegativeButton("Done", null)
                .create();
        String moodId = (String) getArguments().getSerializable("id");

        ListView commentList = view.findViewById(R.id.commentList);
        commentAdapter = new CommentListAdapter(requireContext(), new ArrayList<>());
        commentList.setAdapter(commentAdapter);

        Task<List<Comment>> commentTask = moodDatabaseService.getComments(moodId);
        commentTask.addOnSuccessListener(comments -> commentAdapter.displayComments(comments))
                .addOnFailureListener(comments -> commentAdapter.clear());

        TextInputLayout commentTextLayout = view.findViewById(R.id.addCommentLayout);
        EditText commentTextBox = commentTextLayout.getEditText();
        dialog.setOnShowListener(dialog1 -> {
            Button commentButton = dialog.findViewById(R.id.addCommentButton);

            commentButton.setOnClickListener(v -> {
                String commentText = commentTextBox.getText().toString();
                Comment newComment = new Comment(firebaseAuthenticationService.getCurrentUser(), commentText);
                parseComment(commentText, moodId);
                moodDatabaseService.addComment(moodId, newComment).addOnSuccessListener(d -> {
                    moodDatabaseService.getComments(moodId).addOnSuccessListener(comments -> {
                        commentAdapter.clear(); //
                        commentAdapter.addAll(comments);
                        commentAdapter.notifyDataSetChanged();
                    });
                });

                commentTextBox.setText(null);

            });
        });
        return dialog;
    }

}
