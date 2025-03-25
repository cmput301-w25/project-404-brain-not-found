package com.example.cmput301_team_project.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.example.cmput301_team_project.R;
import com.example.cmput301_team_project.db.FirebaseAuthenticationService;
import com.example.cmput301_team_project.db.MoodDatabaseService;
import com.example.cmput301_team_project.model.Comment;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;

public class CommentListFragment extends DialogFragment {
    MoodDatabaseService moodDatabaseService = MoodDatabaseService.getInstance();
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


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = getLayoutInflater().inflate(R.layout.fragment_comment_list, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        AlertDialog dialog = builder
                .setView(view)
                .setNegativeButton("Done", null)
                .create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(requireContext(), R.color.background)));
        String moodId = (String) getArguments().getSerializable("id");


        ListView commentList = view.findViewById(R.id.commentList);
        commentAdapter = new CommentListAdapter(requireContext(), new ArrayList<>());
        commentList.setAdapter(commentAdapter);

        Task<List<Comment>> commentTask = moodDatabaseService.getComments(moodId);
        commentTask.addOnSuccessListener(comments -> commentAdapter.displayComments(comments))
                .addOnFailureListener(comments -> commentAdapter.clear());

        TextInputLayout commentTextLayout = view.findViewById(R.id.addCommentLayout);
        EditText commentTextBox = commentTextLayout.getEditText();
        dialog.show();

        // Set negative button color
        Button negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        if (negativeButton != null) {
            negativeButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.black));
        }
        dialog.setOnShowListener(dialog1 -> {
            Button commentButton = dialog.findViewById(R.id.addCommentButton);
            commentButton.setOnClickListener(v -> {
                String commentText = commentTextBox.getText().toString();
                Comment newComment = new Comment(firebaseAuthenticationService.getCurrentUser(), commentText);
                moodDatabaseService.addComment(moodId, newComment).addOnSuccessListener(d -> {
                    commentAdapter.add(newComment);
                    commentAdapter.notifyDataSetChanged();
                });

                commentTextBox.setText(null);

            });
        });
        return dialog;

    }

}
