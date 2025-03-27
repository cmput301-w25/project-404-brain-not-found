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
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.Tasks;
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

    public Task<Boolean> parseComment(String comment, String moodId) {
        String[] commentArray = comment.split(" ");
        ArrayList<Task<Boolean>> tasks = new ArrayList<>();
        ArrayList<String> mentionArray = new ArrayList<>();

        for (String word : commentArray) {
            if (word.charAt(0) == '@') {
                String mentionedUser = word.substring(1);
                TaskCompletionSource<Boolean> taskSource = new TaskCompletionSource<>();

                userDatabaseService.userExists(mentionedUser).addOnSuccessListener(exists -> {
                    if (exists) {
                        mentionArray.add(mentionedUser);
                        taskSource.setResult(true);
                    } else {
                        mentionArray.add("");
                        taskSource.setResult(false);
                    }
                }).addOnFailureListener(e -> {
                    mentionArray.add("");
                    taskSource.setResult(false);
                });

                tasks.add(taskSource.getTask());
            }
        }

        return Tasks.whenAll(tasks).continueWith(task -> {
            if (mentionArray.contains("")) {
                return false;
            }
            for (String user : mentionArray) {
                userDatabaseService.addMention(moodId, user);
            }
            return true;
        });
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
                if (!commentText.isEmpty()) {
                    Comment newComment = new Comment(firebaseAuthenticationService.getCurrentUser(), commentText);
                    parseComment(commentText, moodId).addOnSuccessListener(result -> {
                        if (result) {
                            moodDatabaseService.addComment(moodId, newComment).addOnSuccessListener(d -> {
                                moodDatabaseService.getComments(moodId).addOnSuccessListener(comments -> {
                                    commentAdapter.displayComments(comments);
                                });
                            });
                            commentTextBox.setText(null); // Clear the input box
                        } else {
                            commentTextBox.setError("At least one mentioned user does not exist");
                        }
                    }).addOnFailureListener(e -> {
                        System.out.println("Error while parsing comment: " + e.getMessage());
                    });
                }
            });
        });
        return dialog;


}}
